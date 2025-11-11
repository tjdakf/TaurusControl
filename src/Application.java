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
        JSONObject obj;
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

        // 초기화
        viplexCore.nvSetDevLang("Java");
        String companyInfo = loadTemplate("init-request.json");
        System.out.print("nvInit(SDK초기화):");
        System.out.println(viplexCore.nvInit(tempDir,companyInfo));

        /*
        // 프로그램 생성
        obj = new JSONObject(loadTemplate("create-program-request.json"));
        System.out.println("=== Create Program ===");
        viplexCore.nvCreateProgramAsync(obj.toString(), callBack);
        waitAPIReturn();

        // MD5 구하기
        String mediaPath1 = "{\"filePath\":\"./resources/1.jpg\"}";
        viplexCore.nvGetFileMD5Async(mediaPath1, callBack);
        String md5_1 = g_data;
        String mediaPath2 = "{\"filePath\":\"./resources/2.jpg\"}";
        viplexCore.nvGetFileMD5Async(mediaPath2, callBack);
        String md5_2 = g_data;

        // 프로그램 수정
        obj = new JSONObject(loadTemplate("edit-program-request.json"));
        System.out.println("=== Edit Program ===");
        viplexCore.nvSetPageProgramAsync(obj.toString(), callBack);
        waitAPIReturn();

        // 프로그램 저장(로컬)
        String generate = String.format("{\"programID\":1,\"outPutPath\":\"%s\",\"mediasPath\":[{\"oldPath\":\"./\",\"newPath\":\"./\"}]}", "./");
        System.out.println("=== Save Program ===");
        viplexCore.nvMakeProgramAsync(generate, callBack);
        waitAPIReturn();

        viplexCore.nvGetProgramAsync("", callBack);
        waitAPIReturn();

        viplexCore.nvDeleteProgramAsync("{\"programID\":[1]}", callBack);
        waitAPIReturn();

        viplexCore.nvGetProgramAsync("", callBack);
        waitAPIReturn();

        System.out.println("=== 장치 검색 시작 ===");
        viplexCore.nvSearchTerminalAsync(callBack);
        Thread.sleep(3000);
        g_bAPIReturn = false;

        if (g_data == null) {
            System.out.println("=== 장치 검색 실패 ===");
            return;
        }

        obj = new JSONObject(g_data);
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

        // 프로그램 전송
        obj = new JSONObject(loadTemplate("publish-request.json"));
        obj.put("sn", sn);
        System.out.println("=== 프로그램 전송 시작 ===");
        viplexCore.nvStartTransferProgramAsync(obj.toString(), callBack);
        waitAPIReturn();
        Thread.sleep(10000);
        System.out.println("=== 프로그램 전송 완료 ===");

        obj = new JSONObject(loadTemplate("logout-request.json"));
        obj.put("sn", sn);
        System.out.println("=== 장치 로그아웃 시작 ===");
        viplexCore.nvLogoutAsync(obj.toString(), callBack);
        waitAPIReturn();
        System.out.println("=== 로그아웃 완료 ===");
         */
    }

}
