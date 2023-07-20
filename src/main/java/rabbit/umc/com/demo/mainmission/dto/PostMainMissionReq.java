package rabbit.umc.com.demo.mainmission.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PostMainMissionReq {
    private String mainMissionTitle;
    private String mainMissionContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate missionStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate missionEndTime;

    private Boolean lastMission;
}
