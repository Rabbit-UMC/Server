package rabbit.umc.com.demo.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.BaseException;
import rabbit.umc.com.config.BaseResponseStatus;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionUsers;
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
@Transactional(readOnly = true)
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

        MissionSchedule missionSchedule = missionScheduleRepository.getMissionScheduleByScheduleId(scheduleId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        Mission mission = new Mission();
        missionSchedule.setSchedule(schedule);
        if(missionSchedule.getMission() == null){
            missionSchedule.setMission(null);
        }else {
            mission = missionRepository.getMissionById(missionSchedule.getMission().getId());
        }
            missionSchedule.setMission(mission);

        return ScheduleDetailRes.setMissionSchedule(missionSchedule);
    }

    @Override
    @Transactional
    public Long postSchedule(PostScheduleReq postScheduleReq, Long userId) {
        Schedule schedule = new Schedule();
        System.out.println("userId = " + userId);
        User user = userRepository.getReferenceById(userId);
        schedule.setSchedule(postScheduleReq);
        schedule.setUser(user);
        System.out.println("schedule = " + schedule.getId());
        // schedule에 내용들 저장
        scheduleRepository.save(schedule);

        MissionSchedule missionSchedule = new MissionSchedule();
        Mission mission = new Mission();
        if(postScheduleReq.getMissionId() != null){
            mission.setId(postScheduleReq.getMissionId());
            missionSchedule.setMission(mission);
        }

        missionSchedule.setSchedule(schedule);


        // missionSchedule 테이블에 일정 아이디랑 미션 아이디 저장
        missionScheduleRepository.save(missionSchedule);

        return schedule.getId();
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId,Long userId) throws BaseException {
        Schedule findSchedule = scheduleRepository.findScheduleById(scheduleId);

        if (findSchedule==null){
            throw new BaseException(BaseResponseStatus.FAILED_TO_SCHEDULE);
        }

        if(!findSchedule.getUser().getId().equals(userId)){
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        }

        missionScheduleRepository.deleteByScheduleId(scheduleId);
        scheduleRepository.deleteById(scheduleId);

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

    @Override
    @Transactional
    public void updateSchedule(PostScheduleReq patchScheduleReq, Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        schedule.setSchedule(patchScheduleReq);
        MissionSchedule missionSchedule = missionScheduleRepository.findMissionScheduleByScheduleId(scheduleId);
        if(patchScheduleReq.getMissionId() != null){
            Mission mission = missionRepository.getReferenceById(patchScheduleReq.getMissionId());
            missionSchedule.setMission(mission);
        }else {
            missionSchedule.setMission(null);
        }
        missionScheduleRepository.save(missionSchedule);
        scheduleRepository.save(schedule);
    }


}
