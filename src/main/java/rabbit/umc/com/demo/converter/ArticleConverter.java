package rabbit.umc.com.demo.converter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.dto.CommunityHomeRes.PopularArticleDto;

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
}
