package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;
import rabbit.umc.com.demo.mainmission.domain.MainMission;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionProof;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMainMissionRes {
    private Long mainMissionId;
    private String mainMissionName;
    private String startDay;
    private String dDay;
    private String mainMissionContent;
    private List<RankDto> rank;
    private List<MissionProofImageDto> missionProofImages;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankDto {
        private Long userId;
        private String userName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionProofImageDto {
        private Long imageId;
        private Long userId;
        private String filePath;
        private Boolean isLike;

        public void setIsLike(){
            this.isLike = true;
        }
    }
}




