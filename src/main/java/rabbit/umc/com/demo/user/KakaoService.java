package rabbit.umc.com.demo.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.KakaoDto;
import rabbit.umc.com.demo.user.jwt.JwtProperties;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static rabbit.umc.com.demo.Status.ACTIVE;
import static rabbit.umc.com.demo.user.Domain.UserPermision.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    @Autowired
    UserRepository userRepository;

    public User kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        //인가 코드로 액세스 토큰 요청
        String accessToken = getAccessToken(code);

        //토큰으로 카카오 API 호출
        KakaoDto kakaoDto = findProfile(accessToken);

        //카카오ID로 회원가입 처리
        User user = saveUser(kakaoDto);

        return user;
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "c160f1524ce053f21f2d2a4195a0ed0b"); //나주에 묘집사 애플리케이션 id로 바꾸기
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

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        //refreshToken = (String) jsonObj.get("refresh_token"); //필요하면?
        return jsonNode.get("access_token").asText();

    }

    // 2. 토큰으로 카카오 API 호출
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

    public User saveUser(KakaoDto kakaoDto) {

        //같은 카카오 아이디있는지 확인
        Optional<User> optionalUser = userRepository.findById(kakaoDto.getKakaoId());
        System.out.println("카카오 아이디 확인 완료");


        User user = null;
        if (!optionalUser.isPresent()) { //회원가입 진행(이메일, 닉네임 제외 모두)
            Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());

            user = new User(kakaoDto.getKakaoId(), kakaoDto.getUserProfileImage(), USER, kakaoDto.getAgeRange(),
                    kakaoDto.getGender(), kakaoDto.getBirthday(), ACTIVE, currentTimestamp, currentTimestamp);
            userRepository.save(user);
        }
        return user;
    }

    /*public String createToken(User user) {
        // 서명 키 생성
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        //String str_expirationTime = String.valueOf(JwtProperties.EXPIRATION_TIME);

        String jwtToken = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .claim("kakao_id", user.getKakaoId())
                .claim("user_permission", user.getUserPermission())
                //.claim("expriration_time", str_expirationTime);
                .claim("created_at", String.valueOf(user.getCreatedAt()))
                .signWith(secretKey)
                .compact();

        return jwtToken;
    }*/

}
