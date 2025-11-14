import org.json.JSONObject;

public class Terminal {
    private String aliasName;
    private String sn;
    private int width;
    private int height;
    private boolean logined;
    private boolean hasPassword;

    public Terminal(JSONObject data) {
        this.aliasName = data.getString("aliasName");
        this.sn = data.getString("sn");
        this.width = data.getInt("width");
        this.height = data.getInt("height");
        this.logined = data.getBoolean("logined");
        this.hasPassword = data.getBoolean("hasPassword");
    }
}
