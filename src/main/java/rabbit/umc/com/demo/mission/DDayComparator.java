package rabbit.umc.com.demo.mission;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DDayComparator implements Comparator<String> {
    private static final Pattern pattern = Pattern.compile("D([+-]?\\d+)");

    @Override
    public int compare(String dDay1, String dDay2) {
        int value1 = extractNumber(dDay1);
        int value2 = extractNumber(dDay2);
        return Integer.compare(value1, value2);
    }

    private int extractNumber(String dDay) {
        Matcher matcher = pattern.matcher(dDay);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0; // 일치하는 숫자가 없을 경우 0 반환
    }
}
