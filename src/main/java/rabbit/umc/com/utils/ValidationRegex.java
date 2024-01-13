package rabbit.umc.com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexDate(String target){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // false일 경우 처리시 입력한 값이 잘못된 형식일 시 오류 발생
            sdf.parse(target);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 월 입력 체크
     */
    public static boolean isRegexMonth(String target){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        try {
            YearMonth yearMonth = YearMonth.parse(target, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean checkStartedTimeAndEndedTime(String startedAt, String endedAt){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            if(sdf.parse(startedAt).after(sdf.parse(endedAt)))
                return true;
            else
                return false;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean checkStartedDateAndEndedDate(String startedDate,String endedDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (sdf.parse(startedDate).after(sdf.parse(endedDate)))
                return true;
            else
                return false;
        } catch (ParseException e) {
            return false;
        }
    }
}

