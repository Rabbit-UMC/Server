package rabbit.umc.com.demo.community.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostArticleReq {
    private String articleTitle;
    private String articleContent;
}
