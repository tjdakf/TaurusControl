package tauruscontrol;

import tauruscontrol.domain.media.MediaManager;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.domain.terminal.TerminalManager;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.service.ProgramManager;

public class MainTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("터미널 검색 시작");
        TerminalManager terminalManager = new TerminalManager();
        terminalManager.searchTerminal();
        System.out.println("터미널 검색 완료");

        System.out.println("검색된 터미널 개수: " + terminalManager.getTerminalCount());

        Terminal firstTerminal = terminalManager.getTerminals().getFirst();
        System.out.println("첫 번째 터미널 로그인 시도");
        String sn = firstTerminal.getSn();
        terminalManager.loginTerminal(sn, "SN2008@+");
        System.out.println("로그인 상태: " + firstTerminal.isLogined());

        ProgramManager programManager = new ProgramManager();
        System.out.println("프로그램ID 반환: " + programManager.findOrCreateProgramId(firstTerminal));

        System.out.println("미디어 파일 추가");
        MediaManager mediaManager = new MediaManager();
        String mediaPath1 = "resources/media/1.jpg";
        mediaManager.addMedia(mediaPath1);

        System.out.println("프로그램 수정");
        programManager.editProgram(mediaManager);

        System.out.println("프로그램 로컬 저장 ./temp/program/");
        programManager.saveProgramToLocal();

        System.out.println("프로그램 전송");
        programManager.publishProgram(firstTerminal, mediaManager);
        Thread.sleep(3000);
    }
}
