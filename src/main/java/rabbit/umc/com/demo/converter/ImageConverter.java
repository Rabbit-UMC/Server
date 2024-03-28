package rabbit.umc.com.demo.converter;

import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.image.GenerateImageResDto;
import rabbit.umc.com.demo.image.domain.Image;

public class ImageConverter {

    public static Image toArticleImage(Article article, String filepath, String uuid, String originFileName){
        return Image.builder()
                .article(article)
                .filePath(filepath)
                .s3ImageName(uuid)
                .imageName(originFileName)
                .status(Status.ACTIVE)
                .build();
    }

    public static Image toImage(String imageUrl, String uuid, String originalFilename) {
        return Image.builder()
                .imageName(originalFilename)
                .s3ImageName(uuid)
                .filePath(imageUrl)
                .status(Status.ACTIVE)
                .build();
    }

    public static GenerateImageResDto toGenerateImageResDto(Image image) {
        return GenerateImageResDto.builder()
                .imageId(image.getId())
                .imageUrl(image.getFilePath())
                .build();
    }
}
