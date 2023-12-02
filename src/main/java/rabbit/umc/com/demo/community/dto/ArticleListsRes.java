package rabbit.umc.com.demo.community.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;
import rabbit.umc.com.demo.community.domain.Article;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListsRes {

    private String categoryImage;
    private Long mainMissionId;
    private Long categoryHostId;
    List<ArticleListDto> articleLists;



    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ArticleListDto {

        private Long articleId;
        private String articleTitle;
        private String uploadTime;
        private int likeCount;
        private int commentCount;

    }
}
