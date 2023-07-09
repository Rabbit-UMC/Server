package rabbit.umc.com.demo.article.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchArticleRes {
    private String articleTitle;
    private String articleContent;
    private List<ChangeImageDto> imageList;


}
