package me.siwony.sboauth2google.domain.member.service;

import lombok.RequiredArgsConstructor;
import me.siwony.sboauth2google.domain.auth.dto.SessionMember;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final HttpSession httpSession;

    public SessionMember findMyMemberInfo(){
        return (SessionMember) httpSession.getAttribute("member");
    }
}
