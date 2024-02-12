package rabbit.umc.com.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticateFilter jwtAuthenticateFilter;

//    public WebSecurityConfig(JwtAuthenticateFilter jwtAuthenticateFilter) {
//        this.jwtAuthenticateFilter = jwtAuthenticateFilter;
//    }
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

                .anyRequest().authenticated()
                .and()

//                .exceptionHandling()
//                .accessDeniedHandler(customAccessDeniedHandler);
                .addFilterBefore(jwtAuthenticateFilter,
                        UsernamePasswordAuthenticationFilter.class);
    }
}
