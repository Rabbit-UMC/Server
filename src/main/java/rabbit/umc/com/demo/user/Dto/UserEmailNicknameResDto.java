package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailNicknameResDto {
    private Long id;
    private String userEmail;
    private String userName;
}
