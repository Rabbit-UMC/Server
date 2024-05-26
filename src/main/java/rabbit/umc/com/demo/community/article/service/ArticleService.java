package rabbit.umc.com.demo.community.article.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.community.article.repository.ArticleRepository;
import rabbit.umc.com.demo.community.domain.*;
import java.util.List;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;
import static rabbit.umc.com.demo.base.Status.*;

@ToString
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private static final int PAGING_SIZE = 20;
    private static final int MIN_LIKES_FOR_POPULAR = 10;
    private static final String BASIC_ARTICLE_SORT = "createdAt";

    private final ArticleRepository articleRepository;


    public Article findArticleById(Long id) throws BaseException {
        return articleRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DONT_EXIST_ARTICLE));
    }

    public void validBoardOwner(Long userId, Article article) throws BaseException {
        if (!article.getUser().getId().equals(userId)){
            throw new BaseException(FORBIDDEN);
        }
    }

    public List<Article> getTopLikeArticle(Pageable pageable){
        return articleRepository.findArticlesWithMinLikes(ACTIVE, pageable, MIN_LIKES_FOR_POPULAR);
    }

    public List<Article> getActiveArticleByCategory(int page, Category category){
        Pageable pageable = PageRequest.of(page, PAGING_SIZE, Sort.by(BASIC_ARTICLE_SORT).descending());
        return articleRepository.findAllByCategoryAndStatus(category, ACTIVE, pageable);
    }

    @Transactional
    public void deleteArticle(Article article){
        articleRepository.delete(article);
    }

    @Transactional
    public void saveArticle(Article article) {
        articleRepository.save(article);
    }

}