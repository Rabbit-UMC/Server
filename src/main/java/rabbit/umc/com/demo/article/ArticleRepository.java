package rabbit.umc.com.demo.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.article.domain.Article;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = "select a from Article a , LikeArticle la  where a.id = la.article.id and count(a)>10 order by a.createdAt desc ")
    public List<Article> popularArticle();



}
