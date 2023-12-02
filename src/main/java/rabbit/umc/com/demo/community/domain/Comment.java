package rabbit.umc.com.demo.community.domain;

import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.config.BaseTimeEntity;
import rabbit.umc.com.demo.Status;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static rabbit.umc.com.demo.Status.INACTIVE;

@Entity
@Getter
@Table(name = "comments")
public class Comment extends BaseTimeEntity {

    private static final String GOOD_COMMENT_MESSAGE = "착한 말을 쓰자!";

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comments_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public void setComment(Article article, User user, String content){
        this.article = article;
        this.user = user;
        this.content = content;
    }
    //비즈니스 로직
    public void lockComment(){
        this.status = Status.INACTIVE;
    }

    public String getCommentContent(){
        if (this.getStatus() == INACTIVE){
            return GOOD_COMMENT_MESSAGE;
        }
        return this.getContent();
    }

}
