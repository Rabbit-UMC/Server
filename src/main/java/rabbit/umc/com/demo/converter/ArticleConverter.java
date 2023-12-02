package rabbit.umc.com.demo.converter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.dto.ArticleListRes.ArticleDto;
import rabbit.umc.com.demo.community.dto.ArticleRes;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.PopularArticleDto;
import rabbit.umc.com.demo.community.dto.CommunityHomeResV2.PopularArticleDtoV2;
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
}
