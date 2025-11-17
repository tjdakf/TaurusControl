import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TerminalTimeManager {
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
        ZoneId zone = ZoneId.of("Asia/Seoul");
        long utcTimeMillis = now.toEpochMilli();
        String currentTime = LocalDateTime.ofInstant(now, zone)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        JSONObject obj = TemplateLoader.load("set-time.json");
        obj.put("sn", terminal.getSn());
        obj.put("currentTime", currentTime);
        obj.getJSONObject("timeZoneInfo").put("utcTimeMillis", utcTimeMillis);

        sdk.getViplexCore().nvCalibrateTimeAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }
}
