package rabbit.umc.com.demo.community.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
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

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;



    //Setter
    public void changeImage(String filepath){
        image = filepath;
    }
}
