package rabbit.umc.com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rabbit.umc.com.config.apiPayload.BaseResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;
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
                //인증되지 않은 경우
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
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
                            BaseResponse<Object> baseResponse = new BaseResponse<>(ADMIN_PERMISSION_REQUIRED);
                            responseBody = objectMapper.writeValueAsString(baseResponse);
                        } else if (request.getRequestURI().startsWith("/app/host/")) {
                            // HOST가 아닌 경우
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
