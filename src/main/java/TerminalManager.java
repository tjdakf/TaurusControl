import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TerminalManager {
    private List<Terminal> terminals = new ArrayList<>();
    private Exception callbackException = null;

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

    public void loginTerminal(SDKManager sdk, String sn, String password) {
        callbackException = null;

        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code == 16) throw new IllegalArgumentException("비밀번호는 공란일 수 없습니다.");
                if (code == 65304) throw new IllegalStateException("터미널을 찾을 수 없습니다.");
                if (code == 65313) throw new IllegalArgumentException("SN번호를 입력해주세요.");
                if (code == 65351) throw new IllegalArgumentException("틀린 비밀번호입니다.");
                if (code == 65353) throw new IllegalArgumentException("이미 로그인되어 있어 다시 로그인할 수 없습니다.");
                if (code != 0) throw new IllegalStateException("data: " + data);
                JSONObject obj = new JSONObject(data);
                if (!obj.getBoolean("logined")) throw new IllegalStateException("다른 장치에서 사용중입니다.");

                terminals.stream()
                        .filter(terminal -> terminal.getSn().equals(sn))
                        .findFirst()
                        .ifPresent(terminal -> terminal.setLogined(true));
            } catch (Exception e) {
                callbackException = e;
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("login.json");
        obj.put("sn", sn);
        obj.put("password", password);

        sdk.getViplexCore().nvLoginAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();

        if (callbackException != null) {
            throw new RuntimeException("로그인 오류 발생", callbackException);
        }
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
                        .comparing(Terminal::isLogined).reversed()
                        .thenComparing(Terminal::getAliasName))
                .toList();
    }
}
