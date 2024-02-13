package rabbit.umc.com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static rabbit.umc.com.config.BaseResponseStatus.*;

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

                .addFilterBefore(jwtAuthenticateFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        // HTTP 응답 상태 코드 설정
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        System.out.println("fail!!!");

                        // JSON 형식의 응답 본문 생성
                        ObjectMapper objectMapper = new ObjectMapper();
                        String responseBody = "fail";

                        // 예외 처리
                        if (request.getRequestURI().startsWith("/app/admin/")) {
                            // /app/admin/**에 대한 권한 부족
                            BaseResponse<Object> baseResponse = new BaseResponse<>(ADMIN_PERMISSION_REQUIRED);
                            responseBody = objectMapper.writeValueAsString(baseResponse);
                        } else if (request.getRequestURI().startsWith("/app/host/")) {
                            // /app/host/**에 대한 권한 부족
                            BaseResponse<Object> baseResponse = new BaseResponse<>(HOST_PERMISSION_REQUIRED);
                            responseBody = objectMapper.writeValueAsString(baseResponse);
                            System.out.println("요기");
                        } else {
                            // 로그인 안된 경우
                            BaseResponse<Object> baseResponse = new BaseResponse<>(UNAUTHORIZED_USER);
                            responseBody = objectMapper.writeValueAsString(baseResponse);
                        }

                        // 응답 본문을 HTTP 응답에 쓰고 전송
                        response.getWriter().write(responseBody);
                        response.getWriter().flush();
                    }
                });

    }
}
