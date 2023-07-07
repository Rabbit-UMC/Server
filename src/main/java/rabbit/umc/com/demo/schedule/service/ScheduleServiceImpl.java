package rabbit.umc.com.demo.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.repository.MissionRepository;
import rabbit.umc.com.demo.schedule.Schedule;
import rabbit.umc.com.demo.schedule.dto.MissionListDto;
import rabbit.umc.com.demo.schedule.dto.ScheduleHomeRes;
import rabbit.umc.com.demo.schedule.dto.ScheduleListDto;
import rabbit.umc.com.demo.schedule.repository.ScheduleRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final MissionRepository missionRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, MissionRepository missionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.missionRepository = missionRepository;
    }

    @Override
    public ScheduleHomeRes getHome() {
        ScheduleHomeRes scheduleHomeRes = new ScheduleHomeRes();


        List<Schedule> scheduleList = scheduleRepository.getHome();
        scheduleHomeRes.setSceduleList(
                scheduleList.stream().map(ScheduleListDto::toScheduleDto).collect(Collectors.toList())
        );

        List<Mission> missionList = missionRepository.getHome();
        scheduleHomeRes.setMissionList(
                missionList.stream().map(MissionListDto::toMissionListDto).collect(Collectors.toList())
        );


        return scheduleHomeRes;
    }
}
