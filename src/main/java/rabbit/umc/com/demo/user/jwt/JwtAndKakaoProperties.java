package rabbit.umc.com.demo.user.jwt;

public interface JwtAndKakaoProperties {

    int EXPIRATION_TIME =  864000000;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String Client_Secret = "hOtv6TIzsNeXEzZyRNS42DvMNUcqFLeu"; //카카오 시크릿키
    String Admin = "c358d78a8721722584703228f2b04dd2"; //카카오 어드민키

}
