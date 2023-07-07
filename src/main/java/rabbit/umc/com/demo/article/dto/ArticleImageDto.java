package rabbit.umc.com.demo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.article.domain.Image;

@Getter
@Setter
@AllArgsConstructor
public class ArticleImageDto {
    private Long id;
    private String filePath;

    public static ArticleImageDto toArticleImageDto(Image image){
        return new ArticleImageDto(
                image.getId(),
                image.getFilePath()
        );
    }

}
