package com.example.myShop.config;

import com.example.myShop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberService memberService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 해시 함수로 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/members/login")               // 커스텀 로그인 페이지
                .defaultSuccessUrl("/")                    // 로그인 성공 시 이동 경로
                .usernameParameter("email")                // 로그인 요청 시 사용할 파라미터명
                .failureUrl("/members/login/error")        // 로그인 실패 시 이동 경로
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃 URL
                .logoutSuccessUrl("/");                   // 로그아웃 성공 시 이동 경로
        http.authorizeRequests() // 시큐리티 처리에 HttpServletRequest를 이용
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated(); // 앞서 설정해준 경로를 제외한 나머지 경로는 인증을 요구
        http.exceptionHandling() // 인증되지 않은 사용자가 리소스 접근 시 수행되는 핸들러 등록
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(memberService)             // 사용자 인증을 위한 서비스
                .passwordEncoder(passwordEncoder());           // 비밀번호 암호화 방식 설정
    }

    @Override
    public void configure(WebSecurity web) throws Exception { // static 디렉토리 하위파일은 인증 무시
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }
}