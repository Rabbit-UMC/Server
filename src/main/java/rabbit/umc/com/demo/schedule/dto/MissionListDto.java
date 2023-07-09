package rabbit.umc.com.demo.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rabbit.umc.com.demo.mission.Mission;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class MissionListDto {
    // 미션
    private Long missionId;
    private String missionTitle;

    // 미션 도전자수 미션 테이블 미션 유저 테이블 조인 해서 count
    private int challengerCnts;

    // 종료날짜 - 오늘 날짜
    private long dDAy;

    public static MissionListDto toMissionListDto(Mission mission){
        Calendar calendar = Calendar.getInstance();
        Timestamp currentTimestamp = new Timestamp(calendar.getTimeInMillis());

        Date targetDate = new Date(mission.getEndAt().getTime());
        Date currentDate = new Date(currentTimestamp.getTime());

        long timeDiff = targetDate.getTime() - currentDate.getTime();
        long dayDiff = timeDiff / (24 * 60 * 60 * 1000);

        return new MissionListDto(
            mission.getId(),
            mission.getTitle(),
            mission.getMissionUsers().size(),
            dayDiff
        );
    }

}
