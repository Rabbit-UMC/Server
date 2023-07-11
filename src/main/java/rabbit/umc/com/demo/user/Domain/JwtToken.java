package rabbit.umc.com.demo.user.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "jwt")
public class JwtToken {
    @Id
    @GeneratedValue
    @Column(name = "jwt_id")
    private Long id;

    @Column
    private String accessToken;

    @Column
    private String refreshToken;

    @Column
    private Date accessTokenExp;

    @Column
    private Date refreshTokenExp;
    @Builder
    public JwtToken(String accessToken, String refreshToken, Date accessTokenExp, Date refreshTokenExp){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExp = accessTokenExp;
        this.refreshTokenExp = refreshTokenExp;
    }
}
