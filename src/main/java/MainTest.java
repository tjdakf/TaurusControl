public class MainTest {
    public static void main(String[] args) throws InterruptedException {
        SDKManager sdkManager = SDKManager.getInstance();
        System.out.println("SDK 초기화 완료");

        System.out.println("터미널 검색 시작");
        TerminalManager terminalManager = new TerminalManager();
        terminalManager.searchTerminal(sdkManager);
        System.out.println("터미널 검색 완료");

        System.out.println("검색된 터미널 개수: " + terminalManager.getTerminalCount());

        System.out.println("첫 번째 터미널 로그인 시도");
        String sn = terminalManager.getTerminals().getFirst().getSn();
        terminalManager.loginTerminal(sdkManager, sn, "SN2008@+");
        System.out.println("로그인 상태: " + terminalManager.getTerminals().getFirst().isLogined());

        ProgramManager programManager = new ProgramManager();
        System.out.println("프로그램ID 반환: " + programManager.findOrCreateProgramId(sdkManager, terminalManager.getTerminals().getFirst()));
    }
}
