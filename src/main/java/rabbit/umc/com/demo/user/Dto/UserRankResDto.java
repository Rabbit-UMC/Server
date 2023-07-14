package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRankResDto {
    private Long categoryId;
    private Long userId;
    private Long rank;
}
