package tauruscontrol.domain.media;

public enum MediaType {
    VIDEO, PICTURE, GIF;

    public static MediaType from(String extension) {
        String ext = extension.toLowerCase();
        if (ext.equals(".mp4") || ext.equals(".avi")) {
            return VIDEO;
        }
        if (ext.equals(".gif")) {
            return GIF;
        }
        return PICTURE;
    }
}
