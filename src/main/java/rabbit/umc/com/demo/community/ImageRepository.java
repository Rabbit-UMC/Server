package rabbit.umc.com.demo.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.Image;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByArticleId(Long articleId);


}
