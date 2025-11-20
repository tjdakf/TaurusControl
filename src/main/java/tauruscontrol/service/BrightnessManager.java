package tauruscontrol.service;

import org.json.JSONObject;
import tauruscontrol.util.AsyncHelper;
import tauruscontrol.util.TemplateLoader;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

import java.util.function.Consumer;

public class BrightnessManager {
    private final SDKManager sdk;

    public BrightnessManager() {
        this.sdk = SDKManager.getInstance();
    }

    public void readLedBrightness(Terminal terminal,
                                  Consumer<Float> onSuccess,
                                  Consumer<String> onError) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    onError.accept("오류 코드: " + code);
                    return;
                }

                JSONObject obj = new JSONObject(data);
                float brightness = obj.getFloat("ratio");
                onSuccess.accept(brightness);
            } catch (Exception e) {
                onError.accept("알 수 없는 오류: " + e.getMessage());
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("terminal-request.json");
        obj.put("sn", terminal.getSn());

        sdk.getViplexCore().nvGetScreenBrightnessAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void setLedBrightness(Terminal terminal,
                                 float brightness,
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

        JSONObject obj = TemplateLoader.load("set-brightness.json");
        obj.put("sn", terminal.getSn());
        obj.getJSONObject("screenBrightnessInfo").put("ratio", brightness);

        sdk.getViplexCore().nvSetScreenBrightnessAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }
}
