package rabbit.umc.com.demo.user.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailAuthenticationDto {
    private Long userId;
    private String authenticationCode;
    private String enteredCode;
}
