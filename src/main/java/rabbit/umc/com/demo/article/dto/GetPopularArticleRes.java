package rabbit.umc.com.demo.article.dto;

import lombok.*;
import rabbit.umc.com.demo.article.domain.Article;

import java.time.format.DateTimeFormatter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPopularArticleRes {

    private Long articleId;
    private String articleTitle;
    private String uploadTime;
    private int likeCount;
    private int commentCount;

    public static GetPopularArticleRes toPopularArticle(Article article){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String uploadTime = article.getCreatedAt().format(formatter);

        return new GetPopularArticleRes(
                article.getId(),
                article.getTitle(),
                uploadTime,
                article.getLikeArticles().size(),
                article.getComments().size()
        );
    }

}
