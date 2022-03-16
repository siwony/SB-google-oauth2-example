package me.siwony.sboauth2google.domain.auth.dto;


import lombok.Getter;
import me.siwony.sboauth2google.domain.member.entity.Member;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;

/**
 * 인증된 회원의 정보를 저장하는 DTO
 */
@Getter
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionMember implements Serializable {

    private final String name;
    private final String email;
    private final String picture;

    public SessionMember(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.picture = member.getPicture();
    }
}
