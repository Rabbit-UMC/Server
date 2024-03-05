package rabbit.umc.com.demo.mission.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckMissionSuccessOrFail {
    private List<Long> ids;
    private int totalCnt;

    public CheckMissionSuccessOrFail(List<Long> ids, int totalCnt) {
        this.ids = ids;
        this.totalCnt = totalCnt;
    }
}
