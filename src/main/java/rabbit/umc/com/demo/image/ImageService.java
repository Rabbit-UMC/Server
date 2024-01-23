package rabbit.umc.com.demo.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.dto.PatchArticleReq.ChangeImageDto;
import rabbit.umc.com.demo.converter.ImageConverter;
import rabbit.umc.com.utils.S3Uploader;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public List<String> getImageUrl(List<MultipartFile> multipartFiles, String path) throws IOException {
        List<String> filePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = s3Uploader.upload(multipartFile, path );
            filePathList.add(filePath);
        }

        return filePathList;
    }

    public List<Image> getArticleImages(Long articleId){
        return imageRepository.findAllByArticleId(articleId);
    }

    @Transactional
    public void deleteImages(List<Image> images, Set<Long> updatedImageIds){
        List<Image> imagesToDelete = images
                .stream()
                .filter(image -> !updatedImageIds.contains(image.getId()))
                .collect(Collectors.toList());

        imageRepository.deleteAll(imagesToDelete);
    }

    @Transactional
    public void postArticleImage(List<String> imageList, Article article){
        for (String filepath : imageList) {
            Image image = ImageConverter.toImage(article, filepath);
            imageRepository.save(image);
        }
    }

    @Transactional
    public void updateArticleImage(List<ChangeImageDto> imageDtos, List<Image> findImages, Article targetArticle) {
        for (ChangeImageDto imageDto : imageDtos) {
            Image findImage = findImages
                    .stream()
                    .filter(image -> image.getId().equals(imageDto.getImageId()))
                    .findFirst()
                    .orElse(new Image()); // 새 이미지 생성

            findImage.setArticle(targetArticle);
            findImage.setFilePath(imageDto.getFilePath());

            imageRepository.save(findImage);
        }
    }
}
