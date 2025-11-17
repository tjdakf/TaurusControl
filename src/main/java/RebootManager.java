import org.json.JSONArray;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.temporal.ChronoField;

public class RebootManager {
    public void searchRebootTask(SDKManager sdk, Terminal terminal) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }

                JSONObject obj = new JSONObject(data);
                if (obj.isEmpty()) {
                    System.out.println("재부팅 초기 설정되지 않았습니다.");
                    return;
                }

                JSONArray conditions = obj.getJSONArray("conditions");
                if (conditions.isEmpty()) {
                    System.out.println("재부팅 시간이 설정되지 않았습니다.");
                    return;
                }

                String cron = conditions.getJSONObject(0)
                        .getJSONArray("cron")
                        .getString(0);

                parseSchedule(cron);
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetReBootTaskAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    private void parseSchedule(String cron) {
        String[] parts = cron.split(" ");
        String minute = parts[1];
        String hour = parts[2];
        int dayOfWeek = Integer.parseInt(parts[5]) - 1; // SDK응답 SUN:1 - SAT:7
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        System.out.printf("%s %s시 %s분\n",
                DayOfWeek.of(dayOfWeek).name(), hour, minute);
    }
}
