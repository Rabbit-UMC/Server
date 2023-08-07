package rabbit.umc.com.utils;



import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.secret.Secret;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static rabbit.umc.com.config.BaseResponseStatus.*;


@Service
public class JwtService {

    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createJwt(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    // jwt refresh 토큰 생성
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println("request: "+request);
        System.out.println("access token: "+request.getHeader("X-ACCESS-TOKEN"));
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserIdx() throws BaseException {
        //1. JWT 추출
        String accessToken = getJwt();
        System.out.println("accessToken = " + accessToken);
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e){
            throw new BaseException(EXPIRED_JWT_ACCESS);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Integer.class);
    }

//    public int getUserIdByCookie (String jwtToken)throws BaseException {
//        Jws<Claims> claims;
//        try{
//            claims = Jwts.parser()
//                    .setSigningKey(Secret.JWT_SECRET_KEY)
//                    .parseClaimsJws(jwtToken);
//        } catch (Exception ignored) {
//            throw new BaseException(INVALID_JWT);
//        }
//
//        // 3. userIdx 추출
//        return claims.getBody().get("userIdx",Integer.class);
//    }

    /**
     * 토큰의 Claim 디코딩
     */
    private Claims getAllClaims(String token) {
        //log.info("getAllClaims token = {}", token);
        return Jwts.parser()
                .setSigningKey(Secret.JWT_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Claim 에서 user id 가져오기
     * 만료된 access token에서 user id 가져올 때 필요
     */
    public Long getUserIdFromToken(String token) {
        Long userId = getAllClaims(token).get("userIdx",Long.class);
        //log.info("getUsernameFormToken subject = {}", username);
        return userId;
    }

    /**
     * 토큰 만료기한 가져오기
     */
    public Date getExpirationDate(String token) {
        Claims claims = getAllClaims(token);
        return claims.getExpiration();
    }

    public String invalidateToken(String accessToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(Secret.JWT_SECRET_KEY).parseClaimsJws(accessToken);

        // 현재 시간으로부터 유효기간이 0초인 토큰 생성
        String invalidatedToken = Jwts.builder()
                .setClaims(claims.getBody())
                .setIssuedAt(new Date())
                .setExpiration(new Date())
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();

        return invalidatedToken;
    }

}
