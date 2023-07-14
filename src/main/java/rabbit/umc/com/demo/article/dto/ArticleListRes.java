package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ArticleListRes {

    private Long articleId;
    private String articleTitle;
    private String uploadTime;
    private int likeCount;
    private int commentCount;

    public static ArticleListRes toArticleListRes(Article article){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String uploadTime = article.getCreatedAt().format(formatter);
        return new ArticleListRes(
                article.getId(),
                article.getTitle(),
                uploadTime,
                article.getLikeArticles().size(),
                article.getComments().size());
    }
}
