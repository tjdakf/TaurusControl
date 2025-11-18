package tauruscontrol;

import tauruscontrol.sdk.SDKManager;

public class MainTest2 {
    public static void main(String[] args) throws InterruptedException {
        SDKManager sdkManager = SDKManager.getInstance();
        System.out.println("SDK 초기화 완료");

        TerminalManager terminalManager = new TerminalManager();
        System.out.println("터미널 검색 시작");
        terminalManager.searchTerminal(sdkManager);
        System.out.println("터미널 검색 완료");

        System.out.println("검색된 터미널 개수: " + terminalManager.getTerminalCount());

        Terminal firstTerminal = terminalManager.getTerminals().getFirst();
        System.out.println("첫 번째 터미널 로그인 시도");
        String sn = firstTerminal.getSn();
        terminalManager.loginTerminal(sdkManager, sn, "SN2008@+");
        System.out.println("로그인 상태: " + firstTerminal.isLogined());

        TerminalTimeManager terminalTimeManager = new TerminalTimeManager();
        System.out.println("터미널 시간 설정");
        terminalTimeManager.setCurrentTime(sdkManager, firstTerminal);

        RebootManager rebootManager = new RebootManager();
        System.out.println("reboot 스케쥴 설정");
        rebootManager.setRebootTask(sdkManager, firstTerminal, "0 0 6 ? * 1");

        System.out.println("reboot 스케쥴 조회");
        rebootManager.searchRebootTask(sdkManager, firstTerminal);

        PowerManager powerManager = new PowerManager();
        System.out.println("전원 관리 모드 설정");
        powerManager.setPowerMode(sdkManager, firstTerminal, "MANUALLY");

        System.out.println("전원 관리 모드 조회");
        powerManager.readPowerMode(sdkManager,  firstTerminal);

        System.out.println("전원 상태 설정");
        powerManager.setPowerState(sdkManager, firstTerminal, "CLOSE");

        System.out.println("전원 상태 조회");
        powerManager.readPowerState(sdkManager,  firstTerminal);

        System.out.println("전원 관리 스케쥴 설정");
        powerManager.setPowerSchedule(sdkManager, firstTerminal, "0 0 6 * * ?", "0 0 22 * * ?");

        System.out.println("AUTO면 스케쥴 조회");
        powerManager.readPowerSchedule(sdkManager,  firstTerminal);
    }
}
