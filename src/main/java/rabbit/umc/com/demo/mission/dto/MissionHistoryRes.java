package rabbit.umc.com.demo.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MissionHistoryRes {
    private int point; // 성공률 * 100
    private int missionCnt; // 전체 미션 수
    private int targetCnt; // 성공 또는 실패한 미션 수
    private List<MissionHomeRes> missionHomeResList;


    public static MissionHistoryRes toMissionHistoryRes(int totalCnt, List<MissionHomeRes> missionHomeResList){
        return new MissionHistoryRes(
                (int) ((missionHomeResList.size() / (double) totalCnt) * 100),
                totalCnt,
                missionHomeResList.size(),
                missionHomeResList
        );
    }
}
