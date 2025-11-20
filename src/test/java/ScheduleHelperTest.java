import org.junit.jupiter.api.Test;
import tauruscontrol.service.PowerManager.ScheduleEntry;
import tauruscontrol.util.ScheduleHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleHelperTest {

    @Test
    public void 매일_스케쥴이_올바르게_인식된다() {
        List<ScheduleEntry> entries = new ArrayList<>();
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * ?"));
        entries.add(new ScheduleEntry("CLOSE", "0 0 20 * * ?"));

        List<ScheduleEntry> result = ScheduleHelper.normalizeAndOptimize(entries);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getCron().contains("?"));
        assertEquals("매일 06시 00분 ON", ScheduleHelper.formatScheduleDisplay(result.get(0)));
    }

    @Test
    public void 중복_OPEN이_제거된다() {
        List<ScheduleEntry> entries = new ArrayList<>();
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * ?"));
        entries.add(new ScheduleEntry("OPEN", "0 0 14 * * ?"));
        entries.add(new ScheduleEntry("CLOSE", "0 0 20 * * ?"));

        List<ScheduleEntry> result = ScheduleHelper.normalizeAndOptimize(entries);

        assertEquals(2, result.size());
        assertEquals("OPEN", result.get(0).getAction());
        assertEquals("CLOSE", result.get(1).getAction());
        assertTrue(result.get(0).getCron().contains("06"));
    }

    @Test
    public void 중복_CLOSE는_마지막만_남는다() {
        List<ScheduleEntry> entries = new ArrayList<>();
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * ?"));
        entries.add(new ScheduleEntry("CLOSE", "0 0 14 * * ?"));
        entries.add(new ScheduleEntry("CLOSE", "0 0 20 * * ?"));

        List<ScheduleEntry> result = ScheduleHelper.normalizeAndOptimize(entries);

        assertEquals(2, result.size());
        assertTrue(result.get(1).getCron().contains("20"));
    }

    @Test
    public void 평일_패턴이_올바르게_인식된다() {
        List<ScheduleEntry> entries = new ArrayList<>();
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * 2"));
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * 3"));
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * 4"));
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * 5"));
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * 6"));
        entries.add(new ScheduleEntry("CLOSE", "0 0 20 * * 2,3,4,5,6"));

        List<ScheduleEntry> result = ScheduleHelper.normalizeAndOptimize(entries);

        String display = ScheduleHelper.formatScheduleDisplay(result.get(0));
        assertTrue(display.contains("평일"));
    }

    @Test
    public void 주말_패턴이_올바르게_인식된다() {
        List<ScheduleEntry> entries = new ArrayList<>();
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * 1"));
        entries.add(new ScheduleEntry("OPEN", "0 0 6 * * 7"));
        entries.add(new ScheduleEntry("CLOSE", "0 0 20 * * 1,7"));

        List<ScheduleEntry> result = ScheduleHelper.normalizeAndOptimize(entries);

        String display = ScheduleHelper.formatScheduleDisplay(result.get(0));
        assertTrue(display.contains("주말"));
    }

    @Test
    public void CRON_정규화가_올바르게_작동한다() {
        List<ScheduleEntry> entries = new ArrayList<>();
        entries.add(new ScheduleEntry("OPEN", "0 5 6 * * ?"));

        List<ScheduleEntry> result = ScheduleHelper.normalizeAndOptimize(entries);

        assertTrue(result.get(0).getCron().startsWith("00 05 06"));
    }
}
