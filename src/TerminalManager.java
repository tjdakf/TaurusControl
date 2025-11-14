import org.json.JSONObject;

import java.util.ArrayList;
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
}
