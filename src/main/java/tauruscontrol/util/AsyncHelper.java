package tauruscontrol.util;

public class AsyncHelper {
    private static final int WAIT_INTERVAL = 1000;

    // volatile: 멀티스레드 환경에서 메모리 가시성 보장
    // SDK 콜백(다른 스레드)에서 설정한 값을 waitAPIReturn()에서 즉시 확인 가능
    private static volatile boolean apiReturn = false;

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
