package tauruscontrol.service;

import org.json.JSONObject;
import tauruscontrol.util.AsyncHelper;
import tauruscontrol.util.TemplateLoader;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TerminalTimeManager {
    private static final String DEFAULT_ZONEID = "Asia/Seoul";
    private static final String TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public void setCurrentTime(SDKManager sdk, Terminal terminal) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
            AsyncHelper.setApiReturn(true);
            }
        };

        Instant now = Instant.now();
        ZoneId zone = ZoneId.of(DEFAULT_ZONEID);
        long utcTimeMillis = now.toEpochMilli();
        String currentTime = LocalDateTime.ofInstant(now, zone)
                .format(DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN));


        JSONObject obj = TemplateLoader.load("set-time.json");
        obj.put("sn", terminal.getSn());
        obj.put("currentTime", currentTime);
        obj.getJSONObject("timeZoneInfo").put("utcTimeMillis", utcTimeMillis);

        sdk.getViplexCore().nvCalibrateTimeAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }
}
