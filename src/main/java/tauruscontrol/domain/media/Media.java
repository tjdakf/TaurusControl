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
        this.path = path;
        this.md5 = md5;
        this.fileName = extractFileName();
        this.extension = path.substring(path.lastIndexOf("."));
        this.mediaType = MediaType.from(extension);
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
        int nameStartIndex = path.lastIndexOf("/");
        int nameEndIndex = path.lastIndexOf(".");
        return path.substring(nameStartIndex + 1, nameEndIndex);
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }
}
