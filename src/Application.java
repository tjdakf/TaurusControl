import com.sun.jna.Native;

public class Application {
    static Boolean g_bAPIReturn = false;
    static int g_code = 0;
    static String g_sn = "BZSA79353N1310006847";

    static void waitAPIReturn() throws InterruptedException {
        while (!g_bAPIReturn) {
            Thread.sleep(1000);
        }
        g_bAPIReturn = false;
    }

    public static void main(String[] args) throws InterruptedException {
        String rootDir = System.getProperty("user.dir").replaceAll("\\\\", "/");
        String jnaDir = rootDir + "/bin";
        String tempDir = rootDir + "/temp";
        String companyInfo = "{\"company\":\"tjdakf\",\"phone\":\"010-1234-5678\",\"email\":\"ksc73450056@gmail.com\"}";

        System.setProperty("jna.encoding", "UTF-8");
        System.setProperty("jna.library.path", jnaDir);
        ViplexCore viplexCore = (ViplexCore) Native.loadLibrary("viplexcore",ViplexCore.class);
        ViplexCore.CallBack callBack = new ViplexCore.CallBack() {
            @Override
            public void dataCallBack(int code, String data) {
                g_code = code;
                String strCode = "\nViplexCore Demo code:" + code;
                String strData = "\nViplexCore Demo data:" + data;
                System.out.println(strCode);
                System.out.println(strData);
                g_bAPIReturn=true;
            }
        };

        viplexCore.nvSetDevLang("Java");
        System.out.print("nvInit(SDK초기화):");
        System.out.println(viplexCore.nvInit(tempDir,companyInfo));
        System.out.println("ViplexCore Demo nvSearchTerminalAsync(搜索) begin... ");
        viplexCore.nvSearchTerminalAsync(callBack);
        Thread.sleep(3000);
        g_bAPIReturn = false;
    }
}
