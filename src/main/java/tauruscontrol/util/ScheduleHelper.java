package tauruscontrol.util;

import tauruscontrol.service.PowerManager.ScheduleEntry;
import tauruscontrol.view.components.TimeSettingDialog.ScheduleTime;

import java.util.*;
import java.util.stream.Collectors;

public class ScheduleHelper {

    private static final String[] DAY_NAMES = {"", "일", "월", "화", "수", "목", "금", "토"};

    public static String createCron(int hour, int minute, Set<Integer> days) {
        String dayStr;

        if (days.size() == 7) {
            dayStr = "*";
        } else {
            dayStr = days.stream()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        return String.format("00 %02d %02d ? * %s", minute, hour, dayStr);
    }

    public static List<ScheduleEntry> normalizeAndOptimize(List<ScheduleEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return new ArrayList<>();
        }

        List<ScheduleEntry> normalized = new ArrayList<>();
        for (ScheduleEntry entry : entries) {
            normalized.add(new ScheduleEntry(entry.getAction(), normalizeCron(entry.getCron())));
        }

        List<DaySchedule> allDaySchedules = new ArrayList<>();
        for (int day = 1; day <= 7; day++) {
            allDaySchedules.addAll(simulateDayTimeline(day, normalized));
        }

        return regroupToCron(allDaySchedules);
    }

    private static String normalizeCron(String cron) {
        String[] parts = cron.split(" ");
        if (parts.length < 6) {
            return cron;
        }

        String second = parts[0].equals("0") ? "00" : parts[0];
        String minute = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        String hour = parts[2].length() == 1 ? "0" + parts[2] : parts[2];
        String dayOfMonth = "?";
        String month = "*";
        String dayOfWeek = parts[5];

        return String.format("%s %s %s %s %s %s", second, minute, hour, dayOfMonth, month, dayOfWeek);
    }

    private static List<DaySchedule> simulateDayTimeline(int day, List<ScheduleEntry> entries) {
        List<DaySchedule> dayEvents = new ArrayList<>();

        for (ScheduleEntry entry : entries) {
            String[] parts = entry.getCron().split(" ");
            String dayPart = parts[5];

            List<Integer> days = parseDays(dayPart);
            if (days.contains(day)) {
                int hour = Integer.parseInt(parts[2]);
                int minute = Integer.parseInt(parts[1]);
                dayEvents.add(new DaySchedule(day, hour, minute, entry.getAction()));
            }
        }

        dayEvents.sort(Comparator.comparingInt((DaySchedule s) -> s.hour * 60 + s.minute));

        List<DaySchedule> cleaned = new ArrayList<>();
        for (DaySchedule event : dayEvents) {
            if (cleaned.isEmpty()) {
                cleaned.add(event);
            } else {
                DaySchedule last = cleaned.get(cleaned.size() - 1);
                if (last.action.equals(event.action)) {
                    if (last.action.equals("OPEN")) {
                        continue;
                    } else {
                        cleaned.set(cleaned.size() - 1, event);
                    }
                } else {
                    cleaned.add(event);
                }
            }
        }

        return cleaned;
    }

    private static List<ScheduleEntry> regroupToCron(List<DaySchedule> daySchedules) {
        Map<Integer, List<DaySchedule>> byDay = new HashMap<>();
        for (DaySchedule schedule : daySchedules) {
            byDay.computeIfAbsent(schedule.day, k -> new ArrayList<>()).add(schedule);
        }

        Map<String, Set<Integer>> pairGroups = new HashMap<>();

        for (Map.Entry<Integer, List<DaySchedule>> entry : byDay.entrySet()) {
            int day = entry.getKey();
            List<DaySchedule> schedules = entry.getValue();

            for (int i = 0; i < schedules.size(); i++) {
                DaySchedule schedule = schedules.get(i);
                if (schedule.action.equals("OPEN")) {
                    DaySchedule nextOff = null;
                    for (int j = i + 1; j < schedules.size(); j++) {
                        if (schedules.get(j).action.equals("CLOSE")) {
                            nextOff = schedules.get(j);
                            break;
                        }
                    }

                    if (nextOff != null) {
                        String pairKey = String.format("%02d:%02d:ON-%02d:%02d:OFF",
                                schedule.hour, schedule.minute, nextOff.hour, nextOff.minute);
                        pairGroups.computeIfAbsent(pairKey, k -> new HashSet<>()).add(day);
                    }
                }
            }
        }

        List<ScheduleEntry> result = new ArrayList<>();
        for (Map.Entry<String, Set<Integer>> entry : pairGroups.entrySet()) {
            String[] parts = entry.getKey().split("[-:]");
            int onHour = Integer.parseInt(parts[0]);
            int onMinute = Integer.parseInt(parts[1]);
            int offHour = Integer.parseInt(parts[3]);
            int offMinute = Integer.parseInt(parts[4]);
            Set<Integer> days = entry.getValue();

            String onCron = createCron(onHour, onMinute, days);
            result.add(new ScheduleEntry("OPEN", onCron));

            String offCron = createCron(offHour, offMinute, days);
            result.add(new ScheduleEntry("CLOSE", offCron));
        }

        return sortForDisplay(result);
    }

