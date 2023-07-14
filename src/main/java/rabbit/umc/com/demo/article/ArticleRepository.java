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

    @Query(value =
            "SELECT a FROM Article a " +
            "JOIN LikeArticle la ON a.id = la.article.id " +
            "WHERE a.status = :status " +
            "GROUP BY a " +
            "HAVING COUNT(a) >= 5 " +
            "ORDER BY a.createdAt DESC ")
    List<Article> findPopularArticleLimitedToFour(@Param("status") Status status, PageRequest pageRequest);

//    @Query("SELECT new rabbit.umc.com.demo.article.dto.PopularArticleDto(a.id,a.title,a. createdAt, count(la), count(c)) " +
//            "FROM Article a " +
//            "LEFT JOIN a.likeArticles la " +
//            "LEFT JOIN a.comments c " +
//            "WHERE a.status = :status " +
//            "GROUP BY a.id, a.title, a.updatedAt " +
//            "HAVING COUNT(la) >= 20 " +
//            "ORDER BY a.updatedAt DESC")
//    List<rabbit.umc.com.demo.article.dto.PopularArticleDto> findPopularArticleLimitedToFour(@Param("status") Status status, PageRequest pageRequest);


//    List<Article> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);


    List<Article> findAllByCategoryIdAndStatusOrderByCreatedAtDesc(Long categoryId,Status status, PageRequest pageRequest);



    Article findArticleById(Long id);


    @Query(value = "SELECT a\n" +
            "FROM Article a " +
            "JOIN LikeArticle la ON a.id = la.article.id " +
            "WHERE a.status = :status " +
            "GROUP BY a " +
            "HAVING COUNT(a) >= 5 "
           )
    List<Article> findArticleLimited20(@Param("status") Status status, PageRequest pageRequest);






}