import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaManager {
    private static final List<String> VALID_EXTENSIONS = List.of("mp4", "avi", "jpg", "png", "gif");
    private List<Media> medias = new ArrayList<>();
    private String md5;

    public void addMedia(SDKManager sdk, String path) {
        validateMedia(path);
        String md5 = calculateMD5(sdk,  path);
        medias.add(new Media(path, md5));
    }

    public void validateMedia(String path) {
        validatePath(path);
        validateExtension(path);
    }

    public JSONObject buildWidgetContainers() {
        JSONObject obj = TemplateLoader.load("widgetcontainers.json");

        for (int i = 0; i < medias.size(); i++) {
            JSONObject widget = medias.get(i).toJSON();
            widget.put("id", i + 1);
            obj.getJSONArray("widgetContainers")
                    .getJSONObject(0)
                    .getJSONObject("contents")
                    .getJSONArray("widgets")
                    .put(widget);
        }

        return obj;
    }

    public JSONObject buildMediasPath() {
        JSONObject obj = new JSONObject();
        for (Media media : medias) {
            obj.put(media.getPath(), media.getFileName() + media.getExtension());
        }

        return obj;
    }

    private void validatePath(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다: " + path);
        }
    }

    private void validateExtension(String path) {
        String extension = path.substring(path.lastIndexOf('.') + 1);
        if (!VALID_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + extension);
        }
    }

    private String calculateMD5(SDKManager sdk, String path) {
        ViplexCore.CallBack callBack = (code, data) -> {
            md5 = data;

            AsyncHelper.setApiReturn(true);
        };

        JSONObject obj = new JSONObject();
        obj.put("filePath", path);

        sdk.getViplexCore().nvGetFileMD5Async(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();

        return md5;
    }
}
