package rabbit.umc.com.demo.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.article.domain.LikeArticle;

@Repository
public interface LikeArticleRepository extends JpaRepository<LikeArticle, Long> {

    LikeArticle findLikeArticleByArticleIdAndUserId(Long articleId, Long userId);
}
