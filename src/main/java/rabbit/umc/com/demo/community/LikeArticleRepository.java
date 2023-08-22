package rabbit.umc.com.demo.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.LikeArticle;

@Repository
public interface LikeArticleRepository extends JpaRepository<LikeArticle, Long> {

    LikeArticle findLikeArticleByArticleIdAndUserId(Long articleId, Long userId);
    LikeArticle deleteByArticleIdAndUserId(Long articleId, Long userId);

    Boolean existsByArticleIdAndUserId(Long articleId, Long userId);
}
