package tauruscontrol.domain.terminal;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TerminalTest {

    @Test
    void JSON으로_터미널_생성() {
        JSONObject json = new JSONObject();
        json.put("aliasName", "TestTerminal");
        json.put("sn", "SN12345");
        json.put("width", 1920);
        json.put("height", 1080);
        json.put("logined", false);
        json.put("hasPassWord", true);
        json.put("password", "test123");
        json.put("terminalState", 0);

        Terminal terminal = new Terminal(json);

        assertThat(terminal.getAliasName()).isEqualTo("TestTerminal");
        assertThat(terminal.getSn()).isEqualTo("SN12345");
        assertThat(terminal.getWidth()).isEqualTo(1920);
        assertThat(terminal.getHeight()).isEqualTo(1080);
        assertThat(terminal.isLogined()).isFalse();
        assertThat(terminal.hasPassword()).isTrue();
        assertThat(terminal.getPassword()).isEqualTo("test123");
        assertThat(terminal.getTerminalState()).isEqualTo(0);
    }

    @Test
    void terminalState_3이면_이_앱에서_로그인() {
        JSONObject json = createBasicTerminalJson();
        json.put("terminalState", 3);

        Terminal terminal = new Terminal(json);

        assertThat(terminal.isLoginedByThisApp()).isTrue();
        assertThat(terminal.isLoginedByOtherDevice()).isFalse();
    }

    @Test
    void terminalState_2이면_다른_장치에서_로그인() {
        JSONObject json = createBasicTerminalJson();
        json.put("terminalState", 2);

        Terminal terminal = new Terminal(json);

        assertThat(terminal.isLoginedByThisApp()).isFalse();
        assertThat(terminal.isLoginedByOtherDevice()).isTrue();
    }

    @Test
    void terminalState_0이면_로그인_안됨() {
        JSONObject json = createBasicTerminalJson();
        json.put("terminalState", 0);

        Terminal terminal = new Terminal(json);

        assertThat(terminal.isLoginedByThisApp()).isFalse();
        assertThat(terminal.isLoginedByOtherDevice()).isFalse();
    }

    @Test
    void setLogined_true시_terminalState_업데이트() {
        JSONObject json = createBasicTerminalJson();
        json.put("terminalState", 0);

        Terminal terminal = new Terminal(json);
        terminal.setLogined(true);

        assertThat(terminal.isLogined()).isTrue();
        assertThat(terminal.getTerminalState()).isEqualTo(3);
        assertThat(terminal.isLoginedByThisApp()).isTrue();
    }

    @Test
    void setLogined_false시_terminalState_유지() {
        JSONObject json = createBasicTerminalJson();
        json.put("terminalState", 3);

        Terminal terminal = new Terminal(json);
        terminal.setLogined(false);

        assertThat(terminal.isLogined()).isFalse();
        assertThat(terminal.getTerminalState()).isEqualTo(3);
    }

    @Test
    void setTerminalState로_상태_직접_변경() {
        JSONObject json = createBasicTerminalJson();
        json.put("terminalState", 0);

        Terminal terminal = new Terminal(json);
        terminal.setTerminalState(2);

        assertThat(terminal.getTerminalState()).isEqualTo(2);
        assertThat(terminal.isLoginedByOtherDevice()).isTrue();
    }

    private JSONObject createBasicTerminalJson() {
        JSONObject json = new JSONObject();
        json.put("aliasName", "TestTerminal");
        json.put("sn", "SN12345");
        json.put("width", 1920);
        json.put("height", 1080);
        json.put("logined", false);
        json.put("hasPassWord", false);
        json.put("password", "");
        json.put("terminalState", 0);
        return json;
    }
}
