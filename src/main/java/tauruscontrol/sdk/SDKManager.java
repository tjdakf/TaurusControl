package tauruscontrol.sdk;

import com.sun.jna.Native;
import org.json.JSONObject;
import tauruscontrol.util.TemplateLoader;

public class SDKManager {
    private static SDKManager instance;
    private ViplexCore viplexCore;

    private SDKManager() {
        System.setProperty("jna.encoding", "UTF-8");
        System.setProperty("jna.library.path", "bin");
        viplexCore = Native.loadLibrary("viplexcore",ViplexCore.class);
        initializeSDK();
    }

    public static SDKManager getInstance() {
        if (instance == null) {
            instance = new SDKManager();
        }
        return instance;
    }

    private void initializeSDK() {
        viplexCore.nvSetDevLang("Java");
        JSONObject credentials = TemplateLoader.load("credentials.json");

        if (viplexCore.nvInit("temp",credentials.toString()) != 0) {
            throw new RuntimeException("SDK 초기화 실패");
        }
    }

    public ViplexCore getViplexCore() {
        return viplexCore;
    }
}
