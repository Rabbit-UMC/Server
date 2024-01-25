package rabbit.umc.com.demo.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rabbit.umc.com.config.apiPayload.BaseException;
import rabbit.umc.com.config.apiPayload.BaseResponseStatus;
import rabbit.umc.com.demo.mission.Mission;
import rabbit.umc.com.demo.mission.MissionUsers;
import rabbit.umc.com.demo.mission.repository.MissionRepository;
import rabbit.umc.com.demo.mission.repository.MissionUsersRepository;
import rabbit.umc.com.demo.schedule.domain.MissionSchedule;
import rabbit.umc.com.demo.schedule.domain.Schedule;
import rabbit.umc.com.demo.schedule.dto.*;
import rabbit.umc.com.demo.schedule.repository.MissionScheduleRepository;
import rabbit.umc.com.demo.schedule.repository.ScheduleRepository;
import rabbit.umc.com.demo.user.Domain.User;
import rabbit.umc.com.demo.user.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static rabbit.umc.com.config.apiPayload.BaseResponseStatus.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MissionRepository missionRepository;
    private final MissionScheduleRepository missionScheduleRepository;
    private final UserRepository userRepository;
    private final MissionUsersRepository missionUserRepository;

    /**
     * 일정 홈
     */
    @Override
    public ScheduleHomeRes getHome(Long userId) {
        ScheduleHomeRes scheduleHomeRes = new ScheduleHomeRes();
        // 일정 리스트
        List<Schedule> scheduleList = scheduleRepository.getSchedulesByUserIdOrderByEndAt(userId);

        scheduleHomeRes.setScheduleList(
                scheduleList.stream().map(ScheduleListDto::toScheduleDto).collect(Collectors.toList())
        );

        // 미션 유저 리스트
        List<MissionUsers> missionUsersList = missionUserRepository.getMissionUsersByUserId(userId);
        List<Mission> missionList = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();


        // 미션 유저 테이블의 미션 번호로 종료되지 않은 미션 찾기
        for (MissionUsers mu :missionUsersList) {
            Mission mission = missionRepository.getMissionByIdAndEndAtIsAfterOrderByEndAt(mu.getMission().getId(), currentDateTime);
            if(mission != null)
                missionList.add(mission);
        }

        if(missionList.isEmpty()){
            List<MissionListDto> missionListDto = new ArrayList<>();
            scheduleHomeRes.setMissionList(missionListDto);
        }else{
            // dDay 순으로 정렬
            Collections.sort(missionList,Comparator.comparing(mission ->
                    ChronoUnit.DAYS.between(currentDateTime, mission.getEndAt())));

            scheduleHomeRes.setMissionList(
                    missionList.stream()
                            .map(MissionListDto::toMissionListDto)
                            .collect(Collectors.toList())
            );
        }


        return scheduleHomeRes;
    }

    /**
     * 일정 상제 페이지
     */
    @Override
    public ScheduleDetailRes getScheduleDetail(Long scheduleId,Long userId) throws BaseException {

        MissionSchedule missionSchedule = missionScheduleRepository.getMissionScheduleByScheduleId(scheduleId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        // 해당 일정이 없을 때
        if(schedule == null || schedule.getUser().getId() != userId){
            throw new BaseException(BaseResponseStatus.FAILED_TO_SCHEDULE);
        } else{
            if(missionSchedule != null){
                missionSchedule.setSchedule(schedule);
            }else {
                throw new BaseException(BaseResponseStatus.FAILED_TO_SCHEDULE);
            }
        }

        // 일정에 미션이 없을 때
        if(missionSchedule.getMission() == null){
            missionSchedule.setMission(null);
        }else{
            Mission mission = missionRepository.getMissionById(missionSchedule.getMission().getId());
            missionSchedule.setMission(mission);
        }

        return ScheduleDetailRes.setMissionSchedule(missionSchedule);
    }

    /**
     * 일정 등록
     */
    @Override
    @Transactional
    public Long postSchedule(PostScheduleReq postScheduleReq, Long userId) throws BaseException {

        Schedule schedule = new Schedule();
        User user = userRepository.getReferenceById(userId);
        schedule.setSchedule(postScheduleReq);
        schedule.setUser(user);


        MissionSchedule missionSchedule = new MissionSchedule();

        // 미션 아이디가 있을 때
        if(postScheduleReq.getMissionId() != null){
            Mission findMission = missionRepository.getMissionById(postScheduleReq.getMissionId());
            if(findMission == null){
                throw new BaseException(FAILED_TO_MISSION);
            }

            List<MissionSchedule> missionScheduleList = missionScheduleRepository.getMissionScheduleByMissionId(postScheduleReq.getMissionId());

            for (MissionSchedule ms: missionScheduleList) {
                Schedule findSchedule = scheduleRepository.getScheduleByIdAndUserId(ms.getSchedule().getId(),userId);

                if(findSchedule != null){

                    if (postScheduleReq.getWhen().equals(findSchedule.getStartAt().toString().substring(0,10))){
                        throw new BaseException(BaseResponseStatus.FAILED_TO_POST_SCHEDULE);
                    }
                    // 미션 날짜 범위 안에 있는지 체크
                    LocalDate localDate = LocalDate.parse(postScheduleReq.getWhen());
                    LocalDate startDate = LocalDate.parse(findMission.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).minusDays(1);
                    LocalDate endDate = LocalDate.parse(findMission.getEndAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).plusDays(1);
                    if(!localDate.isAfter(startDate) || !localDate.isBefore(endDate))
                        throw new BaseException(FAILED_TO_SCHEDULE_DATE);
                }
                
            }

//            mission.setId(postScheduleReq.getMissionId());
            Mission mission = missionRepository.getMissionById(postScheduleReq.getMissionId());
            missionSchedule.setMission(mission);
        }


        // schedule에 내용들 저장
        scheduleRepository.save(schedule);

        missionSchedule.setSchedule(schedule);

        // missionSchedule 테이블에 일정 아이디랑 미션 아이디 저장
        missionScheduleRepository.save(missionSchedule);

        return schedule.getId();
    }

    @Override
    @Transactional
    public void deleteSchedule(List<Long> scheduleIds,Long userId) throws BaseException {
        List<Schedule> findSchedules = scheduleRepository.findSchedulesByIdsAndUserId(scheduleIds,userId);

        if(findSchedules.size() == 0 || findSchedules.size() != scheduleIds.size())
            throw new BaseException(BaseResponseStatus.FAILED_TO_SCHEDULE);

        for(Schedule schedule : findSchedules){
            if (!schedule.getUser().getId().equals(userId)) {
                throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
            }
        }


        findSchedules.forEach(
                schedule ->
                {
                    missionScheduleRepository.deleteByScheduleId(schedule.getId());
                    System.out.println("schedule.getId() = " + schedule.getId());
                    scheduleRepository.deleteById(schedule.getId());
                }
        );





    }


    @Override
    public List<ScheduleListDto> getScheduleByWhen(String when,long userId) {
        LocalDate localDate = LocalDate.parse(when);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        ScheduleHomeRes scheduleHomeRes = new ScheduleHomeRes();
        List<Schedule> scheduleList = scheduleRepository.getScheduleByWhenAndUserId(timestamp,userId);
        scheduleHomeRes.setScheduleList(
                scheduleList.stream().map(ScheduleListDto::toScheduleDto).collect(Collectors.toList())
        );

        return scheduleHomeRes.getScheduleList();
    }


    @Override
    @Transactional
    public void updateSchedule(PostScheduleReq patchScheduleReq, Long userId, Long scheduleId) throws BaseException {
        Schedule schedule = scheduleRepository.findScheduleByIdAndUserId(scheduleId, userId);
        if(schedule == null){
            throw new BaseException(BaseResponseStatus.FAILED_TO_SCHEDULE);
        }
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

    @Override
    public DayRes getScheduleWhenMonth(YearMonth yearMonth, long userId) throws BaseException {
        DayRes results = new DayRes();
        Map<Integer,Integer> map = new HashMap<>();
        List<Schedule> scheduleList = scheduleRepository.findSchedulesByMonth(yearMonth.getMonthValue(),userId,yearMonth.getYear());

        // 스케쥴 날짜 가져온거에서 각각 몇 개 잇는지
        scheduleList.forEach(s -> {
            Integer cnt = scheduleRepository.countByEndAtIs(s.getEndAt());
            results.setSchedulesOfDay(s.getEndAt().getDayOfMonth(),cnt);
        });

        if(results.getSchedulesOfDay().isEmpty())
                throw  new BaseException(EMPTY_SCHEDULE);
//        resultList.setDayList(scheduleList.stream().map(schedule -> schedule.getEndAt().getDayOfMonth()).distinct().collect(Collectors.toList()));

        return results;
    }


}
