package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostArticleReq {
    private String articleTitle;
    private String articleContent;
    private List<String> imageList;


}
