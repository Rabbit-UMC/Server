package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Comment;

@Getter
@Setter
@Data
@AllArgsConstructor
public class CommentListDto {
    private Long commentUserId;
    private Long commentId;
    private String commentAuthorProfileImage;
    private String commentAuthorName;
    private String commentContent;
    private String userPermission;

    public static CommentListDto toCommentListDto(Comment comment){
        /**
         * 잠긴 댓글 내용 변경 로직
         */
        String content;
        if (comment.getStatus() == Status.INACTIVE){
            content = "착한 말을 쓰자!";
        }else {
            content = comment.getContent();
        }

        return new CommentListDto(
                comment.getUser().getId(),
                comment.getId(),
                comment.getUser().getUserProfileImage(),
                comment.getUser().getUserName(),
                content,
                comment.getUser().getUserPermission().name());
    }
}
