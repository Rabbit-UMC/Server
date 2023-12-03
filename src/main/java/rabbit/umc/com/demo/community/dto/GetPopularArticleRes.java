package rabbit.umc.com.demo.community.dto;

import lombok.*;
import rabbit.umc.com.demo.community.domain.Article;

import java.time.format.DateTimeFormatter;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPopularArticleRes {

    private Long articleId;
    private String articleTitle;
    private String uploadTime;
    private int likeCount;
    private int commentCount;

}
