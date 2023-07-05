package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;

@Getter
@Setter
@AllArgsConstructor
public class PopularArticle {
    private Long articleId;
    private String articleTitle;
    private int likeCount;
    private int commentCount;

    public static PopularArticle toPopularArticle(Article article){
        return new PopularArticle(
                article.getId(),
                article.getTitle(),
                article.getComments().size(),
                article.getLikeArticles().size());
    }


}
