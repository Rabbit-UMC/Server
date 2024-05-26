package rabbit.umc.com.demo.community.article.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.community.domain.Article;

import java.util.List;
import rabbit.umc.com.demo.community.domain.Category;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAllByCategoryAndStatus(Category category, Status status, Pageable pageable);

    Article findArticleById(Long id);

    @Query("SELECT a FROM Article a WHERE a.status = :status AND a.likeCount >= :minLikes")
    List<Article> findArticlesWithMinLikes(@Param("status") Status status, Pageable pageable, @Param("minLikes") int minLikes);
}