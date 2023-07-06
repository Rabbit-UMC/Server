package rabbit.umc.com.demo.user.Domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.Status;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @JoinColumn(nullable = false)
    private Long kakaoId;

    private String userEmail;

    private String userName;

    private String userProfileImage;

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private UserPermision userPermission;

    private String ageRange;
    private String gender;
    private String birthday;

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private Status status;
    @JoinColumn(nullable = false)
    private Timestamp createdAt;
    @JoinColumn(nullable = false)
    private Timestamp updatedAt;

    public User(Long kakaoId, String profile_image, UserPermision userPermission, String ageRange,
                String gender, String birthday, Status status, Timestamp createdAt,Timestamp updatedAt) {
        this.kakaoId = kakaoId;
        this.userProfileImage = profile_image;
        this.userPermission=userPermission;
        this.ageRange=ageRange;
        this.gender=gender;
        this.birthday=birthday;
        this.status=status;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
    }

}
