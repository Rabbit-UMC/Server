package rabbit.umc.com.demo.community.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;

import javax.persistence.*;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.user.Domain.User;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Table(name = "category")
public class Category extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "category")
    private List<MainMission> mainMissions;


    //Setter
    public void changeImage(String filepath){
        image = filepath;
    }

    public void  changeHostUser(User user){
        this.user = user;
    }
}
