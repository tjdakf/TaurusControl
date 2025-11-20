package tauruscontrol.domain.terminal;

import org.json.JSONObject;

public class Terminal {
    private String aliasName;
    private String sn;
    private int width;
    private int height;
    private boolean logined;
    private boolean hasPassword;
    private String password;
    private int terminalState;

    public Terminal(JSONObject data) {
        this.aliasName = data.getString("aliasName");
        this.sn = data.getString("sn");
        this.width = data.getInt("width");
        this.height = data.getInt("height");
        this.logined = data.getBoolean("logined");
        this.hasPassword = data.getBoolean("hasPassWord");
        this.password = data.getString("password");
        this.terminalState = data.getInt("terminalState");
    }

    public boolean isLogined() {
        return logined;
    }

    public int getTerminalState() {
        return terminalState;
    }

    public boolean isLoginedByThisApp() {
        return terminalState == 3;
    }

    public boolean isLoginedByOtherDevice() {
        return terminalState == 2;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getSn() {
        return sn;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPassword() {
        return password;
    }

    public boolean hasPassword() {
        return hasPassword;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
        if (logined) {
            this.terminalState = 3;  // 이 앱에서 로그인됨
        }
    }

    public void setTerminalState(int terminalState) {
        this.terminalState = terminalState;
    }
}
