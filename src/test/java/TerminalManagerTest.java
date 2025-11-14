import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerminalManagerTest {
    @Test
    void 로그인_여부에_따라_정렬된다() {
        JSONObject data1 = new JSONObject();
        data1.put("aliasName", "A");
        data1.put("sn", "123456");
        data1.put("width", 400);
        data1.put("height", 400);
        data1.put("logined", false);
        data1.put("hasPassWord", false);

        JSONObject data2 = new JSONObject();
        data2.put("aliasName", "B");
        data2.put("sn", "123457");
        data2.put("width", 400);
        data2.put("height", 400);
        data2.put("logined", true);
        data2.put("hasPassWord", false);

        Terminal terminal1 = new Terminal(data1);
        Terminal terminal2 = new Terminal(data2);
        TerminalManager terminalManager = new TerminalManager();
        terminalManager.addTerminal(terminal1);
        terminalManager.addTerminal(terminal2);

        List<Terminal> result = terminalManager.getTerminals();

        assertEquals(terminal2, result.get(0));
        assertEquals(terminal1, result.get(1));
    }
}