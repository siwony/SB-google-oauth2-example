package me.siwony.sboauth2google.global.security;

import lombok.RequiredArgsConstructor;
import me.siwony.sboauth2google.domain.auth.service.CustomOAuth2UserService;
import me.siwony.sboauth2google.domain.member.entity.Member;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable();

        http
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/images/**",
                        "/js/**", "/h2-console/**").permitAll()
                .antMatchers("/api/v1/**")
                    .hasRole(Member.Role.CLIENT.name())
                .anyRequest().authenticated();

        http
                .logout()
                .logoutSuccessUrl("/");

        http
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);
    }


}
