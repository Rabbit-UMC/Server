package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.community.domain.Article;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class UserArticleListResDto {

    private Long articleId;
    private String articleTitle;
    private String uploadTime;
    private int likeCount;
    private int commentCount;

    public static UserArticleListResDto toArticleListRes(Article article){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String uploadTime = article.getCreatedAt().format(formatter);

        return new UserArticleListResDto(
                article.getId(),
                article.getTitle(),
                uploadTime,
                article.getLikeArticles().size(),
                article.getComments().size());
    }


}
