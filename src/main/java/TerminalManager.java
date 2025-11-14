import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TerminalManager {
    private List<Terminal> terminals = new ArrayList<>();

    public void searchTerminal(SDKManager sdk) throws InterruptedException {
        ViplexCore.CallBack callBack = (code, data) -> {
            if (code != 0) {
                throw new IllegalStateException("data: " + data);
            }

            JSONObject obj = new JSONObject(data);
            terminals.add(new Terminal(obj));
        };

        sdk.getViplexCore().nvSearchTerminalAsync(callBack);
        Thread.sleep(3000);
    }

    public void addTerminal(Terminal terminal) {
        terminals.add(terminal);
    }

    public int getTerminalCount() {
        return terminals.size();
    }

    public List<Terminal> getTerminals() {
        return terminals.stream()
                .sorted(Comparator
                        .comparing(Terminal::isLogined).reversed())
                .toList();
    }
}
