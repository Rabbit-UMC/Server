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
    /*public KakaoDto(Long kakao_id, String profile_image) {
        this.kakaoId = kakao_id;
        this.userProfileImage = profile_image;
    }*/
}
