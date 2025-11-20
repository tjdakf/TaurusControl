package tauruscontrol.service;

import org.json.JSONArray;
import org.json.JSONObject;
import tauruscontrol.util.AsyncHelper;
import tauruscontrol.util.CronParser;
import tauruscontrol.util.TemplateLoader;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

import java.util.function.Consumer;

public class RebootManager {
    private final SDKManager sdk;

    public RebootManager() {
        this.sdk = SDKManager.getInstance();
    }

    public void searchRebootTask(Terminal terminal,
                                 Consumer<RebootTimeData> onSuccess,
                                 Consumer<String> onError) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    onError.accept("오류 코드: " + code);
                    return;
                }

                JSONObject obj = new JSONObject(data);
                if (obj.isEmpty()) {
                    onError.accept("NOT_INITIALIZED");
                    return;
                }

                JSONArray conditions = obj.getJSONArray("conditions");
                if (conditions.isEmpty()) {
                    onError.accept("NO_TIME_SET");
                    return;
                }

                String cron = conditions.getJSONObject(0)
                        .getJSONArray("cron")
                        .getString(0);

                String displayText = CronParser.parse(cron);
                onSuccess.accept(new RebootTimeData(cron, displayText));
            } catch (Exception e) {
                onError.accept("알 수 없는 오류: " + e.getMessage());
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetReBootTaskAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void setRebootTask(Terminal terminal, String cron,
                              Runnable onSuccess,
                              Consumer<String> onError) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    onError.accept("설정 실패: " + code);
                    return;
                }
                onSuccess.run();
            } catch (Exception e) {
                onError.accept("알 수 없는 오류: " + e.getMessage());
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
                .put(0, cron);

        sdk.getViplexCore().nvSetReBootTaskAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public static class RebootTimeData {
        private final String cron;
        private final String displayText;

        public RebootTimeData(String cron, String displayText) {
            this.cron = cron;
            this.displayText = displayText;
        }

        public String getCron() {
            return cron;
        }

        public String getDisplayText() {
            return displayText;
        }
    }
}
