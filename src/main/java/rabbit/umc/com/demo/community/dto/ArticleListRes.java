package rabbit.umc.com.demo.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListRes {

    private String categoryImage;
    private Long mainMissionId;
    private Long categoryHostId;
    List<ArticleDto> articleLists;



    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ArticleDto {

        private Long articleId;
        private String articleTitle;
        private String uploadTime;
        private int likeCount;
        private int commentCount;

    }
}
