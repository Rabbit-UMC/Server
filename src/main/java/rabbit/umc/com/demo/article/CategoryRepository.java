package rabbit.umc.com.demo.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.article.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
}
