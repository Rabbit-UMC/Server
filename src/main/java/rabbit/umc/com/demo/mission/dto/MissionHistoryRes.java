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


    public static MissionHistoryRes toSuccessMissionHistoryRes(int totalCnt, List<MissionHomeRes> missionHomeResList){
        return new MissionHistoryRes(
                (int) ((missionHomeResList.size() / (double) totalCnt) * 100), // 여기서 이런식으로 하니까 성공이랑 실패랑 다름
                totalCnt,
                missionHomeResList.size(),
                missionHomeResList
        );
    }

    public static MissionHistoryRes toFailMissionHistoryRes(int totalCnt, List<MissionHomeRes> missionFailList){
        return new MissionHistoryRes(
                (int) (((totalCnt - missionFailList.size()) / (double) totalCnt) * 100), // 여기서 이런식으로 하니까 성공이랑 실패랑 다름
                totalCnt,
                missionFailList.size(),
                missionFailList
        );
    }
}
