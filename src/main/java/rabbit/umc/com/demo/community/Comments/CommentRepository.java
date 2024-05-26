package rabbit.umc.com.demo.community.Comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.community.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
