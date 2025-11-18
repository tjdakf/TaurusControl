package tauruscontrol;

import org.json.JSONArray;
import org.json.JSONObject;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

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

                String Schedule = CronParser.parse(cron);
                System.out.println(Schedule);
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetReBootTaskAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void setRebootTask(SDKManager sdk, Terminal terminal, String cron) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("set-reboot.json");
        obj.put("sn", terminal.getSn());
        obj.getJSONObject("taskInfo")
                .getJSONArray("conditions")
                .getJSONObject(0)
                .getJSONArray("cron")
                .put(cron);

        sdk.getViplexCore().nvSetReBootTaskAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }
}
