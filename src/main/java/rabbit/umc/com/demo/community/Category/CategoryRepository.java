package rabbit.umc.com.demo.community.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
}
