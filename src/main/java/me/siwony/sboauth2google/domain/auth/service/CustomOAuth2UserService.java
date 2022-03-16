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

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegateOAuth2UserService;

    @Autowired
    public CustomOAuth2UserService(final AuthService authService, final MemberRepository memberRepository) {
        this.authService = authService;
        this.memberRepository = memberRepository;
        this.delegateOAuth2UserService = new DefaultOAuth2UserService();
    }

    public CustomOAuth2UserService(
            final AuthService authService,
            final MemberRepository memberRepository,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegateOAuth2UserService
    ) {
        this.authService = authService;
        this.memberRepository = memberRepository;
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

        if(isFirst(attributes.getEmail()))
            authService.join(attributes);

        final Member member = memberRepository.findByEmail(attributes.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("OAuth2회원가입이 정상적으로 발생하지 않음"));
        authService.login(member, attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private boolean isFirst(String email){
        return !memberRepository.existsByEmail(email);
    }
}
