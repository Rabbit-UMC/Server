package rabbit.umc.com.demo.article.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentReq {
    private String content;
}
