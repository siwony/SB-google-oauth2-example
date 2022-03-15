package me.siwony.sboauth2google.domain.member.entity;

import lombok.*;

import javax.persistence.*;

@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
@Entity
public class Member {

    @Getter
    @RequiredArgsConstructor
    public enum Role{
        ADMIN("ROLE_ADMIN", "관리자"),
        CLIENT("ROLE_CLIENT", "일반 사용자");

        private final String key;
        private final String title;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column
    private String picture;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public Member update(final String name, final String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }
}
