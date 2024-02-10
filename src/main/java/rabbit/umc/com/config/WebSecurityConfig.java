package rabbit.umc.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                // Spring Boot에서 설정한 CORS (Cross-Origin Resource Sharing) 설정을 따른다 (config/WebConfig.java 설정 확인)
//                .cors()
//                .and()
//                // CSRF (Cross-Site Request Forgery) 방지를 해제한다
//                // Session/Cookie의 취약점을 이용하기 때문에 템플릿처럼 OAuth2, JWT 기반일 경우는 비활성화해도 무관하다.
//                .csrf().disable()
//                .build();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http

                // Spring Boot에서 설정한 CORS (Cross-Origin Resource Sharing) 설정을 따른다 (config/WebConfig.java 설정 확인)
                .cors()
                .and()
                // CSRF (Cross-Site Request Forgery) 방지를 해제한다
                // Session/Cookie의 취약점을 이용하기 때문에 템플릿처럼 OAuth2, JWT 기반일 경우는 비활성화해도 무관하다.
                .csrf().disable()

                .authorizeRequests()
                // login 없이 허용
                .antMatchers("/app/users/kakao-login").permitAll()
                .antMatchers("/app/users/sign-up").permitAll()
                .antMatchers("/app/users/checkDuplication").permitAll()

                .antMatchers("/app/admin/**").hasRole("ADMIN")
                .antMatchers("/app/host/**").hasRole("HOST")

                // 그 외 어떤 요청이든 '인증'과정 필요
                .anyRequest().authenticated();

    }
}
