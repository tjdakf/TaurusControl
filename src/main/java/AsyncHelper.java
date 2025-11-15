public class AsyncHelper {
    static boolean apiReturn = false;

    public static void waitAPIReturn() {
        try {
            while (!apiReturn) {
                Thread.sleep(1000);
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
