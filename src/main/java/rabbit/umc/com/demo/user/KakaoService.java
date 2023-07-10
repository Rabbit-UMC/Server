package rabbit.umc.com.demo.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.KakaoDto;
import rabbit.umc.com.demo.user.jwt.JwtProperties;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static rabbit.umc.com.config.BaseResponseStatus.FAILED_TO_AUTHENTICATION;
import static rabbit.umc.com.demo.Status.ACTIVE;
import static rabbit.umc.com.demo.user.Domain.UserPermision.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final UserRepository userRepository;

    //카카오 로그인
    public User kakaoLogin(/*String code, HttpServletResponse response*/String accessToken) throws JsonProcessingException {
        //인가 코드로 액세스 토큰 요청
        //String accessToken = getAccessToken(code);

        //토큰으로 카카오 API 호출
        KakaoDto kakaoDto = findProfile(accessToken);

        //카카오ID로 회원가입 처리
        User user = saveUser(kakaoDto);

        return user;
    }

    /*
    카카오 엑세스 토큰 얻기
     */

    public String getAccessToken(String code) throws IOException, BaseException {
        String accessToken="";

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "4d27e2c3e437fa46e403f80e72efe932"); //나중에 묘집사 애플리케이션 id로 바꾸기
        body.add("client_secret", JwtProperties.Client_Secret); //시크릿 키도 나중에 바꾸기
        body.add("redirect_uri", "http://localhost:8080/app/users/kakao-login"); //리다이렉스 uri도 나중에 바꾸기
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 상태 코드 가져오기
        int responseCode = response.getStatusCodeValue();
        log.info("getAccessToken response code: {}", responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK){
            // HTTP 응답 (JSON) -> 액세스 토큰 파싱
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            accessToken= jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.get("refresh_token").asText();
            long expires_in = jsonNode.get("expires_in").asLong();
            long refresh_token_expires_in = jsonNode.get("refresh_token_expires_in").asLong();
        }else{
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String error= jsonNode.get("error").asText();
            String error_code= jsonNode.get("error_code").asText();
            String error_description= jsonNode.get("error_description").asText();

            log.error("error: {} ", error);
            log.error("error_code: {} ", error_code);
            log.error("error_Description: {} ", error_description);

            if (error_code.equals("KOE320")) { // authorize_code not found
                //throw new AuthorizationCodeNotFoundException(error_description);
                throw new BaseException(FAILED_TO_AUTHENTICATION);
            }else if(error_code.equals("KOE303")){ // Redirect URI mismatch.
                throw new RuntimeException("Redirect URI mismatch");
            }else if(error_code.equals("KOE101")){ // Not exist client_id
                throw new RuntimeException("Not exist client_id");
            }

        }

        //토큰 유효성 검증
        validateToken(accessToken);
        return accessToken;
