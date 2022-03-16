package me.siwony.sboauth2google.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.siwony.sboauth2google.domain.auth.dto.OAuthAttributes;
import me.siwony.sboauth2google.domain.auth.dto.SessionMember;
import me.siwony.sboauth2google.domain.member.entity.Member;
import me.siwony.sboauth2google.domain.member.entity.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegateOAuth2UserService;

    @Autowired
    public CustomOAuth2UserService(final MemberRepository memberRepository, final HttpSession httpSession) {
        this.memberRepository = memberRepository;
        this.httpSession = httpSession;
        this.delegateOAuth2UserService = new DefaultOAuth2UserService();
    }

    public CustomOAuth2UserService(
            final MemberRepository memberRepository,
            final HttpSession httpSession,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegateOAuth2UserService
    ) {
        this.memberRepository = memberRepository;
        this.httpSession = httpSession;
        this.delegateOAuth2UserService = delegateOAuth2UserService;
    }

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = delegateOAuth2UserService.loadUser(userRequest); // DefaultOAuth2UserService가 대신 OAuth로 로그인한 유저 정보를 가져온다.
        final String registrationId = userRequest.getClientRegistration().getRegistrationId(); // OAuth 밴더를 구별하는 상수
        final String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        log.debug("registrationId = '{}'", registrationId);
        log.debug("userNameAttributeName = '{}'", userNameAttributeName);
        log.debug("oAuth2User.getAttributes = '{}'", Collections.unmodifiableMap(oAuth2User.getAttributes()));

        final OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
        );

        final Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("member", new SessionMember(member)); // 세션에 회원 정보 등록

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    /**
     * OAuth 로그인을 하면 무조건 save를 하고 만약 정보가 변경되면 update를 진행한다.
     */
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member user = memberRepository.findByEmail(attributes.getEmail())
                .map(entity ->
                        entity.update(attributes.getName(), attributes.getPicture())
                ).orElse(attributes.toEntity());

        return memberRepository.save(user);
    }
}
