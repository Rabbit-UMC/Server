package rabbit.umc.com.demo.community.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostArticleReq {
    private String articleTitle;
    private String articleContent;
    private List<String> imageList;


}
