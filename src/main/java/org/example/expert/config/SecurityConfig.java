package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Lv.2-9: Spring Security Config 적용
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil) {
        return new JwtFilter(jwtUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)  // JWT 토큰 인증시 서버가 세션을 저장하지 않으므로 CSRF(Cross-Site Request Forgery) 보호 해제

                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers("/auth/**").permitAll()        // auth : 누구나 접근 가능 (로그인 필요 X)
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // admin : ADMIN 권한 사용자만 접근 가능
                        .anyRequest().authenticated())                    // auth, admin을 제외한 요청 -> authenticated 사용자만 접근 가능

                .httpBasic(AbstractHttpConfigurer::disable)     // 브라우저 팝업 방식 비활성화
                .logout(AbstractHttpConfigurer::disable)        // 세션 로그아웃 기능 비활성화
                .rememberMe(AbstractHttpConfigurer::disable)    // 자동 로그인 쿠키 발급 기능 비활성화
                .formLogin(AbstractHttpConfigurer::disable)     // 토큰 발급이므로 세션 인증을 처리하는 formLogin 비활성화
                .anonymous(AbstractHttpConfigurer::disable)     // JWT 기반 API 에서 인증이 없으면 401로 처리하므로 비활성화

                // exceptionHandling이 필요한 경우 사용
                // .exceptionHandling(exception -> exception
                //         .authenticationEntryPoint(토큰이 없거나 잘못된 처리인 경우)
                //         .accessDeniedHandler(권한이 부족한 경우))

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)     // 로그인 인증 필터 실행 전 JwtFilter 작동
                .build();
    }
}
