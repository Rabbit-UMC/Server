package rabbit.umc.com.demo.community.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rabbit.umc.com.demo.base.BaseTimeEntity;
import rabbit.umc.com.demo.base.Status;
import rabbit.umc.com.demo.user.Domain.User;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static rabbit.umc.com.demo.base.Status.INACTIVE;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    @Builder.Default
    private Status status = Status.ACTIVE;

    //비즈니스 로직

    @PostPersist
    public void updateCommentCountUp(){
        this.article.updateCommentCountUp();
    }

    @PreRemove
    public void updateCommentCountDown(){
        this.article.updateCommentCountDown();
    }

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
