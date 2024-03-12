package rabbit.umc.com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rabbit.umc.com.config.apiPayload.BaseResponse;
import rabbit.umc.com.demo.user.service.UserService;
import rabbit.umc.com.utils.JwtService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtService jwtService;

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
    public void configure(WebSecurity web) throws Exception {
        //login 없이 허용
        web.ignoring()
                .antMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/app/users/kakao-login",
                        "/app/users/kakao-login-web",
                        "/app/users/sign-up",
                        "/app/users/checkDuplication",
                        "/app/users/kakao-disconnect",
                        "/app/users/isValid",
                        "/app/users/reissue");
    }

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
                .antMatchers("/app/admin/**").hasRole("ADMIN")
                .antMatchers("/app/host/**").hasRole("HOST")

                .anyRequest().authenticated()
                .and()

                .addFilterBefore(new JwtAuthenticateFilter(userService, jwtService), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                //인증되지 않은 경우
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        log.warn("로그인되지 않은 회원입니다.");
                        BaseResponse<?> baseResponse = new BaseResponse<>(UNAUTHORIZED_USER);

                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
                    }
                })
                //인증되었으나 권한이 없는 경우
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String responseBody = "";

                        if (request.getRequestURI().startsWith("/app/admin/")) {
                            // ADMIN이 아닌 경우
                            log.warn("ADMIN 회원만 접근할 수 있습니다.");
                            BaseResponse<Object> baseResponse = new BaseResponse<>(ADMIN_PERMISSION_REQUIRED);
                            responseBody = objectMapper.writeValueAsString(baseResponse);
                        } else if (request.getRequestURI().startsWith("/app/host/")) {
                            // HOST가 아닌 경우
                            log.warn("HOST 회원만 접근할 수 있습니다.");
                            BaseResponse<Object> baseResponse = new BaseResponse<>(HOST_PERMISSION_REQUIRED);
                            responseBody = objectMapper.writeValueAsString(baseResponse);
                        }

                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding("UTF-8");

                        response.getWriter().write(responseBody);
                        response.getWriter().flush();
                    }
                });

    }
}
