package rabbit.umc.com.demo.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.dto.MissionHistoryRes;
import rabbit.umc.com.demo.mission.dto.MissionHomeRes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class UserMissionHistoryDto {
//    private int point; // 성공률 * 100
    private int missionCnt; // 전체 미션 수
    private int targetCnt; // 성공 또는 실패한 미션 수
    private List<UserMissionResDto> userMissionResDtos;




    public static UserMissionHistoryDto toSuccessMissionHistoryRes(int totalCnt, List<UserMissionResDto> userMissionResDtos){
        return new UserMissionHistoryDto(
                totalCnt,
                userMissionResDtos.size(),
                userMissionResDtos
        );
    }

    public static UserMissionHistoryDto toFailMissionHistoryRes(int totalCnt, List<UserMissionResDto> userMissionResDtos){
        return new UserMissionHistoryDto(
                totalCnt,
                userMissionResDtos.size(),
                userMissionResDtos
        );
    }


}
