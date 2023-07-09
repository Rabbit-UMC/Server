package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;

@Getter
@Setter
@Data
@AllArgsConstructor
public class PopularArticleDto {
    private Long articleId;
    private String articleTitle;
    private int likeCount;
    private int commentCount;

    public static PopularArticleDto toPopularArticleDto(Article article){
        return new PopularArticleDto(
                article.getId(),
                article.getTitle(),
                article.getLikeArticles().size(),
                article.getComments().size());
    }


}
