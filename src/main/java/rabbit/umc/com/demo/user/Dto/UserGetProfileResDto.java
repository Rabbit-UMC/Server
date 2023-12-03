package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.community.domain.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class UserGetProfileResDto {

    private String userName;

    private String userProfileImage;

    private String createdAt;

    public UserGetProfileResDto(String userName, String userProfileImage, LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createTime = createdAt.format(formatter);
        this.userName = userName;
        this.userProfileImage = userProfileImage;
        this.createdAt = createTime;
    }
}
