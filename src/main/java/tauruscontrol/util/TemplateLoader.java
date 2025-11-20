package tauruscontrol.util;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TemplateLoader {
    public static JSONObject load(String name) {
        try {
            InputStream stream = TemplateLoader.class.getResourceAsStream("/templates/" + name);
            if (stream == null) {
                throw new IOException("템플릿을 찾을 수 없습니다: " + name);
            }
            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("템플릿 로드 실패: " + name, e);
        }
    }
}

