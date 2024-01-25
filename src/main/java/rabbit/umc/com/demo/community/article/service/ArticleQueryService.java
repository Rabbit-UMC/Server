package rabbit.umc.com.demo.community.article.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.community.article.ArticleRepository;
import rabbit.umc.com.demo.community.domain.Article;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ArticleQueryService {
    private final ArticleRepository articleRepository;

    public Article findById(Long id) throws BaseException {
        return articleRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DONT_EXIST_ARTICLE));
    }

}
