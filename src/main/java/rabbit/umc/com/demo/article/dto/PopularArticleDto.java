package rabbit.umc.com.demo.article.dto;

import lombok.*;
import rabbit.umc.com.demo.article.domain.Article;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class PopularArticleDto {
    private Long articleId;
    private String articleTitle;
    private String uploadTime;
    private int likeCount;
    private int commentCount;

//    public PopularArticleDto(Long articleId, String articleTitle, LocalDateTime uploadTime, Long likeCount, Long commentCount){
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        String time = uploadTime.format(formatter);
//
//        this.articleId = articleId;
//        this.articleTitle =articleTitle;
//        this.uploadTime = time;
//        this.likeCount = Math.toIntExact(likeCount);
//        this.commentCount = Math.toIntExact(commentCount);
//    }
    public static PopularArticleDto toPopularArticleDto(Article article){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String time = article.getCreatedAt().format(formatter);
        return new PopularArticleDto(
                article.getId(),
                article.getTitle(),
                time,
                article.getLikeArticles().size(),
                article.getComments().size());
    }


}
