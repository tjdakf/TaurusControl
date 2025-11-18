package tauruscontrol.domain.media;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MediaManagerTest {
    @Test
    void 파일이_없으면_예외() {
        MediaManager mediaManager = new MediaManager();
        String path = "resources/media/5.jpg";

        assertThatThrownBy(() -> mediaManager.validateMedia(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파일이 존재하지 않습니다: resources/media/5.jpg");;

    }

    @Test
    void 지원하지_않는_확장자면_예외() {
        MediaManager mediaManager = new MediaManager();
        String path = "resources/media/5.txt";

        assertThatThrownBy(() -> mediaManager.validateMedia(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 파일 형식입니다: txt");;
    }
}