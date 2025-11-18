import org.json.JSONObject;

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
}