    private static List<ScheduleEntry> sortForDisplay(List<ScheduleEntry> entries) {
        entries.sort(Comparator.comparing((ScheduleEntry e) -> {
            String[] parts = e.getCron().split(" ");
            String dayPart = parts[5];

            int dayPriority;
            if (dayPart.equals("?")) {
                dayPriority = 0;
            } else if (dayPart.equals("2,3,4,5,6")) {
                dayPriority = 1;
            } else if (dayPart.equals("1,7")) {
                dayPriority = 2;
            } else {
                List<Integer> days = parseDays(dayPart);
                dayPriority = 3 + (days.isEmpty() ? 0 : days.get(0));
            }

            int hour = Integer.parseInt(parts[2]);
            int minute = Integer.parseInt(parts[1]);

            return dayPriority * 1000000 + hour * 10000 + minute * 100 + (e.getAction().equals("OPEN") ? 0 : 1);
        }));

        return entries;
    }

    public static String formatScheduleDisplay(ScheduleEntry entry) {
        String[] parts = entry.getCron().split(" ");
        int hour = Integer.parseInt(parts[2]);
        int minute = Integer.parseInt(parts[1]);
        String dayPart = parts[5];

        String action = entry.getAction().equals("OPEN") ? "ON" : "OFF";
        String dayName = formatDayPattern(dayPart);

        return String.format("%s %02d시 %02d분 %s", dayName, hour, minute, action);
    }

    private static String formatDayPattern(String dayPart) {
        if (dayPart.equals("?") || dayPart.equals("*")) {
            return "매일";
        }

        if (dayPart.equals("2,3,4,5,6")) {
            return "평일";
        }

        if (dayPart.equals("1,7")) {
            return "주말";
        }

        List<Integer> days = parseDays(dayPart);
        if (days.size() == 1) {
            return DAY_NAMES[days.get(0)];
        }

        return days.stream()
                .sorted()
                .map(d -> DAY_NAMES[d])
                .collect(Collectors.joining(""));
    }

