package tauruscontrol.util;

public class AsyncHelper {
    private static final int WAIT_INTERVAL = 1000;

    static boolean apiReturn = false;

    public static void waitAPIReturn() {
        try {
            while (!apiReturn) {
                Thread.sleep(WAIT_INTERVAL);
            }
            apiReturn = false;
        } catch (InterruptedException e) {
            throw new RuntimeException("API 응답 대기 중 오류 발생", e);
        }
    }

    public static void setApiReturn(boolean apiReturn) {
        AsyncHelper.apiReturn = apiReturn;
    }
}
