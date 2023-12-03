package rabbit.umc.com.demo.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.community.domain.Article;

import java.time.format.DateTimeFormatter;
import java.util.List;
import rabbit.umc.com.demo.community.domain.Comment;
import rabbit.umc.com.demo.community.domain.Image;

@Getter
@Builder
@NoArgsConstructor
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
    private List<CommentDto> commentList;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleImageDto {
        private Long imageId;
        private String filePath;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDto {
        private Long commentUserId;
        private Long commentId;
        private String commentAuthorProfileImage;
        private String commentAuthorName;
        private String commentContent;
        private String userPermission;

    }
}
