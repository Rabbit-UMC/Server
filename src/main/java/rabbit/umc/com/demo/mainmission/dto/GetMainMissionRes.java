package rabbit.umc.com.demo.mainmission.dto;

import lombok.*;

import java.util.List;

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
        private int likeCount;
        public void setIsLike(){
            this.isLike = true;
        }
    }
}




