package tauruscontrol.util;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TemplateLoaderTest {

    @Test
    void 템플릿_파일_로딩_성공() {
        JSONObject result = TemplateLoader.load("login.json");

        assertThat(result).isNotNull();
        assertThat(result.has("sn")).isTrue();
        assertThat(result.has("password")).isTrue();
    }

    @Test
    void widget_템플릿_로딩_성공() {
        JSONObject result = TemplateLoader.load("widget.json");

        assertThat(result).isNotNull();
        assertThat(result.has("id")).isTrue();
        assertThat(result.has("type")).isTrue();
        assertThat(result.has("dataSource")).isTrue();
    }

    @Test
    void widgetcontainers_템플릿_로딩_성공() {
        JSONObject result = TemplateLoader.load("widgetcontainers.json");

        assertThat(result).isNotNull();
        assertThat(result.has("widgetContainers")).isTrue();
    }

    @Test
    void 존재하지_않는_템플릿_로딩시_예외() {
        assertThatThrownBy(() -> TemplateLoader.load("nonexistent.json"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("템플릿 로드 실패: nonexistent.json")
                .hasCauseInstanceOf(java.io.IOException.class);
    }

    @Test
    void 로딩한_JSON은_수정_가능() {
        JSONObject template = TemplateLoader.load("login.json");

        template.put("sn", "TEST123");
        template.put("password", "testpass");

        assertThat(template.getString("sn")).isEqualTo("TEST123");
        assertThat(template.getString("password")).isEqualTo("testpass");
    }

    @Test
    void UTF8_인코딩_처리() {
        JSONObject result = TemplateLoader.load("login.json");

        assertThat(result).isNotNull();
        String jsonString = result.toString();
        assertThat(jsonString).doesNotContain("�");
    }
}
