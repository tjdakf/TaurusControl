package tauruscontrol;

import org.json.JSONArray;
import org.json.JSONObject;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

public class PowerManager {
    public void readPowerMode(SDKManager sdk, Terminal terminal) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }

                JSONObject obj = new JSONObject(data);
                System.out.println("전원 관리 모드: " + obj.getString("mode"));
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenPowerModeAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void readPowerState(SDKManager sdk, Terminal terminal) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }

                JSONObject obj = new JSONObject(data);
                System.out.println("전원 상태: " + obj.getString("state"));
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenPowerStateAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void readPowerSchedule(SDKManager sdk, Terminal terminal) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }

                JSONObject obj = new JSONObject(data);
                if (!obj.getBoolean("enable")) {
                    return;
                }

                JSONArray conditions = obj.getJSONArray("conditions");
                if (conditions.isEmpty()) {
                    System.out.println("스케쥴 설정 없음");
                    return;
                }

                for (int i = 0; i < conditions.length(); i++) {
                    JSONObject condition = conditions.getJSONObject(i);
                    String action = condition.getString("action");
                    String cron = condition.getJSONArray("cron").getString(0);
                    String schedule = CronParser.parse(cron);
                    System.out.println(action + " " + schedule);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenPowerPolicyAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void setPowerMode(SDKManager sdk, Terminal terminal, String mode) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("set-power-mode.json");
        obj.put("sn", terminal.getSn());
        obj.getJSONObject("taskInfo").put("mode", mode);

        sdk.getViplexCore().nvSetScreenPowerModeAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void setPowerState(SDKManager sdk, Terminal terminal, String state) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("set-power-state.json");
        obj.put("sn", terminal.getSn());
        obj.getJSONObject("taskInfo").put("state", state);

        sdk.getViplexCore().nvSetScreenPowerStateAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void setPowerSchedule(SDKManager sdk, Terminal terminal, String onCron, String offCron) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject onCondition = TemplateLoader.load("power-schedule-condition.json");
        onCondition.put("action", "OPEN");
        onCondition.getJSONArray("cron").put(0, onCron);
        JSONObject offCondition = TemplateLoader.load("power-schedule-condition.json");
        offCondition.put("action", "CLOSE");
        offCondition.getJSONArray("cron").put(0, offCron);
        JSONObject obj = TemplateLoader.load("set-power-schedule.json");
        obj.put("sn", terminal.getSn());
        obj.getJSONObject("taskInfo").getJSONArray("conditions").put(0, onCondition);
        obj.getJSONObject("taskInfo").getJSONArray("conditions").put(1, offCondition);

        sdk.getViplexCore().nvSetScreenPowerPolicyAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }
}
