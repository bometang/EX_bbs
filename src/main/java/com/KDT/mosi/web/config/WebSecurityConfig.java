package com.KDT.mosi.web.config;

import com.KDT.mosi.web.login.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final UserDetailsService userDetailsService;
  private final LoginSuccessHandler loginSuccessHandler; // ✅ 커스텀 로그인 성공 핸들러 주입

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form
            .loginPage("/login")                      // 로그인 폼 경로
            .loginProcessingUrl("/login")             // 로그인 처리 경로
            .successHandler(loginSuccessHandler)      // 로그인 성공 핸들러 등록
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/login/logout")               // 로그아웃 처리 경로
            .logoutSuccessUrl("/")                    // 로그아웃 후 이동 경로
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/", "/login/**",
                "/members/join", "/members/join/**",
                "/members/emailCheck", "/members/nicknameCheck",
                "/find/**", "/css/**", "/js/**", "/img/**",
                "/dev/**"
            ).permitAll()
            .requestMatchers("/mypage/seller/**").authenticated()   // ✅ 판매자 마이페이지 허용
            .requestMatchers("/mypage/role/**").authenticated()
            .requestMatchers("/members/*/delete").authenticated()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .accessDeniedPage("/error/403")           // 권한 오류 페이지 지정
        )
        .userDetailsService(userDetailsService);      // 사용자 정보 제공자

    return http.build();
  }
}
