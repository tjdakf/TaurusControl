package tauruscontrol.domain.terminal;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TerminalManagerTest {
    @Test
    void 로그인_여부에_따라_그리고_이름_순으로_정렬된다() {
        JSONObject data1 = new JSONObject();
        data1.put("aliasName", "A");
        data1.put("sn", "1234561");
        data1.put("width", 400);
        data1.put("height", 400);
        data1.put("logined", false);
        data1.put("hasPassWord", false);

        JSONObject data2 = new JSONObject();
        data2.put("aliasName", "C");
        data2.put("sn", "1234562");
        data2.put("width", 400);
        data2.put("height", 400);
        data2.put("logined", true);
        data2.put("hasPassWord", false);

        JSONObject data3 = new JSONObject();
        data3.put("aliasName", "B");
        data3.put("sn", "1234563");
        data3.put("width", 400);
        data3.put("height", 400);
        data3.put("logined", true);
        data3.put("hasPassWord", false);

        Terminal terminal1 = new Terminal(data1);
        Terminal terminal2 = new Terminal(data2);
        Terminal terminal3 = new Terminal(data3);
        TerminalManager terminalManager = new TerminalManager();
        terminalManager.addTerminal(terminal1);
        terminalManager.addTerminal(terminal2);
        terminalManager.addTerminal(terminal3);

        List<Terminal> result = terminalManager.getTerminals();

        assertThat(result).containsExactly(terminal3, terminal2, terminal1);
    }
}