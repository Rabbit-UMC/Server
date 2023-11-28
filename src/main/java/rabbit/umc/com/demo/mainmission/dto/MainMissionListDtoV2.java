package rabbit.umc.com.demo.mainmission.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MainMissionListDtoV2 {

    private Long mainMissionId;
    private String mainMissionTitle;
    private String dDay;
    private String hostUserName;


}
