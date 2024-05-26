package rabbit.umc.com.demo.community.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.community.article.repository.LikeArticleRepository;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.mapping.LikeArticle;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeArticleService {

    private final LikeArticleRepository likeArticleRepository;

    public Boolean existsByArticleAndUserId(Article article, Long userId) {
        return likeArticleRepository.existsByArticleAndUserId(article, userId);
    }

    public LikeArticle findLikeArticleByArticleIdAndUserId(Long articleId, Long userId) throws BaseException {
        return likeArticleRepository.findLikeArticleByArticleIdAndUserId(articleId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.FAILED_TO_UNLIKE));
    }

    public void deleteLikeArticle(LikeArticle likeArticle) {
        likeArticleRepository.delete(likeArticle);
    }

    public void saveLikeArticle(LikeArticle likeArticle) {
        likeArticleRepository.save(likeArticle);
    }
}
