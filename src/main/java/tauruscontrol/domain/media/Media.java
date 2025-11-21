package tauruscontrol.domain.media;

import org.json.JSONObject;
import tauruscontrol.util.TemplateLoader;

public class Media {
    private String path;
    private String md5;
    private String fileName;
    private String extension;
    private MediaType mediaType;

    public Media(String path, String md5) {
        this.path = normalizePath(path);
        this.md5 = md5;
        this.fileName = extractFileName();
        this.extension = this.path.substring(this.path.lastIndexOf("."));
        this.mediaType = MediaType.from(extension);
    }

    private String normalizePath(String path) {
        // Windows 백슬래시를 Unix 스타일 슬래시로 변환
        // SDK/FTP는 보통 forward slash를 선호
        return path.replace('\\', '/');
    }

    public JSONObject toJSON() {
        JSONObject media = TemplateLoader.load("widget.json");
        media.put("originalDataSource", path);
        media.put("dataSource", md5 + extension);
        media.put("name", fileName + extension);
        media.put("type", mediaType.name());
        return media;
    }

    private String extractFileName() {
        // 이미 normalizePath()에서 슬래시로 통일했으므로 슬래시만 찾으면 됨
        int slashIndex = path.lastIndexOf("/");
        int nameEndIndex = path.lastIndexOf(".");
        return path.substring(slashIndex + 1, nameEndIndex);
    }

    public String getPath() {
        return path;
    }

    public String getMd5() {
        return md5;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }
}
