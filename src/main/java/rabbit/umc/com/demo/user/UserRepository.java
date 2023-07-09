package rabbit.umc.com.demo.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import rabbit.umc.com.demo.article.domain.Article;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.Dto.UserGetProfileResDto;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByKakaoId(Long kakaoId);
    boolean existsByKakaoId(Long kakaoId);
    @Modifying
    @Query("UPDATE User u SET u.userProfileImage = :newProfileImage WHERE u.id = :userId")
    void updateUserUserProfileImageById(@Param("userId") Long userId, @Param("newProfileImage") String newProfileImage);
    @Modifying
    @Query("UPDATE User u SET u.userName = :newNickname WHERE u.id = :userId")
    void updateUserUserNameById(@Param("userId") Long userId, @Param("newNickname") String newNickname);

    //유저가 쓴 글 조회
    @Query("SELECT a FROM Article a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<Article> findArticlesByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, PageRequest pageRequest);

    //유저가 댓글을 남긴 글 조회
    @Query("SELECT a FROM Article a JOIN a.comments c WHERE c.user.id = :userId ORDER BY a.createdAt DESC")
    List<Article> findCommentedArticlesByUserId(@Param("userId") Long userId, PageRequest pageRequest);

    //해당 유저가 해당 메인 미션 유저인지 확인
    @Query("SELECT CASE WHEN COUNT(mmu) > 0 THEN true ELSE false END " +
            "FROM MainMissionUsers mmu " +
            "WHERE mmu.user.id = :userId " +
            "AND mmu.mainMission.category.id = :categoryId")
    boolean existsMainMissionUserByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    //유저 랭킹 조회
    @Query("SELECT COUNT(*) + 1 " +
            "FROM MainMissionUsers mmu " +
            "WHERE mmu.mainMission.category.id = :categoryId " +
            "AND mmu.score > (SELECT mmu2.score FROM MainMissionUsers mmu2 WHERE mmu2.user.id = :userId)")
    Long getRankByScoreForMainMissionByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
    //메인 미션 유저 스코어 중, 해당 유저의 스코어보다 높은 사람 숫자 세기 + 1 -> 랭크
    //메인 미션 유저 테이블에는 카테고리 없고 메인 미션 아이디가 있으니까
    //메인 미션 아이디로 메인 미션, 메인 미션 유저 조인시켜서
    //메인 미션에서 카테고리 아이디 값 가져와야하나?
}