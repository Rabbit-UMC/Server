package rabbit.umc.com.demo.user;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private Long kakaoId;

    private String userEmail;

    private String userName;

    private String userProfileImage;

    @Enumerated(EnumType.STRING)
    private UserPermision userPermision;

    private String ageRange;
    private String gender;
    private String birthday;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