    private static List<Integer> parseDays(String dayPart) {
        if (dayPart.equals("?") || dayPart.equals("*")) {
            return Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        }

        if (dayPart.contains(",")) {
            return Arrays.stream(dayPart.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        return Collections.singletonList(Integer.parseInt(dayPart));
    }

    public static boolean hasTimeConflict(ScheduleTime onTime, ScheduleTime offTime) {
        if (onTime.getHour() != offTime.getHour() || onTime.getMinute() != offTime.getMinute()) {
            return false;
        }

        Set<Integer> commonDays = new HashSet<>(onTime.getDays());
        commonDays.retainAll(offTime.getDays());

        return !commonDays.isEmpty();
    }

    public static List<ScheduleEntry> mergeSchedules(List<ScheduleEntry> existing, ScheduleTime newOn, ScheduleTime newOff) {
        List<ScheduleEntry> combined = new ArrayList<>(existing);

        String onCron = createCron(newOn.getHour(), newOn.getMinute(), newOn.getDays());
        combined.add(new ScheduleEntry("OPEN", onCron));

        String offCron = createCron(newOff.getHour(), newOff.getMinute(), newOff.getDays());
        combined.add(new ScheduleEntry("CLOSE", offCron));

        return normalizeAndOptimize(combined);
    }

    public static List<ScheduleEntry> removeSchedulePair(List<ScheduleEntry> schedules, int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= schedules.size()) {
            return schedules;
        }

        List<ScheduleEntry> result = new ArrayList<>(schedules);
        ScheduleEntry selected = result.get(selectedIndex);
        int pairIndex = -1;

        if (selected.getAction().equals("OPEN")) {
            if (selectedIndex + 1 < result.size() && result.get(selectedIndex + 1).getAction().equals("CLOSE")) {
                pairIndex = selectedIndex + 1;
            }
        } else {
            if (selectedIndex - 1 >= 0 && result.get(selectedIndex - 1).getAction().equals("OPEN")) {
                pairIndex = selectedIndex - 1;
            }
        }

        if (pairIndex == -1) {
            return schedules;
        }

        result.remove(Math.max(selectedIndex, pairIndex));
        result.remove(Math.min(selectedIndex, pairIndex));

        return result;
    }

    public static SchedulePair getSchedulePair(List<ScheduleEntry> schedules, int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= schedules.size()) {
            return null;
        }

        ScheduleEntry selected = schedules.get(selectedIndex);
        ScheduleEntry pair = null;
        int pairIndex = -1;

        if (selected.getAction().equals("OPEN")) {
            if (selectedIndex + 1 < schedules.size() && schedules.get(selectedIndex + 1).getAction().equals("CLOSE")) {
                pair = schedules.get(selectedIndex + 1);
                pairIndex = selectedIndex + 1;
            }
        } else {
            if (selectedIndex - 1 >= 0 && schedules.get(selectedIndex - 1).getAction().equals("OPEN")) {
                pair = schedules.get(selectedIndex - 1);
                pairIndex = selectedIndex - 1;
            }
        }

        if (pair == null) {
            return null;
        }

        ScheduleEntry on = selected.getAction().equals("OPEN") ? selected : pair;
        ScheduleEntry off = selected.getAction().equals("CLOSE") ? selected : pair;

        String[] onParts = on.getCron().split(" ");
        String[] offParts = off.getCron().split(" ");

        Set<Integer> onDays = new HashSet<>(parseDays(onParts[5]));
        Set<Integer> offDays = new HashSet<>(parseDays(offParts[5]));

        return new SchedulePair(
            new ScheduleTime(Integer.parseInt(onParts[2]), Integer.parseInt(onParts[1]), onDays),
            new ScheduleTime(Integer.parseInt(offParts[2]), Integer.parseInt(offParts[1]), offDays),
            Math.min(selectedIndex, pairIndex)
        );
    }

    public static List<ScheduleEntry> updateSchedulePair(List<ScheduleEntry> schedules,
                                                         int selectedIndex,
                                                         ScheduleTime newOn,
                                                         ScheduleTime newOff) {
        List<ScheduleEntry> afterRemove = removeSchedulePair(schedules, selectedIndex);
        return mergeSchedules(afterRemove, newOn, newOff);
    }

    private static List<DaySchedule> expandToDaySchedules(ScheduleEntry entry) {
        String[] parts = entry.getCron().split(" ");
        int hour = Integer.parseInt(parts[2]);
        int minute = Integer.parseInt(parts[1]);
        String dayPart = parts[5];

        List<Integer> days = parseDays(dayPart);

        List<DaySchedule> result = new ArrayList<>();
        for (int day : days) {
            result.add(new DaySchedule(day, hour, minute, entry.getAction()));
        }
        return result;
    }

    private static class DaySchedule {
        final int day;
        final int hour;
        final int minute;
        final String action;

        DaySchedule(int day, int hour, int minute, String action) {
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.action = action;
        }
    }

    public static class SchedulePair {
        private final ScheduleTime onTime;
        private final ScheduleTime offTime;
        private final int index;

        public SchedulePair(ScheduleTime onTime, ScheduleTime offTime, int index) {
            this.onTime = onTime;
            this.offTime = offTime;
            this.index = index;
        }

        public ScheduleTime getOnTime() {
            return onTime;
        }

        public ScheduleTime getOffTime() {
            return offTime;
        }

        public int getIndex() {
            return index;
        }
    }
}
