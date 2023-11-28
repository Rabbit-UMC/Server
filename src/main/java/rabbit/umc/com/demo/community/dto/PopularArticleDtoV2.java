package rabbit.umc.com.demo.community.dto;

import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.community.domain.Article;

@Getter
@Setter
@Data
@AllArgsConstructor
public class PopularArticleDtoV2 {
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
    public static PopularArticleDtoV2 toPopularArticleDto(Article article){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String time = article.getCreatedAt().format(formatter);
        return new PopularArticleDtoV2(
                article.getId(),
                article.getTitle(),
                time,
                article.getLikeArticles().size(),
                article.getComments().size());
    }


}
