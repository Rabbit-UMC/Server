package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class ArticleListRes {

    private Long id;
    private String articleTitle;
    private Timestamp uploadTime;
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
