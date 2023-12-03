package rabbit.umc.com.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static String calculateDDay(LocalDate endDateTime) {
        LocalDate currentDateTime = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(currentDateTime, endDateTime);

        if (daysRemaining > 0) {
            return "D-" + daysRemaining;
        } else if (daysRemaining == 0) {
            return "D-day";
        } else {
            return "D+" + Math.abs(daysRemaining);
        }
    }

    public static String makeArticleUploadTime (LocalDateTime createTime){
        LocalDateTime now = LocalDateTime.now();
        long yearsAgo = ChronoUnit.YEARS.between(createTime, now);
        String uploadTime;

        if (yearsAgo == 0) {
            long daysAgo = ChronoUnit.DAYS.between(createTime, now);

            if (daysAgo == 0) {
                uploadTime = createTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                uploadTime = createTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            }
        } else {
            uploadTime = yearsAgo + "년 전";
        }
        return uploadTime;
    }

    public static String getMissionDday(LocalDate missionEndAt){
        LocalDate currentDateTime = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(currentDateTime, missionEndAt);
        String dDay;
        if (daysRemaining >= 0) {
            return   daysRemaining + "일";
        }  else {
            return  "미션 종료";
        }
    }

}
