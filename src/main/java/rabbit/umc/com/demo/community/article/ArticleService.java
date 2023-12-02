package rabbit.umc.com.demo.community.article;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.community.*;
import rabbit.umc.com.demo.community.Category.CategoryRepository;
import rabbit.umc.com.demo.community.Comments.CommentRepository;
import rabbit.umc.com.demo.community.domain.*;
import rabbit.umc.com.demo.community.domain.mapping.LikeArticle;
import rabbit.umc.com.demo.community.dto.*;
import rabbit.umc.com.demo.community.dto.ArticleListsRes.ArticleListDto;
import rabbit.umc.com.demo.community.dto.ArticleRes.ArticleImageDto;
import rabbit.umc.com.demo.community.dto.ArticleRes.CommentListDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.MainMissionListDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.PopularArticleDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.MainMissionListDtoV2;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.PopularArticleDtoV2;
import rabbit.umc.com.demo.mainmission.repository.MainMissionRepository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.report.Report;
import rabbit.umc.com.demo.report.ReportRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static rabbit.umc.com.config.BaseResponseStatus.*;
import static rabbit.umc.com.demo.Status.*;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final int POPULAR_ARTICLE_LIKE = 4;
    private static final int PAGING_SIZE = 20;

    private final ArticleRepository articleRepository;
    private final MainMissionRepository mainMissionRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final LikeArticleRepository likeArticleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReportRepository reportRepository;

    private String calculateDDay(LocalDate endDateTime) {
        LocalDate currentDateTime = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(currentDateTime, endDateTime);

        if (daysRemaining > 0) {
            return "D-" + daysRemaining;
        } else if (daysRemaining == 0) {
            return "D-day";
        } else {
            return "D+" + Math.abs(daysRemaining);
        }
    }

    public CommunityHomeRes getHomeV1() {
        //상위 4개만 페이징
        PageRequest pageable = PageRequest.of(0,POPULAR_ARTICLE_LIKE);

        //STATUS:ACTIVE 인기 게시물 4개만 가져오기
        List<Article> articleList = articleRepository.findPopularArticleLimitedToFour(ACTIVE, pageable);
        //DTO 에 매핑
        List<PopularArticleDto> popularArticleDtos = articleList
                        .stream()
                        .map(article -> PopularArticleDto.builder()
                                .articleId(article.getId())
                                .articleTitle(article.getTitle())
                                .uploadTime(article.getCreatedAt().format(DATE_TIME_FORMATTER))
                                .likeCount(article.getLikeArticles().size())
                                .build())
                        .collect(Collectors.toList());

        // STATUS:ACTIVE 미션만 가져오기
        List<MainMission> missionList = mainMissionRepository.findProgressMissionByStatus(ACTIVE);
        //Dto 에 매핑
        List<MainMissionListDto> mainMissionListDtos = missionList
                .stream()
                .map(mainMission -> MainMissionListDto.builder()
                        .mainMissionId(mainMission.getId())
                        .mainMissionTitle(mainMission.getTitle())
                        .categoryImage(mainMission.getCategory().getImage())
                        .categoryName(mainMission.getCategory().getName())
                        .dDay(calculateDDay(mainMission.getEndAt()))
                        .build())
                .collect(Collectors.toList());

        return CommunityHomeRes.builder()
                .mainMission(mainMissionListDtos)
                .popularArticle(popularArticleDtos)
                .build();
    }

    public CommunityHomeResV2 getHomeV2(Long userId) {
        return CommunityHomeResV2.builder()
                .mainMission(getAllMainMission())
                .popularArticle(getTop4Articles())
                .userHostCategory(findHostCategoryIds(userId))
                .build();
    }

    public List<Long> findHostCategoryIds(Long userId){
        List<Category> category = categoryRepository.findAllByUserId(userId);
        return category.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

    }
    public List<PopularArticleDtoV2> getTop4Articles(){
        PageRequest pageRequest = PageRequest.of(0,POPULAR_ARTICLE_LIKE);

        List<Article> top4Article = articleRepository.findPopularArticleLimitedToFour(ACTIVE, pageRequest);

        return top4Article.stream()
                .map(article -> PopularArticleDtoV2.builder()
                        .articleId(article.getId())
                        .articleTitle(article.getTitle())
                        .uploadTime(article.getCreatedAt().format(DATE_TIME_FORMATTER))
                        .likeCount(article.getLikeArticles().size())
                        .build())
                .collect(Collectors.toList());
    }

    public List<MainMissionListDtoV2> getAllMainMission(){
        List<MainMission> allMissions = mainMissionRepository.findProgressMissionByStatus(ACTIVE);

        return allMissions.stream()
                .map(mainMission -> MainMissionListDtoV2.builder()
                        .mainMissionId(mainMission.getId())
                        .mainMissionTitle(mainMission.getTitle())
                        .dDay(calculateDDay(mainMission.getEndAt()))
                        .hostUserName(getHostUserName(mainMission))
                        .build())
                .collect(Collectors.toList());
    }

    private String getHostUserName(MainMission mainMission){
        Long hostId = mainMission.getCategory().getUserId();
        User hostUser = userRepository.getReferenceById(hostId);
        return  hostUser.getUserName();
    }

    public ArticleListsRes getArticles(int page, Long categoryId){

        Category category = categoryRepository.getReferenceById(categoryId);

        PageRequest pageRequest =PageRequest.of(page, PAGING_SIZE, Sort.by("createdAt").descending());
        // Status:ACTIVE, categoryId에 해당하는 게시물 페이징 해서 가져오기
        List<Article> articlePage = articleRepository.findAllByCategoryIdAndStatusOrderByCreatedAtDesc(categoryId, Status.ACTIVE, pageRequest);
        List<ArticleListDto> articleListRes = articlePage
                .stream()
                .map(article -> ArticleListDto.builder()
                        .articleId(article.getId())
                        .articleTitle(article.getTitle())
                        .uploadTime(makeArticleUploadTime(article.getCreatedAt()))
                        .likeCount(article.getLikeArticles().size())
                        .commentCount(article.getComments().size())
                        .build())
                .collect(Collectors.toList());

        //Status:ACTIVE, categoryId에 해당하는 메인미션 가져오기
        MainMission mainMission = mainMissionRepository.findMainMissionsByCategoryIdAndStatus(categoryId, ACTIVE);
        //DTO 에 매핑 (카테고리 이미지, 메인미션 ID, 카테고리 ID, 페이징된 게시물 DTO)

        return ArticleListsRes.builder()
                .categoryImage(category.getImage())
                .mainMissionId(mainMission.getId())
                .categoryHostId(category.getUserId())
                .articleLists(articleListRes)
                .build();
    }

    private String makeArticleUploadTime (LocalDateTime createTime){
        LocalDateTime now = LocalDateTime.now();
        long yearsAgo = ChronoUnit.YEARS.between(createTime, now);
        String uploadTime;

        if (yearsAgo == 0) {
            long daysAgo = ChronoUnit.DAYS.between(createTime, now);

            if (daysAgo == 0) {
                uploadTime = createTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                uploadTime = createTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            }
        } else {
            uploadTime = yearsAgo + "년 전";
        }
        return uploadTime;

    }


    public ArticleRes getArticle(Long articleId, Long userId){
        // userId 유저가 articleId 게시물 좋아하는지 체크
        Boolean isLike = likeArticleRepository.existsByArticleIdAndUserId(articleId, userId);

        Article article = articleRepository.findArticleById(articleId);

        // 게시물의 이미지들에 대해 DTO 에 매핑
        List<ArticleImageDto> articleImages = article.getImages()
                .stream()
                .map(image -> ArticleImageDto.builder()
                        .imageId(image.getId())
                        .filePath(image.getFilePath())
                        .build())
                .collect(Collectors.toList());

        // 게시물의 댓글들에 대해 DTO 매핑
        List<CommentListDto> commentLists = article.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(comment -> CommentListDto.builder()
                        .commentUserId(comment.getUser().getId())
                        .commentId(comment.getId())
                        .commentAuthorProfileImage(comment.getUser().getUserProfileImage())
                        .commentAuthorName(comment.getUser().getUserName())
                        .commentContent(comment.getCommentContent())
                        .userPermission(comment.getUser().getUserPermission().name())
                        .build())
                .collect(Collectors.toList());

        return ArticleRes.builder()
                .categoryName(article.getCategory().getName())
                .articleId(article.getId())
                .authorId(article.getUser().getId())
                .authorProfileImage(article.getUser().getUserProfileImage())
                .authorName(article.getUser().getUserName())
                .uploadTime(article.getCreatedAt().format(DATE_TIME_FORMATTER))
                .articleTitle(article.getTitle())
                .articleContent(article.getContent())
                .likeArticle(isLike)
                .articleImage(articleImages)
                .commentList(commentLists)
                .build();

    }


    @Transactional
    public void deleteArticle(Long articleId, Long userId) throws BaseException {
        try {
            Article findArticle = articleRepository.findArticleById(articleId);
            // 게시물 존재 여부 체크
            if (findArticle.getId() == null) {
                throw new NullPointerException("Unable to find Article with id: " + articleId);
            }
            // JWT 가 게시물 작성유저와 동일한지 체크
            if (!findArticle.getUser().getId().equals(userId)) {
                throw new BaseException(INVALID_USER_JWT);
            }
            articleRepository.deleteById(articleId);
        }catch (NullPointerException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }

    @Transactional
    public Long postArticle(PostArticleReq postArticleReq, Long userId , Long categoryId ) {
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(categoryId);
        Article article = new Article();

        // 게시물 생성
        article.setArticle(postArticleReq, user, category);
        articleRepository.save(article);

        // 게시물 이미지 생성
        List<String> imageList = postArticleReq.getImageList();
        for (String imagePath : imageList) {
            Image image = new Image();
            image.setImage(article,imagePath);
            imageRepository.save(image);
        }
        return article.getId();
    }

    @Transactional
    public void updateArticle(Long userId, PatchArticleReq patchArticleReq, Long articleId) throws BaseException {
        try {
            Article findArticle = articleRepository.findArticleById(articleId);
            //글 존재 여부 체크
            if (findArticle.getId() == null) {
                throw new NullPointerException("Unable to find Article with id:" + articleId);
            }
            // JWT 가 글 작성 유저와 동일한지 체크
            if (!findArticle.getUser().getId().equals(userId)) {
                throw new BaseException(INVALID_USER_JWT);
            }

            findArticle.setTitle(patchArticleReq.getArticleTitle());
            findArticle.setContent(patchArticleReq.getArticleContent());

            List<Image> findImages = imageRepository.findAllByArticleId(articleId);

            // 업데이트할 이미지 ID 목록을 생성
            Set<Long> updatedImageIds = patchArticleReq.getImageList()
                    .stream()
                    .map(ChangeImageDto::getImageId)
                    .collect(Collectors.toSet());

            // 기존 이미지 중 업데이트할 이미지 ID 목록에 포함되지 않은 이미지를 삭제
            List<Image> imagesToDelete = findImages
                    .stream()
                    .filter(image -> !updatedImageIds.contains(image.getId()))
                    .collect(Collectors.toList());

            // 이미지 삭제
            imageRepository.deleteAll(imagesToDelete);

            // 업데이트할 이미지를 기존 이미지와 매칭하여 업데이트 또는 추가
            for (ChangeImageDto imageDto : patchArticleReq.getImageList()) {
                Image findImage = findImages
                        .stream()
                        .filter(image -> image.getId().equals(imageDto.getImageId()))
                        .findFirst()
                        .orElse(new Image()); // 새 이미지 생성

                findImage.setArticle(findArticle);
                findImage.setFilePath(imageDto.getFilePath());

                // 이미지 저장 또는 업데이트
                imageRepository.save(findImage);
            }
        }catch (NullPointerException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }

    @Transactional
    public void reportArticle(Long userId, Long articleId) throws BaseException {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            // 게시물 존재 체크
            if(article.getId() == null){
                throw new EntityNotFoundException("Unable to find article with id:" + articleId);
            }
            User user = userRepository.getReferenceById(userId);

            // 이미 신고한 게시물인지 체크
            Boolean isReportExists = reportRepository.existsByUserAndArticle(user, article);
            if (isReportExists) {
                throw new BaseException(FAILED_TO_REPORT);

            } else {
                Report report = new Report();
                report.setUser(user);
                report.setArticle(article);
                reportRepository.save(report);

                // 신고 횟수 15회 이상 시 게시물 status 변경 로직  [ACTIVE -> INACTIVE]
                int reportCount = reportRepository.countByArticleId(articleId);
                if (reportCount > 14) {
                    article.setStatus(INACTIVE);
                }
            }
        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }


    @Transactional
    public void likeArticle(Long userId, Long articleId) throws BaseException {
        try {
            User user = userRepository.getReferenceById(userId);
            Article article = articleRepository.getReferenceById(articleId);

            //게시물 존재 체크
            if (article.getId() == null) {
                throw new EntityNotFoundException("Unable to find article with id:" + articleId);
            }
            LikeArticle existlikeArticle = likeArticleRepository.findLikeArticleByArticleIdAndUserId(articleId, userId);
            //이미 좋아한 게시물인지 체크
            if (existlikeArticle != null) {
                throw new BaseException(FAILED_TO_LIKE);
            }

            //게시물 좋아요 저장
            LikeArticle likeArticle = new LikeArticle();
            likeArticle.setLikeArticle(user, article);
            likeArticleRepository.save(likeArticle);

        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }

    @Transactional
    public void unLikeArticle(Long userId, Long articleId) throws BaseException {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            //게시물 존재 체크
            if (article.getId() == null) {
                throw new EntityNotFoundException("Unable to find article with id:" + articleId);
            }
            LikeArticle existlikeArticle = likeArticleRepository.findLikeArticleByArticleIdAndUserId(articleId, userId);
            //좋아요 했던 게시물인지 체크
            if (existlikeArticle == null) {
                throw new BaseException(FAILED_TO_UNLIKE);
            }
            likeArticleRepository.delete(existlikeArticle);
        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }

    public List<GetPopularArticleRes> popularArticle(int page) throws BaseException{
        int pageSize = 20; //페이징시 가져올 데이터 수
        PageRequest pageRequest =PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        //todo :  현재는 인기 게시물 기준이 좋아요 5개 이상
        //Status:ACTIVE 좋아요 5개 이상인 게시물 최신순으로 정렬해서 가져오기
        List<Article> popularArticles = articleRepository.findArticleLimited20(ACTIVE, pageRequest);

        //DTO 매핑
        List<GetPopularArticleRes> getPopularArticleRes = popularArticles
                .stream()
                .map(GetPopularArticleRes::toPopularArticle)
                .collect(Collectors.toList());

        // 더 이상 페이지가 없을 때 처리
        if (popularArticles.size() ==0 ) {
            throw new BaseException(END_PAGE);
        }

        return getPopularArticleRes;
    }
}