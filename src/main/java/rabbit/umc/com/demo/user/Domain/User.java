package rabbit.umc.com.demo.user.Domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @JoinColumn(nullable = false)
    private Long kakaoId;

    private String userEmail;

    private String userName;

    private String userProfileImage;

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private UserPermission userPermission;

    private String ageRange;
    private String gender;
    private String birthday;

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private Status status;

    private String jwtRefreshToken;

    public User(Long kakaoId, String profile_image, UserPermission userPermission, String ageRange,
                String gender, String birthday, Status status) {
        this.kakaoId = kakaoId;
        this.userProfileImage = profile_image;
        this.userPermission=userPermission;
        this.ageRange=ageRange;
        this.gender=gender;
        this.birthday=birthday;
        this.status=status;
    }

}
