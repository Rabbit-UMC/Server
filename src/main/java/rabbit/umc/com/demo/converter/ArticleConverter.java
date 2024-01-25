package rabbit.umc.com.demo.converter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.Category;
import rabbit.umc.com.demo.image.domain.Image;
import rabbit.umc.com.demo.community.domain.mapping.LikeArticle;
import rabbit.umc.com.demo.community.dto.ArticleListRes;
import rabbit.umc.com.demo.community.dto.ArticleListRes.ArticleDto;
import rabbit.umc.com.demo.community.dto.ArticleRes;
import rabbit.umc.com.demo.community.dto.ArticleRes.ArticleImageDto;
import rabbit.umc.com.demo.community.dto.ArticleRes.CommentDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.MainMissionDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.PopularArticleDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.PopularArticleDtoV2;
import rabbit.umc.com.demo.community.dto.GetPopularArticleRes;
import rabbit.umc.com.demo.community.dto.PostArticleReq;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.utils.DateUtil;

public class ArticleConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public static List<PopularArticleDto> toPopularArticleDto(List<Article> articleList){
        return articleList
                .stream()
                .map(article -> PopularArticleDto.builder()
                        .articleId(article.getId())
                        .articleTitle(article.getTitle())
                        .uploadTime(article.getCreatedAt().format(DATE_TIME_FORMATTER))
                        .likeCount(article.getLikeArticles().size())
                        .build())
                .collect(Collectors.toList());
    }

    public static CommunityHomeRes toCommunityHomeRes(List<MainMission> missionList, List<Article> articleList){
        List<PopularArticleDto> popularArticleList = ArticleConverter.toPopularArticleDto(articleList);
        List<MainMissionDto> mainMissionDtoList = MainMissionConverter.toMainMissionDtoList(missionList);

        return CommunityHomeRes.builder()
                .mainMission(mainMissionDtoList)
                .popularArticle(popularArticleList)
                .build();
    }

    public static List<PopularArticleDtoV2> toPopularArticleDtoV2(List<Article> top4Articles){
        return top4Articles.stream()
                .map(article -> PopularArticleDtoV2.builder()
                        .articleId(article.getId())
                        .articleTitle(article.getTitle())
                        .uploadTime(article.getCreatedAt().format(DATE_TIME_FORMATTER))
                        .likeCount(article.getLikeArticles().size())
                        .build())
                .collect(Collectors.toList());

    }

    public static List<ArticleDto> toArticleDto (List<Article> articleList){
        return articleList
                .stream()
                .map(article -> ArticleDto.builder()
                        .articleId(article.getId())
                        .articleTitle(article.getTitle())
                        .uploadTime(DateUtil.makeArticleUploadTime(article.getCreatedAt()))
                        .likeCount(article.getLikeArticles().size())
                        .commentCount(article.getComments().size())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<GetPopularArticleRes> toGetPopularArticleRes(List<Article> articleList){
        return articleList
                .stream()
                .map(article -> GetPopularArticleRes.builder()
                        .articleId(article.getId())
                        .articleTitle(article.getTitle())
                        .uploadTime(article.getCreatedAt().format(DATE_TIME_FORMATTER))
                        .likeCount(article.getLikeArticles().size())
                        .commentCount(article.getComments().size())
                        .build())
                .collect(Collectors.toList());
    }

    public static ArticleListRes toArticleListRes(Category category, MainMission mainMission, List<Article> articlePage){
        return ArticleListRes.builder()
                .categoryImage(category.getImage())
                .mainMissionId(mainMission.getId())
                .categoryHostId(category.getUserId())
                .articleLists(ArticleConverter.toArticleDto(articlePage))
                .build();
    }

    public static ArticleImageDto toArticleImageDto(Image image){
        return ArticleImageDto.builder()
                .imageId(image.getId())
                .filePath(image.getFilePath())
                .build();
    }

    public static ArticleRes toArticleRes(Article article, Boolean isLike,List<ArticleImageDto> articleImages, List<CommentDto> commentLists ){
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

    public static Article toArticle(PostArticleReq postArticleReq, User user, Category category){
        return Article.builder()
                .title(postArticleReq.getArticleTitle())
                .content(postArticleReq.getArticleContent())
                .user(user)
                .category(category)
                .build();
    }

    public static LikeArticle toLikeArticle(User user, Article article){
        return LikeArticle.builder()
                .user(user)
                .article(article)
                .build();
    }
}
