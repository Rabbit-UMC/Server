package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ArticleRes {
    private Long id;
    private String authorProfileImage;
    private String authorName;
    private Timestamp uploadTime;
    private String articleTitle;
    private String articleContent;
    private List<ArticleImageDto> articleImage;
    private List<CommentListDto> commentList;

    public static ArticleRes toArticleRes(Article article, List<ArticleImageDto> articleImage, List<CommentListDto> commentList){
        return new ArticleRes(
                article.getId(),
                article.getUser().getUserProfileImage(),
                article.getUser().getUserName(),
                article.getCreatedAt(),
                article.getTitle(),
                article.getContent(),
                articleImage,
                commentList
        );
    }

}
