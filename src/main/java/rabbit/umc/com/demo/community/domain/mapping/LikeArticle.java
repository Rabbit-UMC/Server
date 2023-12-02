package rabbit.umc.com.demo.community.domain.mapping;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.community.domain.Article;
import rabbit.umc.com.demo.user.Domain.User;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "like_article")
public class LikeArticle extends BaseTimeEntity {
    @Id@GeneratedValue(strategy = IDENTITY)
    @Column(name = "like_article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",nullable = false)
    private Article article;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;


    //Setter
    public void setLikeArticle(User user, Article article){
        this.user = user;
        this.article = article;
    }
}
