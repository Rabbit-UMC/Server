package rabbit.umc.com.demo.user.Domain;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    private Long id;

    @Schema(description = "카카오 아이디(식별자)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @JoinColumn(nullable = false)
    private Long kakaoId;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1234")
    private String userEmail;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1234")
    private String userName;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1234")
    private String userProfileImage;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private UserPermission userPermission;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1234")
    private String ageRange;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1234")
    private String gender;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1234")
    private String birthday;


    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private Status status;

    @Schema(description = "유저 식별자", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    private String jwtRefreshToken;

    public User(Long kakaoId, String userName, String profile_image, UserPermission userPermission, String ageRange,
                String gender, String birthday, Status status) {
        this.kakaoId = kakaoId;
        this.userName = userName;
        this.userProfileImage = profile_image;
        this.userPermission=userPermission;
        this.ageRange=ageRange;
        this.gender=gender;
        this.birthday=birthday;
        this.status=status;
    }

}
