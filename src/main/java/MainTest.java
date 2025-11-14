public class MainTest {
    public static void main(String[] args) throws InterruptedException {
        SDKManager sdkManager = SDKManager.getInstance();
        System.out.println("SDK 초기화 완료");

        TerminalManager terminalManager = new TerminalManager();
        terminalManager.searchTerminal(sdkManager);
        System.out.println("터미널 검색 완료");

        System.out.println(terminalManager.getTerminalCount());
    }
}
