package rabbit.umc.com.demo.article.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.Status;
<<<<<<< HEAD
=======
import rabbit.umc.com.demo.article.dto.PostArticleReq;
>>>>>>> 7fcf3bb43902111068ac93fc8de16eaacd49b844
import rabbit.umc.com.demo.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "article")
public class Article {
    @Id@GeneratedValue
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;

<<<<<<< HEAD
    @OneToMany(mappedBy = "article")
    private List<Comment> comments;

    @OneToMany(mappedBy = "article")
    private List<LikeArticle> likeArticles;

=======
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<LikeArticle> likeArticles;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

>>>>>>> 7fcf3bb43902111068ac93fc8de16eaacd49b844
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private Timestamp createdAt;
    @Column(nullable = false)
    private Timestamp updatedAt;


<<<<<<< HEAD


=======
    public void setArticle(PostArticleReq postArticleReq) {
        title = postArticleReq.getArticleTitle();
        content = postArticleReq.getArticleContent();
    }
>>>>>>> 7fcf3bb43902111068ac93fc8de16eaacd49b844
}
