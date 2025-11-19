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

    public Terminal(JSONObject data) {
        this.aliasName = data.getString("aliasName");
        this.sn = data.getString("sn");
        this.width = data.getInt("width");
        this.height = data.getInt("height");
        this.logined = data.getBoolean("logined");
        this.hasPassword = data.getBoolean("hasPassWord");
        this.password = data.getString("password");
    }

    public boolean isLogined() {
        return logined;
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
    }
}
