package rabbit.umc.com.demo.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.HttpURLConnection;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.Status.ACTIVE;
import static rabbit.umc.com.demo.user.Domain.UserPermission.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    @Value("${kakao-client-id}")
    private String kakao_client_id;

    @Value("${kakao-admin-key}")
    private String kakao_admin_key;

    @Value("${kakao-secret-key}")
    private String kakao_secret_key;

    private final UserRepository userRepository;
    private final UserService userService;

    //카카오 엑세스 토큰 얻기
    public String getAccessToken(String code) throws IOException, BaseException {
        String accessToken="";
        if (code == null) {
            log.info("인증 코드가 존재하지 않습니다.");
            throw new BaseException(FAILED_TO_AUTHENTICATION);
        }

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakao_client_id);
        body.add("client_secret", kakao_secret_key);
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
        }else{
            log.info("요청에 실패하였습니다");
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String error= jsonNode.get("error").asText();
            String error_code= jsonNode.get("error_code").asText();
            String error_description= jsonNode.get("error_description").asText();

            log.error("error: {} ", error);
            log.error("error_code: {} ", error_code);
            log.error("error_Description: {} ", error_description);

            if (error_code.equals("KOE320")) {
                log.info("인가 코드를 새로 발급한 후, 다시 엑세스 엑세스 토큰을 요청해주세요.");
                throw new BaseException(FAILED_TO_AUTHENTICATION);
            }else if(error_code.equals("KOE303")){
                log.info("인가 코드 요청시 사용한 redirect_uri와 액세스 토큰 요청 시 사용한 redirect_uri가 다릅니다.");
                throw new RuntimeException("Redirect URI mismatch");
            }else if(error_code.equals("KOE101")){
                log.info("잘못된 앱 키 타입을 사용하거나 앱 키에 오타가 있는 것 같습니다.");
                throw new RuntimeException("Not exist client_id");
            }

        }
        return accessToken;
    }

    // 토큰으로 카카오 API 호출
    @Transactional
    public KakaoDto findProfile(String accessToken) throws JsonProcessingException {
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

        kakaoDto.setKakaoId(kakao_id);
        kakaoDto.setUserProfileImage(profile_image);

        if (hasAgeRange) {
            String ageRange = jsonNode.get("kakao_account").get("age_range").asText();
            kakaoDto.setAgeRange(ageRange);
        } else {
            kakaoDto.setAgeRange(null);
        }

        if (hasBirthday) {
            String birthday = jsonNode.get("kakao_account").get("birthday").asText();
            kakaoDto.setBirthday(birthday);
        } else {
            kakaoDto.setBirthday(null);
        }

        if (hasGender) {
            String gender = jsonNode.get("kakao_account").get("gender").asText();
            kakaoDto.setGender(gender);
        } else {
            kakaoDto.setGender(null);
        }

        return kakaoDto;
    }

    //유저 회원가입 or 로그인
    public User saveUser(KakaoDto kakaoDto) throws BaseException {
        User user = new User();

        boolean isUser = userRepository.existsByKakaoId(kakaoDto.getKakaoId());

        //회원이 아닌 경우
        //회원가입 진행(이메일, 닉네임 제외 모두)
        if(!isUser){
            throw new BaseException(USER_NOT_FOUND);
        }

        //회원인 경우, 회원 조회
        else{
            log.info("로그인을 진행하겠습니다.");
            user.setStatus(ACTIVE);
            //userRepository.save(user);

            user = userRepository.findByKakaoId(kakaoDto.getKakaoId());
        }
        return user;
    }

    public User signUpUser(String userName, KakaoDto kakaoDto) throws BaseException {

        log.info("회원 가입을 진행하겠습니다.");

        User user = new User(kakaoDto.getKakaoId(), userName,kakaoDto.getUserProfileImage(), USER, kakaoDto.getAgeRange(),
        kakaoDto.getGender(), kakaoDto.getBirthday(), ACTIVE);

        userRepository.save(user);
        return user;
    }

    public boolean existsUser(Long kakaoId){
        return userRepository.existsByKakaoId(kakaoId);
    }

    //카카오 로그아웃
    public Long logout(Long kakaoId) throws IOException, BaseException {
        //String adminKey= JwtAndKakaoProperties.Admin;

        String str_kakaoId = String.valueOf(kakaoId);

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK "+kakao_admin_key);

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", str_kakaoId); //로그아웃할 회원의 kakaoId

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/logout",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 상태 코드 가져오기
        int responseCode = response.getStatusCodeValue();
        log.info("getAccessToken response code: {}", responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK) {
            // HTTP 응답 (JSON) -> 액세스 토큰 파싱
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            kakaoId = jsonNode.get("id").asLong();
        }
        else{
            log.info("서버 응답 오류");
            throw new BaseException(SERVER_ERROR);
        }

        return kakaoId;
    }

    //카카오 연결끊기
    public Long unlink(Long kakaoId) throws IOException, BaseException {
        //String adminKey= JwtAndKakaoProperties.Admin;

        String str_kakaoId = String.valueOf(kakaoId);

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK "+kakao_admin_key);

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", str_kakaoId); //로그아웃할 회원의 kakaoId

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 상태 코드 가져오기
        int responseCode = response.getStatusCodeValue();
        log.info("getAccessToken response code: {}", responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK) {
            // HTTP 응답 (JSON) -> 액세스 토큰 파싱
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            kakaoId = jsonNode.get("id").asLong();
        }
        else{
            log.info("서버 응답 오류");
            throw new BaseException(SERVER_ERROR);
        }

        return kakaoId;
    }

}