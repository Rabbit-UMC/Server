package rabbit.umc.com.demo.user.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEmailNicknameReqDto {
    private Long id;
    private String userEmail;
    private String userName;
    /*UserEmailNicknameReqDto(Long id, String userEmail, String userName){
        this.id=id;
        this.userEmail= userEmail;
        this.userName=userName;
    }*/
}
