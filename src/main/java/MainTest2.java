public class MainTest2 {
    public static void main(String[] args) throws InterruptedException {
        SDKManager sdkManager = SDKManager.getInstance();
        System.out.println("SDK 초기화 완료");

        System.out.println("터미널 검색 시작");
        TerminalManager terminalManager = new TerminalManager();
        terminalManager.searchTerminal(sdkManager);
        System.out.println("터미널 검색 완료");

        System.out.println("검색된 터미널 개수: " + terminalManager.getTerminalCount());

        Terminal firstTerminal = terminalManager.getTerminals().getFirst();
        System.out.println("첫 번째 터미널 로그인 시도");
        String sn = firstTerminal.getSn();
        terminalManager.loginTerminal(sdkManager, sn, "SN2008@+");
        System.out.println("로그인 상태: " + firstTerminal.isLogined());

        System.out.println("터미널 시간 설정");
        TerminalTimeManager terminalTimeManager = new TerminalTimeManager();
        terminalTimeManager.setCurrentTime(sdkManager, firstTerminal);

        System.out.println("reboot 스케쥴 설정");
        RebootManager rebootManager = new RebootManager();
        rebootManager.setRebootTask(sdkManager, firstTerminal, "0 0 6 ? * 1");

        System.out.println("reboot 스케쥴 조회");
        rebootManager.searchRebootTask(sdkManager, firstTerminal);
    }
}
