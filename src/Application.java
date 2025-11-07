import com.sun.jna.Native;

public class Application {

    public static void main(String[] args) {
        System.setProperty("jna.encoding", "UTF-8");
        System.setProperty("jna.library.path", System.getProperty("user.dir") + "\\bin");
        ViplexCore viplexCore = (ViplexCore) Native.loadLibrary("viplexcore",ViplexCore.class);
    }
}
