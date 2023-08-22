package rabbit.umc.com.demo.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.community.domain.Article;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ArticleRes {
    private String categoryName;
    private Long articleId;
    private Long authorId;
    private String authorProfileImage;
    private String authorName;
    private String uploadTime;
    private String articleTitle;
    private String articleContent;
    private Boolean likeArticle;
    private List<ArticleImageDto> articleImage;
    private List<CommentListDto> commentList;

    public static ArticleRes toArticleRes(Article article, List<ArticleImageDto> articleImage, List<CommentListDto> commentList, Boolean isLike){
        /**
         * 시간 포맷 (yyyy-MM-dd HH:mm)
         */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String uploadTime = article.getCreatedAt().format(formatter);
        return new ArticleRes(
                article.getCategory().getName(),
                article.getId(),
                article.getUser().getId(),
                article.getUser().getUserProfileImage(),
                article.getUser().getUserName(),
                uploadTime,
                article.getTitle(),
                article.getContent(),
                isLike,
                articleImage,
                commentList
        );
    }

}
