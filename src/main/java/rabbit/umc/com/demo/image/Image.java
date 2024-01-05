package rabbit.umc.com.demo.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import javax.persistence.*;
import rabbit.umc.com.demo.community.domain.Article;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
public class Image extends BaseTimeEntity {
    @Id@GeneratedValue(strategy = IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String imageName;
    private String s3ImageName;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",nullable = false)
    private Article article;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE ;

}
