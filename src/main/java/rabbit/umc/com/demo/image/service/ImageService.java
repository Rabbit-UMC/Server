package rabbit.umc.com.demo.image.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.converter.ImageConverter;
import rabbit.umc.com.demo.image.GenerateImageResDto;
import rabbit.umc.com.demo.image.domain.Image;
import rabbit.umc.com.demo.image.repository.ImageRepository;
import rabbit.umc.com.demo.image.uuid.Uuid;
import rabbit.umc.com.demo.image.uuid.UuidRepository;
import rabbit.umc.com.s3.AmazonS3Manager;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    @Transactional
    public Uuid makeUuid() {
        String uuid = UUID.randomUUID().toString();

        return uuidRepository.save(Uuid.builder()
                .uuid(uuid)
                .build());
    }

    @Transactional
    public void createArticleImage(List<MultipartFile> files, Article article) {
        for (MultipartFile file : files) {
            Uuid uuid = makeUuid();
            String imageUrl = s3Manager.uploadFile(s3Manager.generateKeyName(uuid, "article"), file);

            Image image = ImageConverter.toArticleImage(article, imageUrl, uuid.getUuid(), file.getOriginalFilename());
            imageRepository.save(image);
        }
    }

    @Transactional
    public GenerateImageResDto createImage(MultipartFile file, String path) {
        Uuid uuid = makeUuid();
        String imageUrl = s3Manager.uploadFile(s3Manager.generateKeyName(uuid, path), file);
        Image image = ImageConverter.toImage(imageUrl, uuid.getUuid(), file.getOriginalFilename());
        imageRepository.save(image);

        return ImageConverter.toGenerateImageResDto(image);
    }

    public String createImageUrl(MultipartFile file, String path) {
        Uuid uuid = makeUuid();
        return s3Manager.uploadFile(s3Manager.generateKeyName(uuid, path), file);
    }

    @Transactional
    public void deleteImages(List<Long> imageId) {
        List<Image> images = imageRepository.findAllById(imageId);

        for (Image image : images) {
            s3Manager.delete(image.getFilePath());
        }

        imageRepository.deleteAll(images);
    }

    public Image findById(Long id) throws BaseException {
        return imageRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DONT_EXIST_IMAGE));
    }

    public void saveArticleImage(Long id, Article targetArticle) throws BaseException {
        Image image = findById(id);
        image.setArticle(targetArticle);
        imageRepository.save(image);
    }
}
