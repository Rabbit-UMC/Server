package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.*;
import rabbit.umc.com.demo.article.dto.*;
import rabbit.umc.com.demo.mainmission.MainMissionRepository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.dto.MainMissionListDto;
import rabbit.umc.com.demo.report.Report;
import rabbit.umc.com.demo.report.ReportRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MainMissionRepository mainMissionRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final LikeArticleRepository likeArticleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReportRepository reportRepository;


    public CommunityHomeRes getHome() {
        CommunityHomeRes communityHomeRes = new CommunityHomeRes();

        PageRequest pageable = PageRequest.of(0,4);
        List<Article> articleList = articleRepository.findPopularArticleLimitedToFour(Status.ACTIVE, pageable);
        communityHomeRes.setPopularArticle(
                articleList.stream()
                        .map(PopularArticleDto::toPopularArticleDto)
                        .collect(Collectors.toList())
        );

        List<MainMission> missionList = mainMissionRepository.findProgressMissionByStatus(Status.ACTIVE);
        communityHomeRes.setMainMission(missionList
                .stream()
                .map(MainMissionListDto::tomainMissionListDto)
                .collect(Collectors.toList()));

        return communityHomeRes;
    }

    public List<ArticleListRes> getArticles(int page, Long categoryId){

        int pageSize = 20;

        PageRequest pageRequest =PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        List<Article> articlePage = articleRepository.findAllByCategoryIdOrderByCreatedAtDesc(categoryId, pageRequest);

        List<ArticleListRes> articleListRes = articlePage.stream()
                .map(ArticleListRes::toArticleListRes)
                .collect(Collectors.toList());

        return articleListRes;
    }

    public ArticleRes getArticle(Long articleId){

        Article article = articleRepository.findArticleById(articleId);
        List<Image> images = imageRepository.findAllByArticleId(articleId);

        List<ArticleImageDto> articleImages = images.stream()
                .map(ArticleImageDto::toArticleImageDto)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByArticleId(articleId);

        List<CommentListDto> commentLists = comments.stream()
                .map(CommentListDto::toCommentListDto)
                .collect(Collectors.toList());

        return ArticleRes.toArticleRes(article,articleImages,commentLists);

    }

    @Transactional
    public void deleteArticle(Long articleId, Long userId) throws BaseException {
        Article findArticle = articleRepository.findArticleById(articleId);
        if(!findArticle.getUser().getId().equals(userId)){
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        }
        articleRepository.deleteById(articleId);
    }

    @Transactional
    public Long postArticle(PostArticleReq postArticleReq, Long userId , Long categoryId) {
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(categoryId);
        Article article = new Article();
        article.setTitle(postArticleReq.getArticleTitle());
        article.setContent(postArticleReq.getArticleContent());

        article.setUser(user);
        article.setCategory(category);


        articleRepository.save(article);

        List<String> imageList = postArticleReq.getImageList();
        for (String imagePath : imageList) {
            Image image = new Image();
            image.setArticle(article);
            image.setFilePath(imagePath);
            imageRepository.save(image);
        }
        return article.getId();
    }

    @Transactional
    public void updateArticle(Long userId, PatchArticleReq patchArticleReq, Long articleId) throws BaseException {

        //유저 권한 확인
        Article findArticle = articleRepository.findArticleById(articleId);
        System.out.println("userId" + userId);
        System.out.println("authorId" + findArticle.getUser().getId());
        if (!findArticle.getUser().getId().equals(userId)) {
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        }

        findArticle.setTitle(patchArticleReq.getArticleTitle());
        findArticle.setContent(patchArticleReq.getArticleContent());

        List<Image> findImages = imageRepository.findAllByArticleId(articleId);

        // 업데이트할 이미지 ID 목록을 생성
        Set<Long> updatedImageIds = patchArticleReq.getImageList().stream()
                .map(ChangeImageDto::getImageId)
                .collect(Collectors.toSet());

        // 기존 이미지 중 업데이트할 이미지 ID 목록에 포함되지 않은 이미지를 삭제
        List<Image> imagesToDelete = findImages.stream()
                .filter(image -> !updatedImageIds.contains(image.getId()))
                .collect(Collectors.toList());

        // 이미지 삭제
        imageRepository.deleteAll(imagesToDelete);

        // 업데이트할 이미지를 기존 이미지와 매칭하여 업데이트 또는 추가
        for (ChangeImageDto imageDto : patchArticleReq.getImageList()) {
            Image findImage = findImages.stream()
                    .filter(image -> image.getId().equals(imageDto.getImageId()))
                    .findFirst()
                    .orElse(new Image()); // 새 이미지 생성

            findImage.setArticle(findArticle);
            findImage.setFilePath(imageDto.getFilePath());

            // 이미지 저장 또는 업데이트
            imageRepository.save(findImage);
        }
    }

    @Transactional
    public void reportArticle(Long userId, Long articleId) throws BaseException {
        User user = userRepository.getReferenceById(userId);
        Article article = articleRepository.getReferenceById(articleId);
        Report existingReport = reportRepository.findReportByUserIdAndArticleId(userId, articleId);
        if(existingReport != null){
            throw new BaseException(BaseResponseStatus.FAILED_TO_REPORT);
        }
        Report report = new Report();
        report.setUser(user);
        report.setArticle(article);
        reportRepository.save(report);

    }


    @Transactional
    public void likeArticle(Long userId, Long articleId) throws BaseException {
        User user = userRepository.getReferenceById(userId);
        Article article = articleRepository.getReferenceById(articleId);
        LikeArticle existlikeArticle = likeArticleRepository.findLikeArticleByArticleIdAndUserId(articleId,userId);
        if(existlikeArticle != null){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LIKE);
        }
        LikeArticle likeArticle = new LikeArticle();
        likeArticle.setUser(user);
        likeArticle.setArticle(article);
        likeArticleRepository.save(likeArticle);
    }

    @Transactional
    public void unLikeArticle(Long userId, Long articleId) throws BaseException {
        User user = userRepository.getReferenceById(userId);
        Article article = articleRepository.getReferenceById(articleId);
        LikeArticle existlikeArticle = likeArticleRepository.findLikeArticleByArticleIdAndUserId(articleId,userId);
        if(existlikeArticle == null){
            throw new BaseException(BaseResponseStatus.FAILED_TO_UNLIKE);
        }
        likeArticleRepository.delete(existlikeArticle);

    }
}