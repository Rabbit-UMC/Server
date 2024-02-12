package rabbit.umc.com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static rabbit.umc.com.config.BaseResponseStatus.*;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
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
}
