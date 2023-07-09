package rabbit.umc.com.demo.article;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.user.Domain.User;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = "SELECT a\n" +
            "FROM Article a JOIN LikeArticle la ON a.id = la.article.id \n" +
            "WHERE a.status = :status " +
            "GROUP BY a\n" +
            "HAVING COUNT(a) > 1\n" +
            "ORDER BY a.createdAt DESC ")
    List<Article> findPopularArticleLimitedToFour(@Param("status") Status status, PageRequest pageRequest);

    List<Article> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);

    List<Article> findAllByCategoryIdOrderByCreatedAtDesc(Long categoryId, PageRequest pageRequest);


    Article findArticleById(Long id);



}