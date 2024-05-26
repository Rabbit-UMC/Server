package rabbit.umc.com.demo.community.article.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.community.domain.mapping.LikeArticle;

@Repository
public interface LikeArticleRepository extends JpaRepository<LikeArticle, Long> {

    Optional<LikeArticle> findLikeArticleByArticleIdAndUserId(Long articleId, Long userId);

    Boolean existsByArticleAndUserId(Article article, Long userId);
}
