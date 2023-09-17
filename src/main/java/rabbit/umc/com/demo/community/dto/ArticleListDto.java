package rabbit.umc.com.demo.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import rabbit.umc.com.demo.community.domain.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ArticleListDto {

    private Long articleId;
    private String articleTitle;
    private String uploadTime;
    private int likeCount;
    private int commentCount;


    public static ArticleListDto toArticleListRes(Article article){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = article.getCreatedAt();
        long yearsAgo = ChronoUnit.YEARS.between(createdAt, now);

        String uploadTime;

        if (yearsAgo == 0) {
            long daysAgo = ChronoUnit.DAYS.between(createdAt, now);

            if (daysAgo == 0) {
                uploadTime = createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                uploadTime = createdAt.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            }
        } else {
            uploadTime = yearsAgo + "년 전";
        }

        return new ArticleListDto(
                article.getId(),
                article.getTitle(),
                uploadTime,
                article.getLikeArticles().size(),
                article.getComments().size());
    }
}
