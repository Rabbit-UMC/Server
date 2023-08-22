package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;
import rabbit.umc.com.demo.mainmission.domain.MainMissionUsers;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RankDto {
    private Long userId;
    private String userName;



    //Setter
    public void setRanking(MainMissionUsers missionUsers){
        this.userId = missionUsers.getId();
        this.userName = missionUsers.getUser().getUserName();
    }
}
