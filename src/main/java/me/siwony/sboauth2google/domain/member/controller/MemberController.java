package me.siwony.sboauth2google.domain.member.controller;

import lombok.RequiredArgsConstructor;
import me.siwony.sboauth2google.domain.auth.dto.SessionMember;
import me.siwony.sboauth2google.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    private ResponseEntity<SessionMember> getMyInfo(){
        return ResponseEntity.ok()
                .body(memberService.findMyMemberInfo());
    }
}
