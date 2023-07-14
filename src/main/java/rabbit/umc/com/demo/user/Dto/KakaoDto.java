package rabbit.umc.com.demo.user.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoDto {
    private Long kakaoId;
    private String userProfileImage;
    private String ageRange;
    private String gender;
    private String birthday;
}
