package rabbit.umc.com.demo.article;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.article.domain.Comment;
import rabbit.umc.com.demo.article.domain.Image;
import rabbit.umc.com.demo.article.dto.*;
import rabbit.umc.com.demo.mainmission.MainMissionRepository;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.dto.MainMissionListDto;

import java.util.List;
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

    public void deleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }

    public Long postArticle(PostArticleReq postArticleReq, Long userId) {
        Article article = new Article();
        article.setArticle(postArticleReq);

        List<String> imageList = postArticleReq.getImageList();
        for (String imagePath : imageList) {
            Image image = new Image();
            image.setArticle(article);
            image.setFilePath(imagePath);
            imageRepository.save(image);
        }

        return article.getId();
    }

}
