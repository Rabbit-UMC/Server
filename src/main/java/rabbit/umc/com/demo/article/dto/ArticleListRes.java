package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ArticleListRes {

    private Long articleId;
    private String articleTitle;
    private LocalDateTime uploadTime;
    private int likeCount;
    private int commentCount;

    public static ArticleListRes toArticleListRes(Article article){
        return new ArticleListRes(
                article.getId(),
                article.getTitle(),
                article.getUpdatedAt(),
                article.getLikeArticles().size(),
                article.getComments().size());
    }
}
