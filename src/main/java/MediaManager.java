import java.io.File;
import java.util.List;

public class MediaManager {
    private static final List<String> VALID_EXTENSIONS = List.of("mp4", "avi", "jpg", "png", "gif");

    public void validateMedia(String path) {
        validatePath(path);
        validateExtension(path);
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
}
