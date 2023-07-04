package rabbit.umc.com.demo.article;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "catagory")
public class Category {
    @Id @GeneratedValue
    @Column(name = "catagory_id")
    private Long id;

    private String name;

    private String image;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
