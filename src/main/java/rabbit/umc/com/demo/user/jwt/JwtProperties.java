package rabbit.umc.com.demo.user.jwt;

import rabbit.umc.com.config.secret.Secret;

public interface JwtProperties {

    int EXPIRATION_TIME =  864000000;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String Client_Secret = "IFYpUrEsiVGkAWJ62u7cjISlIZON4bsR";
}
