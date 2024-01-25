package rabbit.umc.com.demo.converter;

import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.image.Image;

public class ImageConverter {

    public static Image toImage(Article article, String filepath, String imageName){
        return Image.builder()
                .article(article)
                .filePath(filepath)
                .imageName(imageName)
                .build();
    }
}
