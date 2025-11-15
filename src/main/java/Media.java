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

    private String extractFileName() {
        int nameStartIndex = path.lastIndexOf("/");
        int nameEndIndex = path.lastIndexOf(".");
        return path.substring(nameStartIndex + 1, nameEndIndex);
    }
}
