package rabbit.umc.com.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.user.UserService;
import rabbit.umc.com.utils.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtService.getJwt(request);
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Request Protocol: " + request.getProtocol());
        System.out.println("Request Parameters: " + request.getParameterMap());
        System.out.println("Request Headers: " + Collections.list(request.getHeaderNames()));
        System.out.println("Request Remote Address: " + request.getRemoteAddr());
        System.out.println("Request Remote Host: " + request.getRemoteHost());
        System.out.println("Request Remote Port: " + request.getRemotePort());

        log.info("token: {}", token);
        int userIdx = jwtService.getUserIdx(token);
        String userId = String.valueOf(userIdx);

        if(token != null && token.length() != 0){
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = userService.loadUserByUsername(userId);
            Collection<? extends GrantedAuthority> userDetailsAuthorities = userDetails.getAuthorities();

            if (SecurityContextHolder.getContext().getAuthentication() == null || !existingAuth.getAuthorities().equals(userDetailsAuthorities)) {
                log.info("인증 객체 생성");

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetailsAuthorities);

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        log.info("실행");
        filterChain.doFilter(request, response);

    }

}
