package tauruscontrol;

public class CronParser {
    private static final String[] DAY_OF_WEEK = {
            "", "일", "월", "화", "수", "목", "금", "토"
    };

    public static String parse(String cron) {
        String[] parts = cron.split(" ");
        String minute = parts[1];
        String hour = parts[2];
        String dayOfWeekPart = parts[5];

        String dayOfWeek = parseWeekdays(dayOfWeekPart);

        return String.format("%s %s시 %s분", dayOfWeek, padZero(hour), padZero(minute));
    }

    private static String parseWeekdays(String dayOfWeekPart) {
        if (dayOfWeekPart.equals("?") || dayOfWeekPart.equals("*")) {
            return "매일";
        }

        if (dayOfWeekPart.equals("1,2,3,4,5,6,7")) {
            return "매일";
        }

        if (dayOfWeekPart.equals("2,3,4,5,6")) {
            return "평일";
        }

        if (dayOfWeekPart.equals("1,7") || dayOfWeekPart.equals("7,1")) {
            return "주말";
        }

        String[] days = dayOfWeekPart.split(",");
        StringBuilder result = new StringBuilder();
        for (String s : days) {
            int day = Integer.parseInt(s);
            result.append(DAY_OF_WEEK[day]);
        }

        return result.toString();
    }

    private static String padZero(String time) {
        int t = Integer.parseInt(time);
        return String.format("%02d", t);
    }
}
