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
import rabbit.umc.com.demo.user.service.UserService;
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
//@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtService.getJwt(request);

        log.info("token: {}", token);
        int userIdx = jwtService.getUserIdx(token); //쿼리1
        String userId = String.valueOf(userIdx);

        if (token != null && token.length() != 0) {
            //로그아웃 또는 회원 탈퇴한 유저인지 확인
            if (userService.isUserValid((long) userIdx)) { //유저의 status가 ACTIVE, PENDING인 경우
                log.info("유저의 status가 ACTIVE, PENDING입니다.");
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                UserDetails userDetails = userService.loadUserByUsername(userId); //쿼리 2
                Collection<? extends GrantedAuthority> userDetailsAuthorities = userDetails.getAuthorities();

                if (SecurityContextHolder.getContext().getAuthentication() == null || !existingAuth.getAuthorities().equals(userDetailsAuthorities)) {
                    log.info("인증 객체 생성");

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetailsAuthorities);

                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } else { //유저의 status가 INACTIVE, LOGGED_OUT인 경우
                log.info("유저가 로그아웃이나 탈퇴 상태입니다.");
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } else {
            log.info("인증이 필요없는 API입니다.");
        }
        filterChain.doFilter(request, response);


    }
}
