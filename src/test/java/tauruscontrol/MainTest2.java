package tauruscontrol;

import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.domain.terminal.TerminalManager;
import tauruscontrol.service.PowerManager;
import tauruscontrol.service.RebootManager;
import tauruscontrol.service.TerminalTimeManager;

public class MainTest2 {
    public static void main(String[] args) throws InterruptedException {
        TerminalManager terminalManager = new TerminalManager();
        System.out.println("터미널 검색 시작");
        terminalManager.searchTerminal();
        System.out.println("터미널 검색 완료");

        System.out.println("검색된 터미널 개수: " + terminalManager.getTerminalCount());

        Terminal firstTerminal = terminalManager.getTerminals().getFirst();
        System.out.println("첫 번째 터미널 로그인 시도");
        String sn = firstTerminal.getSn();
        terminalManager.loginTerminal(sn, "SN2008@+");
        System.out.println("로그인 상태: " + firstTerminal.isLogined());

        TerminalTimeManager terminalTimeManager = new TerminalTimeManager();
        System.out.println("터미널 시간 설정");
        terminalTimeManager.setCurrentTime(firstTerminal);

        RebootManager rebootManager = new RebootManager();
        System.out.println("reboot 스케쥴 설정");
        rebootManager.setRebootTask(firstTerminal, "0 0 6 ? * 1");

        System.out.println("reboot 스케쥴 조회");
        rebootManager.searchRebootTask(firstTerminal);

        PowerManager powerManager = new PowerManager();
        System.out.println("전원 관리 모드 설정");
        powerManager.setPowerMode(firstTerminal, "MANUALLY");

        System.out.println("전원 관리 모드 조회");
        powerManager.readPowerMode(firstTerminal);

        System.out.println("전원 상태 설정");
        powerManager.setPowerState(firstTerminal, "CLOSE");

        System.out.println("전원 상태 조회");
        powerManager.readPowerState(firstTerminal);

        System.out.println("전원 관리 스케쥴 설정");
        powerManager.setPowerSchedule(firstTerminal, "0 0 6 * * ?", "0 0 22 * * ?");

        System.out.println("AUTO면 스케쥴 조회");
        powerManager.readPowerSchedule(firstTerminal);
    }
}
