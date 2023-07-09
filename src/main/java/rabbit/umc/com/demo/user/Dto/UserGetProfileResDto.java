package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserGetProfileResDto {
    private String userEmail;

    private String userName;

    private String userProfileImage;
}
