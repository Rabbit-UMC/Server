package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Comment;

@Getter
@Setter
@AllArgsConstructor
public class CommentListDto {
    private Long id;
    private String commentAuthorProfileImage;
    private String commentAuthorName;
    private String commentContent;

    public static CommentListDto toCommentListDto(Comment comment){
        return new CommentListDto(
                comment.getId(),
                comment.getUser().getUserProfileImage(),
                comment.getUser().getUserName(),
                comment.getContent());
    }
}
