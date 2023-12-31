package rabbit.umc.com.demo.converter;

import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Image;

public class ImageConverter {

    public static Image toImage(Article article, String filepath){
        return Image.builder()
                .article(article)
                .filePath(filepath)
                .build();
    }
}
