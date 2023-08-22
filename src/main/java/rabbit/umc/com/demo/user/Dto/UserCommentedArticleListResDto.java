package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.community.domain.*;
import rabbit.umc.com.demo.user.Domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserCommentedArticleListResDto {
//    private Long articleId;
//    private String articleTitle;
//    private String uploadTime;
//    private int likeCount;
//    private int commentCount;
//    private LocalDateTime commentCreatedAt;

    private Long id;

    private Category category;

    private User user;

    private String title;

    private String content;

    private List<Comment> comments;

    private List<LikeArticle> likeArticles;

    private List<Image> images;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime commentCreatedAt;


    public static UserArticleListResDto toArticleListRes(UserCommentedArticleListResDto userCommentedArticleListResDto){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String uploadTime = userCommentedArticleListResDto.getCreatedAt().format(formatter);

        return new UserArticleListResDto(
                userCommentedArticleListResDto.getId(),
                userCommentedArticleListResDto.getTitle(),
                uploadTime,
                userCommentedArticleListResDto.getLikeArticles().size(),
                userCommentedArticleListResDto.getComments().size());
    }
}
