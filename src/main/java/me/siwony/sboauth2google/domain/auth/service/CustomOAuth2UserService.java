package me.siwony.sboauth2google.domain.auth.service;

import lombok.RequiredArgsConstructor;
import me.siwony.sboauth2google.domain.auth.dto.OAuthAttributes;
import me.siwony.sboauth2google.domain.auth.dto.SessionMember;
import me.siwony.sboauth2google.domain.member.entity.Member;
import me.siwony.sboauth2google.domain.member.entity.MemberRepository;
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

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        final OAuth2User oAuth2User = delegate.loadUser(userRequest);

        final String registrationId = userRequest.getClientRegistration().getRegistrationId(); // OAuth 밴더를 구별하는 상수
        final String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

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

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member user = memberRepository.findByEmail(attributes.getEmail())
                .map(entity ->
                        entity.update(attributes.getName(), attributes.getPicture())
                ).orElse(attributes.toEntity());

        return memberRepository.save(user);
    }
}