//        return validateToken(accessToken);
    }

    /*
    토큰 유효성 검증(토큰 정보보기)
    */

    private boolean validateToken(String /*access_token*/accessToken) throws IOException, BaseException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/access_token_info",
                HttpMethod.GET,
                //kakaoTokenRequest,
                entity,
                String.class
        );

        // HTTP 응답 상태 코드 가져오기
        int responseCode = response.getStatusCodeValue();

        if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK

            // HTTP 응답 (JSON) -> 액세스 토큰 파싱
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Long id= jsonNode.get("id").asLong();
            Integer expires_in = jsonNode.get("expires_in").asInt();

            return true;
        } else { // not 200

            // HTTP 응답 (JSON) -> 액세스 토큰 파싱
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            int code= jsonNode.get("code").asInt();
            String msg = jsonNode.get("msg").asText();

            if(code == -401){
                throw new BaseException(FAILED_TO_AUTHENTICATION);
            }else {
                throw new RuntimeException(msg);
            }
        }
    }


    // 토큰으로 카카오 API 호출
    private KakaoDto findProfile(String accessToken) throws JsonProcessingException {
        KakaoDto kakaoDto = new KakaoDto();
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        System.out.println("responsesBody: " + jsonNode);

        //kakao_id, 프로필 이미지 파싱
        Long kakao_id = jsonNode.get("id").asLong();
        String profile_image = jsonNode.get("kakao_account").get("profile").get("thumbnail_image_url").asText();
        //안들어올 수도 있는 정보
        boolean hasAgeRange = jsonNode.get("kakao_account").get("has_age_range").asBoolean();
        boolean hasBirthday = jsonNode.get("kakao_account").get("has_birthday").asBoolean();
        boolean hasGender = jsonNode.get("kakao_account").get("has_gender").asBoolean();

        System.out.println("kakao_id: " + kakao_id);
        System.out.println("profile_image: " + profile_image);
        kakaoDto.setKakaoId(kakao_id);
        kakaoDto.setUserProfileImage(profile_image);

        if (hasAgeRange) {
            String ageRange = jsonNode.get("kakao_account").get("age_range").asText();
            kakaoDto.setAgeRange(ageRange);
            System.out.println("AgeRange: " + ageRange);
        } else {
            kakaoDto.setAgeRange(null);
        }

        if (hasBirthday) {
            String birthday = jsonNode.get("kakao_account").get("birthday").asText();
            kakaoDto.setBirthday(birthday);
            System.out.println("birthday: " + birthday);
        } else {
            kakaoDto.setBirthday(null);
        }

        if (hasGender) {
            String gender = jsonNode.get("kakao_account").get("gender").asText();
            kakaoDto.setAgeRange(gender);
            System.out.println("Gender: " + gender);
        } else {
            kakaoDto.setGender(null);
        }

        return kakaoDto;
    }

    //
    public User saveUser(KakaoDto kakaoDto) {

        //같은 카카오 아이디있는지 확인
        boolean isUser = userRepository.existsByKakaoId(kakaoDto.getKakaoId());
        System.out.println("회원, 비회원 확인 완료");

        User user=null;

        //회원이 아닌 경우
        //회원가입 진행(이메일, 닉네임 제외 모두)
        if(!isUser){
            user = new User(kakaoDto.getKakaoId(), kakaoDto.getUserProfileImage(), USER, kakaoDto.getAgeRange(),
                    kakaoDto.getGender(), kakaoDto.getBirthday(), ACTIVE);
            userRepository.save(user);
        }

        //회원인 경우, 회원 조회
        else{
            //kakao_id로 user 객체 조회
            user = userRepository.findByKakaoId(kakaoDto.getKakaoId());
        }

        //로그인 처리하기
//        Jwt jwt = tokenService.createTokens(user.getId(), user.getUserName(), kakaoAccessToken); // 세션 저장 대신 JWT 토큰 사용
//
//        // 리프레쉬 토큰 httpOnly 쿠키 설정
//        long maxAge = (jwt.getRefreshTokenExp().getTime() - System.currentTimeMillis()) / 1000;
//        Cookie refreshTokenCookie = setCookie("refreshToken", jwt.refreshToken, true, false, (int) maxAge, "/");
//        response.addCookie(refreshTokenCookie);
//
//        CreateTokenResponse createTokenResponse = new CreateTokenResponse(jwt.accessToken, jwt.refreshToken, jwt.accessTokenExp, jwt.refreshTokenExp);

        return user;
    }
    //쿠키 설정
    public static Cookie setCookie(String cookieName, String value, boolean isHttpOnly, boolean isSecure, int maxAge, String path) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setSecure(isSecure); // HTTPS 프로토콜에서만 쿠키 전송 가능
        return cookie;
    }

//    public String createToken(User user) {
//        // 서명 키 생성
//        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

//        //String str_expirationTime = String.valueOf(JwtProperties.EXPIRATION_TIME);

//
//        String jwtToken = Jwts.builder()
//                .setSubject(String.valueOf(user.getId()))
//                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
//                .claim("kakao_id", user.getKakaoId())
//                .claim("user_permission", user.getUserPermission())
//                //.claim("expriration_time", str_expirationTime);

//                .claim("created_at", String.valueOf(user.getCreatedAt()))
//                .signWith(secretKey)
//                .compact();
//
//        return jwtToken;
//    }


//    public String createJwt(int userIdx){
//        Date now = new Date();
//        return Jwts.builder()
//                .setHeaderParam("type","jwt")
//                .claim("userIdx",userIdx)
//                .setIssuedAt(now)
//                .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
//                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
//                .compact();
//    }

    //카카오 로그아웃
    public void logout(String access_Token) throws IOException {
        String reqURL = "https://kapi.kakao.com/v1/user/logout";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //카카오 연결 끊기
    public void unlink(String access_Token) {
        String reqURL = "https://kapi.kakao.com/v1/user/unlink";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
