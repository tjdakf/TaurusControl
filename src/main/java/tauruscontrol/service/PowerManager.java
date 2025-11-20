package tauruscontrol.service;

import org.json.JSONArray;
import org.json.JSONObject;
import tauruscontrol.util.AsyncHelper;
import tauruscontrol.util.TemplateLoader;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

import java.util.ArrayList;
import java.util.List;

public class PowerManager {
    private final SDKManager sdk;
    private final Terminal terminal;

    public PowerManager(Terminal terminal) {
        this.sdk = SDKManager.getInstance();
        this.terminal = terminal;
    }

    public String readPowerMode() {
        final String[] result = new String[1];

        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }

                JSONObject obj = new JSONObject(data);
                result[0] = obj.getString("mode");
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenPowerModeAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();

        return result[0];
    }

    public String readPowerState() {
        final String[] result = new String[1];

        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }

                JSONObject obj = new JSONObject(data);
                result[0] = obj.getString("state");
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenPowerStateAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();

        return result[0];
    }

    public List<ScheduleEntry> readPowerSchedule() {
        final List<ScheduleEntry> scheduleList = new ArrayList<>();

        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }

                JSONObject obj = new JSONObject(data);
                if (obj.has("conditions")) {
                    JSONArray conditions = obj.getJSONArray("conditions");

                    for (int i = 0; i < conditions.length(); i++) {
                        JSONObject condition = conditions.getJSONObject(i);
                        String action = condition.getString("action");
                        JSONArray cronArray = condition.getJSONArray("cron");
                        if (cronArray.length() > 0) {
                            String cron = cronArray.getString(0);
                            scheduleList.add(new ScheduleEntry(action, cron));
                        }
                    }
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenPowerPolicyAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();

        return scheduleList;
    }

    public void setPowerMode(String mode) {
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

    public void setPowerState(String state) {
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

    public void setPowerSchedule(List<ScheduleEntry> schedules) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("set-power-schedule.json");
        obj.put("sn", terminal.getSn());

        JSONArray conditions = obj.getJSONObject("taskInfo").getJSONArray("conditions");
        for (ScheduleEntry schedule : schedules) {
            JSONObject condition = TemplateLoader.load("power-schedule-condition.json");
            condition.put("action", schedule.getAction());
            condition.getJSONArray("cron").put(0, schedule.getCron());
            conditions.put(condition);
        }

        sdk.getViplexCore().nvSetScreenPowerPolicyAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public static class ScheduleEntry {
        private final String action;
        private final String cron;

        public ScheduleEntry(String action, String cron) {
            this.action = action;
            this.cron = cron;
        }

        public String getAction() {
            return action;
        }

        public String getCron() {
            return cron;
        }
    }
}
