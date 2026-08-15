package tauruscontrol.sdk;

import com.sun.jna.Native;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;

public class SDKManager {
    private static final String SDK_OUT_PATH = System.getProperty("user.home")
            + File.separator + "TaurusControl" + File.separator + "temp";


    private static SDKManager instance;
    private ViplexCore viplexCore;

    private SDKManager() {
        System.setProperty("jna.encoding", "UTF-8");

        SDKConfiguration.validateSupportedPlatform();
        Path sdkDirectory = SDKConfiguration.requireSdkDirectory(SDKConfiguration.resolveSdkDirectory());
        System.setProperty("jna.library.path", sdkDirectory.toString());

        try {
            viplexCore = Native.load("viplexcore", ViplexCore.class);
        } catch (UnsatisfiedLinkError e) {
            throw new IllegalStateException(
                    "NovaStar T-SDK를 로드하지 못했습니다. SDK 경로와 하위 DLL 의존성을 확인하세요: " + sdkDirectory,
                    e
            );
        }

        initializeSDK();
    }

    public static SDKManager getInstance() {
        if (instance == null) {
            instance = new SDKManager();
        }
        return instance;
    }

    private void initializeSDK() {
        // temp 디렉토리 생성 (존재하지 않는 경우)
        File tempDir = new File(SDK_OUT_PATH);
        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                throw new RuntimeException("temp 디렉토리 생성 실패: " + SDK_OUT_PATH);
            }
        }

        viplexCore.nvSetDevLang("Java");
        JSONObject credentials = SDKConfiguration.loadCredentials(SDKConfiguration.resolveCredentialsPath());

        if (viplexCore.nvInit(SDK_OUT_PATH, credentials.toString()) != 0) {
            throw new RuntimeException("SDK 초기화 실패");
        }
    }

    public ViplexCore getViplexCore() {
        return viplexCore;
    }
}
