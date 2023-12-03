package rabbit.umc.com.demo.converter;

import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.demo.community.domain.Comment;
import rabbit.umc.com.demo.community.dto.ArticleRes.CommentDto;

public class CommentConverter {
    public static CommentDto toCommentDto(Comment comment){
        return CommentDto.builder()
                .commentUserId(comment.getUser().getId())
                .commentId(comment.getId())
                .commentAuthorProfileImage(comment.getUser().getUserProfileImage())
                .commentAuthorName(comment.getUser().getUserName())
                .commentContent(comment.getCommentContent())
                .userPermission(comment.getUser().getUserPermission().name())
                .build();
    }
}
