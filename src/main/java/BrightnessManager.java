import org.json.JSONObject;

public class BrightnessManager {
    public void readLedBrightness(SDKManager sdk, Terminal terminal) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException("밝기 조회 실패");
                }

                JSONObject obj = new JSONObject(data);
                float brightness = obj.getFloat("ratio");
                System.out.printf("LED 현재 밝기: %f%%\n", brightness);
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenBrightnessAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }
}
