package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLoginResDto {
    private long id;
    private String jwtAccessToken;
    private String jwtRefreshToken;
}
