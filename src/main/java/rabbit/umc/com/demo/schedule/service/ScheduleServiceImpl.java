package rabbit.umc.com.demo.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.repository.MissionRepository;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.*;
import rabbit.umc.com.demo.schedule.repository.MissionScheduleRepository;
import rabbit.umc.com.demo.schedule.repository.ScheduleRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final MissionRepository missionRepository;

    private final MissionScheduleRepository missionScheduleRepository;

    private final UserRepository userRepository;

    /**
     * 일정 홈
     */
    @Override
    public ScheduleHomeRes getHome() {
        ScheduleHomeRes scheduleHomeRes = new ScheduleHomeRes();


        List<Schedule> scheduleList = scheduleRepository.getHome();
        System.out.println("scheduleList = " + scheduleList);
        scheduleHomeRes.setScheduleList(
                scheduleList.stream().map(ScheduleListDto::toScheduleDto).collect(Collectors.toList())
        );

        List<Mission> missionList = missionRepository.getHome();
        System.out.println("missionList = " + missionList);
        scheduleHomeRes.setMissionList(
                missionList.stream().map(MissionListDto::toMissionListDto).collect(Collectors.toList())
        );


        return scheduleHomeRes;
    }

    /**
     * 일정 상제 페이지
     */
    @Override
    public ScheduleDetailRes getScheduleDetail(Long scheduleId) {

        MissionSchedule missionSchedule = missionScheduleRepository.getScheduleDetail(scheduleId);

        return ScheduleDetailRes.setMissionSchedule(missionSchedule);
    }

    @Override
    public Long postSchedule(PostScheduleReq postScheduleReq, Long userId) {
        Schedule schedule = new Schedule();
        User user = userRepository.findById(userId).get();

        schedule.setSchedule(postScheduleReq,user.getId());

        // schedule에 내용들 저장
        scheduleRepository.save(schedule);

        MissionSchedule missionSchedule = new MissionSchedule();
        missionSchedule.setMissionAndSchedule(postScheduleReq.getMissionId(),schedule.getId());

        // missionSchedule 테이블에 일정 아이디랑 미션 아이디 저장
        missionScheduleRepository.save(missionSchedule);

        return schedule.getId();
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }

    @Override
    public void patchSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    @Override
    public Schedule findById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).get();
    }

    @Override
    public List<ScheduleListDto> getScheduleByWhen(String when) throws ParseException {
        LocalDate localDate = LocalDate.parse(when);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        ScheduleHomeRes scheduleHomeRes = new ScheduleHomeRes();
        List<Schedule> scheduleList = scheduleRepository.getScheduleByWhen(timestamp);
        scheduleHomeRes.setScheduleList(
                scheduleList.stream().map(ScheduleListDto::toScheduleDto).collect(Collectors.toList())
        );

        return scheduleHomeRes.getScheduleList();
    }

}
