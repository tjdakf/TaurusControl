import com.sun.jna.Native;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Application {
    static Boolean g_bAPIReturn = false;
    static int g_code;
    static String g_data;
    static String rootDir = System.getProperty("user.dir").replaceAll("\\\\", "/");
    static String jnaDir = rootDir + "/bin";
    static String tempDir = rootDir + "/temp";

    static void waitAPIReturn() throws InterruptedException {
        while (!g_bAPIReturn) {
            Thread.sleep(1000);
        }
        g_bAPIReturn = false;
    }

    public static String loadTemplate(String name) {
        try {
            return Files.readString(Paths.get(rootDir, "resources", name));
        } catch (IOException e) {
            throw new RuntimeException("템플릿 로드 실패: " + name, e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("jna.encoding", "UTF-8");
        System.setProperty("jna.library.path", jnaDir);
        ViplexCore viplexCore = (ViplexCore) Native.loadLibrary("viplexcore",ViplexCore.class);

        ViplexCore.CallBack callBack = new ViplexCore.CallBack() {
            @Override
            public void dataCallBack(int code, String data) {
                g_code = code;
                g_data = data;
                String strCode = "\nViplexCore Demo code:" + code;
                String strData = "\nViplexCore Demo data:" + data;
                System.out.println(strCode);
                System.out.println(strData);
                g_bAPIReturn=true;
            }
        };

        viplexCore.nvSetDevLang("Java");
        String companyInfo = loadTemplate("init-request.json");
        System.out.print("nvInit(SDK초기화):");
        System.out.println(viplexCore.nvInit(tempDir,companyInfo));

        System.out.println("=== 장치 검색 시작 ===");
        viplexCore.nvSearchTerminalAsync(callBack);
        Thread.sleep(3000);
        g_bAPIReturn = false;

        if (g_data == null) {
            System.out.println("=== 장치 검색 실패 ===");
            return;
        }

        JSONObject obj = new JSONObject(g_data);
        String sn = obj.getString("sn");
        obj = new JSONObject(loadTemplate("login-request.json"));
        obj.put("sn", sn);
        System.out.println("=== 장치에 로그인 시작 ===");
        viplexCore.nvLoginAsync(obj.toString(), callBack);
        waitAPIReturn();
        if (g_code != 0) {
            System.out.println("=== 로그인 실패 ===");
            return;
        }

        Thread.sleep(3000);

        obj = new JSONObject(loadTemplate("logout-request.json"));
        obj.put("sn", sn);
        System.out.println("=== 장치 로그아웃 시작 ===");
        viplexCore.nvLogoutAsync(obj.toString(), callBack);
        waitAPIReturn();
        System.out.println("=== 로그아웃 완료 ===");
    }
}
