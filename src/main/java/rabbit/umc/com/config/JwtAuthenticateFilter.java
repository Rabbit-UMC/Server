package rabbit.umc.com.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rabbit.umc.com.demo.user.UserService;
import rabbit.umc.com.utils.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId = "", token = "";

        try {
            token = jwtService.getJwt(request);
            int userIdx = jwtService.getUserIdx(token);
            userId = String.valueOf(userIdx);

            // 현재 SecurityContextHolder 에 인증객체가 있는지 확인
            System.out.println("user id: "+userId);
            System.out.println("인증객체: "+SecurityContextHolder.getContext().getAuthentication());
            if (userId != "0" /*&& SecurityContextHolder.getContext().getAuthentication() == null*/) {
                UserDetails userDetails = userService.loadUserByUsername(userId);

                // 토큰 유효여부 확인
                log.info("JWT Filter token = {}", token);
                log.info("JWT Filter userDetails = {}", userDetails.getUsername());
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);

        } catch (BaseException exception) {
            log.info(String.valueOf(new BaseResponse<>(exception.getStatus())));
        }
    }

}
