package tauruscontrol.domain.media;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTypeTest {
    @Test
    void 미디어_타입을_반환() {
        MediaType mp4 = MediaType.from(".mp4");
        MediaType png = MediaType.from(".png");
        MediaType gif = MediaType.from(".gif");

        String result1 = mp4.name();
        String result2 = png.name();
        String result3 = gif.name();

        assertThat(result1).isEqualTo("VIDEO");
        assertThat(result2).isEqualTo("PICTURE");
        assertThat(result3).isEqualTo("GIF");
    }

    @Test
    void 대문자로된_확장자도_가능() {
        MediaType mp4 = MediaType.from(".MP4");
        MediaType png = MediaType.from(".JPG");
        MediaType gif = MediaType.from(".GIF");

        String result1 = mp4.name();
        String result2 = png.name();
        String result3 = gif.name();

        assertThat(result1).isEqualTo("VIDEO");
        assertThat(result2).isEqualTo("PICTURE");
        assertThat(result3).isEqualTo("GIF");
    }
}