package rabbit.umc.com.demo.mainmission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainMissionViewRes {
    String userName;
    String missionImageUrl;
    String missionTitle;
    String missionStartDay;
    String missionEndDay;
    String memo;
}
