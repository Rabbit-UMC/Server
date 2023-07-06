package rabbit.umc.com.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.user.Domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}