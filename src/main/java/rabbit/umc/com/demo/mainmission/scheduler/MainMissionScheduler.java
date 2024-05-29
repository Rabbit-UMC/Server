package rabbit.umc.com.demo.mainmission.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.demo.community.category.CategoryService;
import rabbit.umc.com.demo.mainmission.domain.MainMission;
import rabbit.umc.com.demo.mainmission.domain.mapping.MainMissionUsers;
import rabbit.umc.com.demo.mainmission.service.MainMissionService;
import rabbit.umc.com.demo.mainmission.service.MainMissionUserService;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.service.UserService;

@Component
@RequiredArgsConstructor
public class MainMissionScheduler {

    private final UserService userService;
    private final CategoryService categoryService;
    private final MainMissionService mainMissionService;
    private final MainMissionUserService mainMissionUserService;

    /**
     * 스케줄러
     * 묘방생 미션 종료시 권한 수정됨
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 스케줄 실행
    public void checkCompletedMainMissions() {
        List<MainMission> completedMissions = mainMissionService.getCompleteMissions();

        for (MainMission mainMission : completedMissions) {
            List<MainMissionUsers> topScorers = mainMissionUserService.getTop3UsersByMainMission(mainMission);
            if (!topScorers.isEmpty()) {
                // 이전 묘집사 강등
                User beforeHost = mainMission.getCategory().getUser();
                userService.changePermissionToUser(beforeHost);

                // 묘집사 임명
                User newHost = topScorers.get(0).getUser();
                userService.changePermissionToHost(newHost);

                //해당 카테고리 묘집사 변경
                categoryService.changeCategoryHost(mainMission, newHost);
                mainMissionService.closeMainMission(mainMission);
            }
        }
    }
}
