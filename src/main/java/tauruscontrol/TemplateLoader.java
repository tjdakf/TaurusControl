package tauruscontrol;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateLoader {
    public static JSONObject load(String name) {
        try {
            return new JSONObject(Files.readString(Paths.get("resources", "templates", name)));
        } catch (IOException e) {
            throw new RuntimeException("템플릿 로드 실패: " + name, e);
        }
    }
}

