package rabbit.umc.com.demo.community.facade;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.END_PAGE;
import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.FAILED_TO_LIKE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.demo.community.article.service.ArticleService;
import rabbit.umc.com.demo.community.article.service.LikeArticleService;
import rabbit.umc.com.demo.community.category.CategoryService;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.community.domain.mapping.LikeArticle;
import rabbit.umc.com.demo.community.dto.ArticleListRes;
import rabbit.umc.com.demo.community.dto.ArticleRes;
import rabbit.umc.com.demo.community.dto.GetPopularArticleRes;
import rabbit.umc.com.demo.community.dto.PatchArticleReq;
import rabbit.umc.com.demo.community.dto.PostArticleReq;
import rabbit.umc.com.demo.converter.ArticleConverter;
import rabbit.umc.com.demo.image.service.ImageService;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.service.MainMissionService;
import rabbit.umc.com.demo.report.ReportService;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.service.UserQueryService;

@Component
@RequiredArgsConstructor
@Transactional
public class ArticleFacade {

    private static final int PAGING_SIZE = 20;

    private final ArticleService articleService;
    private final MainMissionService mainMissionService;
    private final CategoryService categoryService;
    private final LikeArticleService likeArticleService;
    private final UserQueryService userQueryService;
    private final ImageService imageService;
    private final ReportService reportService;

    public ArticleListRes getArticles(int page, Long categoryId) throws BaseException {

        Category category = categoryService.getCategory(categoryId);
        List<Article> articlePage = articleService.getActiveArticleByCategory(page, category);
        MainMission mainMission = mainMissionService.getActiveMainMissionByCategory(category);

        return ArticleConverter.toArticleListRes(category, mainMission, articlePage);
    }

    public ArticleRes getArticle(Long articleId, Long userId) throws BaseException {

            Article article = articleService.findArticleById(articleId);
            Boolean isLike = likeArticleService.existsByArticleAndUserId(article, userId);

            return ArticleConverter.toArticleRes(article,isLike);
    }

    public List<GetPopularArticleRes> popularArticle(int page) throws BaseException{
        Pageable pageable = PageRequest.of(page, PAGING_SIZE, Sort.by("likeCount", "createdAt").descending());
        List<Article> popularArticles = articleService.getTopLikeArticle(pageable);
        List<GetPopularArticleRes> getPopularArticleRes = ArticleConverter.toGetPopularArticleRes(popularArticles);
        if (popularArticles.isEmpty())
            throw new BaseException(END_PAGE);

        return getPopularArticleRes;
    }

    public void deleteArticle(Long articleId, Long userId) throws BaseException {

        Article deleteTarget = articleService.findArticleById(articleId);
        articleService.validBoardOwner(userId, deleteTarget);

        articleService.deleteArticle(deleteTarget);
    }

    public Long postArticle(List<MultipartFile> multipartFiles, PostArticleReq postArticleReq,
                            Long userId , Long categoryId) throws BaseException {

        User user = userQueryService.getUserByUserId(userId);
        Category category = categoryService.getCategory(categoryId);
        Article article = ArticleConverter.toArticle(postArticleReq,user,category);
        articleService.saveArticle(article);
        if (multipartFiles != null ) imageService.createArticleImage(multipartFiles, article);

        return article.getId();
    }

    public void updateArticle(Long userId, PatchArticleReq patchArticleReq, Long articleId) throws BaseException {

        Article targetArticle = articleService.findArticleById(articleId);
        articleService.validBoardOwner(userId, targetArticle);
        targetArticle.updateArticle(patchArticleReq.getArticleTitle(), patchArticleReq.getArticleContent());

        if (!patchArticleReq.getDeleteImageIdList().isEmpty()){
            imageService.deleteImages(patchArticleReq.getDeleteImageIdList());
        }
        if (!patchArticleReq.getNewImageIdList().isEmpty()){
            for (Long id : patchArticleReq.getNewImageIdList())
                imageService.saveArticleImage(id, targetArticle);
        }

        articleService.saveArticle(targetArticle);
    }

    public void reportArticle(Long userId, Long articleId) throws BaseException {

            Article article = articleService.findArticleById(articleId);
            User user = userQueryService.getUserByUserId(userId);

            reportService.reportArticle(user, article);
    }

    public void likeArticle(Long userId, Long articleId) throws BaseException {

        User user = userQueryService.getUserByUserId(userId);
        Article article = articleService.findArticleById(articleId);
        if (likeArticleService.existsByArticleAndUserId(article, userId))
            throw new BaseException(FAILED_TO_LIKE);

        likeArticleService.saveLikeArticle(ArticleConverter.toLikeArticle(user, article));

    }

    public void unLikeArticle(Long userId, Long articleId) throws BaseException {

        LikeArticle existlikeArticle = likeArticleService.findLikeArticleByArticleIdAndUserId(articleId, userId);
        likeArticleService.deleteLikeArticle(existlikeArticle);

    }


}
