package rabbit.umc.com.demo.image;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.dto.PatchArticleReq.ChangeImageDto;
import rabbit.umc.com.demo.converter.ImageConverter;
import rabbit.umc.com.demo.image.uuid.Uuid;
import rabbit.umc.com.demo.image.uuid.UuidRepository;
import rabbit.umc.com.s3.AmazonS3Manager;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {
    private static final String MISSION_PROOF_PATH = "main";

    private final ImageRepository imageRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    @Transactional
    public Uuid makeUuid(){
        String uuid = UUID.randomUUID().toString();
        Uuid saveUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid)
                .build());
        return saveUuid;
    }

    @Transactional
    public void createArticleImage(List<MultipartFile> files, Article article){
        for (MultipartFile file : files) {
            Uuid uuid = makeUuid();
            String imageUrl = s3Manager.uploadFile(s3Manager.generateKeyName(uuid, "article"), file);

            Image image = ImageConverter.toImage(article, imageUrl, uuid.toString());
            imageRepository.save(image);
        }
    }

    @Transactional
    public String createImage(MultipartFile file, String path){
        Uuid uuid = makeUuid();
        return s3Manager.uploadFile(s3Manager.generateKeyName(uuid, path), file);
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
