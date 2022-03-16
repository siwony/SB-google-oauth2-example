package me.siwony.sboauth2google.domain.auth.service;


import lombok.RequiredArgsConstructor;
import me.siwony.sboauth2google.domain.auth.dto.OAuthAttributes;
import me.siwony.sboauth2google.domain.auth.dto.SessionMember;
import me.siwony.sboauth2google.domain.member.entity.Member;
import me.siwony.sboauth2google.domain.member.entity.MemberRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    public Member join(OAuthAttributes oAuthAttributes){
        return memberRepository.save(oAuthAttributes.toEntity());
    }

    public SessionMember login(Member member, OAuthAttributes oAuthAttributes){
        updateMemberInfo(member, oAuthAttributes);
        final SessionMember sessionMember = new SessionMember(member);
        httpSession.setAttribute("member", sessionMember);
        return sessionMember;
    }

    /**
     * 유저의 OAuth 정보가 변경되면 update하는 메서드
     */

    private void updateMemberInfo(Member member, OAuthAttributes oAuthAttributes){
        final Member updateMember = member.update(oAuthAttributes.getName(), oAuthAttributes.getPicture());
        memberRepository.save(updateMember);
    }

}
