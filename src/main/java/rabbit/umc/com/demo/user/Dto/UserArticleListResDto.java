package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserArticleListResDto {

    private Long id;
    private String articleTitle;
    private LocalDateTime uploadTime;
    private int likeCount;
    private int commentCount;

    public static UserArticleListResDto toArticleListRes(Article article){
        return new UserArticleListResDto(
                article.getId(),
                article.getTitle(),
                article.getUpdatedAt(),
                article.getLikeArticles().size(),
                article.getComments().size());
    }
}
