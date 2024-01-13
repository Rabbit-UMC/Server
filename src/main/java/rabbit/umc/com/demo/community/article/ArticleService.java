package rabbit.umc.com.demo.community.article;

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
import rabbit.umc.com.demo.community.category.CategoryRepository;
import rabbit.umc.com.demo.community.Comments.CommentRepository;
import rabbit.umc.com.demo.community.domain.*;
import rabbit.umc.com.demo.community.domain.mapping.LikeArticle;
import rabbit.umc.com.demo.community.dto.*;
import rabbit.umc.com.demo.community.dto.ArticleRes.ArticleImageDto;
import rabbit.umc.com.demo.community.dto.ArticleRes.CommentDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.MainMissionDtoV2;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.PopularArticleDtoV2;
import rabbit.umc.com.demo.community.dto.PatchArticleReq.ChangeImageDto;
import rabbit.umc.com.demo.converter.ArticleConverter;
import rabbit.umc.com.demo.converter.CommentConverter;
import rabbit.umc.com.demo.converter.ImageConverter;
import rabbit.umc.com.demo.converter.MainMissionConverter;
import rabbit.umc.com.demo.converter.ReportConverter;
import rabbit.umc.com.demo.image.Image;
import rabbit.umc.com.demo.image.ImageRepository;
import rabbit.umc.com.demo.image.ImageService;
import rabbit.umc.com.demo.mainmission.repository.MainMissionRepository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.report.Report;
import rabbit.umc.com.demo.report.ReportRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserQueryService;

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
    private static final int POPULAR_ARTICLE_LIKE = 4;
    private static final int PAGING_SIZE = 20;
    private static final int REPORT_LIMIT = 15;

    private final ArticleRepository articleRepository;
    private final MainMissionRepository mainMissionRepository;
    private final CommentRepository commentRepository;
    private final LikeArticleRepository likeArticleRepository;
    private final CategoryRepository categoryRepository;
    private final ReportRepository reportRepository;
    private final UserQueryService userQueryService;
    private final ImageService imageService;


    public CommunityHomeRes getHomeV1() {
        //상위 4개만 페이징
        PageRequest pageable = PageRequest.of(0, POPULAR_ARTICLE_LIKE);
        //STATUS:ACTIVE 인기 게시물 4개만 가져오기
        List<Article> articleList = articleRepository.findPopularArticleLimitedToFour(ACTIVE, pageable);
        // STATUS:ACTIVE 미션만 가져오기
        List<MainMission> missionList = mainMissionRepository.findProgressMissionByStatus(ACTIVE);

        return ArticleConverter.toCommunityHomeRes(missionList, articleList);
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
        List<Article> top4Articles = articleRepository.findPopularArticleLimitedToFour(ACTIVE, pageRequest);
        return ArticleConverter.toPopularArticleDtoV2(top4Articles);
    }

    public List<MainMissionDtoV2> getAllMainMission(){
        List<MainMission> allMissions = mainMissionRepository.findProgressMissionByStatus(ACTIVE);

        return allMissions.stream()
                .map(mainMission -> MainMissionConverter
                        .toMainMissionDtoV2(mainMission, getHostUserName(mainMission)))
                .collect(Collectors.toList());
    }

    public String getHostUserName(MainMission mainMission){
        Long hostId = mainMission.getCategory().getUserId();
        User hostUser = userQueryService.getUser(hostId);
        return  hostUser.getUserName();
    }

    public ArticleListRes getArticles(int page, Long categoryId){

        Category category = categoryRepository.getReferenceById(categoryId);
        PageRequest pageRequest =PageRequest.of(page, PAGING_SIZE, Sort.by("createdAt").descending());

        // Status:ACTIVE, categoryId에 해당하는 게시물 페이징 해서 가져오기
        List<Article> articlePage = articleRepository.findAllByCategoryIdAndStatusOrderByCreatedAtDesc(categoryId, Status.ACTIVE, pageRequest);

        //Status:ACTIVE, categoryId에 해당하는 메인미션 가져오기
        MainMission mainMission = mainMissionRepository.findMainMissionsByCategoryIdAndStatus(categoryId, ACTIVE);

        return ArticleConverter.toArticleListRes(category, mainMission, articlePage);
    }

    public ArticleRes getArticle(Long articleId, Long userId) throws BaseException {
        try {
            // userId 유저가 articleId 게시물 좋아하는지 체크
            Boolean isLike = likeArticleRepository.existsByArticleIdAndUserId(articleId, userId);

            Article article = articleRepository.findArticleById(articleId);

            // 게시물의 이미지들에 대해 DTO 에 매핑
            List<ArticleImageDto> articleImages = article.getImages()
                    .stream()
                    .map(ArticleConverter::toArticleImageDto)
                    .collect(Collectors.toList());

            // 게시물의 댓글들에 대해 DTO 매핑
            List<CommentDto> commentLists = article.getComments()
                    .stream()
                    .sorted(Comparator.comparing(Comment::getCreatedAt))
                    .map(CommentConverter::toCommentDto)
                    .collect(Collectors.toList());

            return ArticleConverter.toArticleRes(article,isLike,articleImages,commentLists);
        }catch (NullPointerException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }

    @Transactional
    public void deleteArticle(Long articleId, Long userId) throws BaseException {
        try {
            Article findArticle = articleRepository.findArticleById(articleId);
//             게시물 존재 여부 체크
            if (findArticle.getId() == null) {
                throw new NullPointerException("Unable to find Article with id: " + articleId);
            }
            // JWT 가 게시물 작성유저와 동일한지 체크
            if (!findArticle.getUser().getId().equals(userId)) {
                throw new BaseException(FORBIDDEN);
            }
            articleRepository.deleteById(articleId);
        }catch (NullPointerException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }

    @Transactional
    public Long postArticle(PostArticleReq postArticleReq, Long userId , Long categoryId ) {
        User user = userQueryService.getUser(userId);
        Category category = categoryRepository.getReferenceById(categoryId);

        Article article = ArticleConverter.toArticle(postArticleReq,user,category);
        articleRepository.save(article);

        // 게시물 이미지 생성
        imageService.postArticleImage(postArticleReq.getImageList(), article);
        return article.getId();
    }

    @Transactional
    public void updateArticle(Long userId, PatchArticleReq patchArticleReq, Long articleId) throws BaseException {
        try {
            Article targetArticle = articleRepository.findArticleById(articleId);
            //글 존재 여부 체크
            if (targetArticle.getId() == null) {
                throw new NullPointerException("Unable to find Article with id:" + articleId);
            }
            // JWT 가 글 작성 유저와 동일한지 체크
            if (!targetArticle.getUser().getId().equals(userId)) {
                throw new BaseException(INVALID_USER_JWT);
            }

            targetArticle.setTitle(patchArticleReq.getArticleTitle());
            targetArticle.setContent(patchArticleReq.getArticleContent());

            List<Image> findImages = imageService.getArticleImages(articleId);

            // 업데이트할 이미지 ID 목록을 생성
            Set<Long> updatedImageIds = patchArticleReq.getImageList()
                    .stream()
                    .map(ChangeImageDto::getImageId)
                    .collect(Collectors.toSet());

            // 기존 이미지 중 업데이트할 이미지 ID 목록에 포함되지 않은 이미지를 삭제
            imageService.deleteImages(findImages, updatedImageIds);

            // 업데이트할 이미지를 기존 이미지와 매칭하여 업데이트 또는 추가
            imageService.updateArticleImage(patchArticleReq.getImageList(), findImages, targetArticle);

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
            User user = userQueryService.getUser(userId);

            // 이미 신고한 게시물인지 체크
            Boolean isReportExists = reportRepository.existsByUserAndArticle(user, article);
            if (isReportExists) {
                throw new BaseException(FAILED_TO_REPORT);
            } else {
                Report report = ReportConverter.toArticleReport(user,article);
                reportRepository.save(report);

                // 신고 횟수 15회 이상 시 게시물 status 변경 로직  [ACTIVE -> INACTIVE]
                int reportCount = reportRepository.countByArticleId(articleId);
                if (reportCount >= REPORT_LIMIT) {
                    article.setInactive();
                }
            }
        }catch (EntityNotFoundException e){
            throw new BaseException(DONT_EXIST_ARTICLE);
        }
    }


    @Transactional
    public void likeArticle(Long userId, Long articleId) throws BaseException {
        try {
            User user = userQueryService.getUser(userId);
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
            LikeArticle likeArticle = ArticleConverter.toLikeArticle(user, article);
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

        //Status:ACTIVE 좋아요 5개 이상인 게시물 최신순으로 정렬해서 가져오기
        List<Article> popularArticles = articleRepository.findArticleLimited20(ACTIVE, pageRequest);

        //DTO 매핑
        List<GetPopularArticleRes> getPopularArticleRes = ArticleConverter.toGetPopularArticleRes(popularArticles);

        // 더 이상 페이지가 없을 때 처리
        if (popularArticles.size() == 0 ) {
            throw new BaseException(END_PAGE);
        }

        return getPopularArticleRes;
    }
}