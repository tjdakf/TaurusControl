package tauruscontrol.domain.media;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTest {
    @Test
    void 위젯_양식의_JSON_데이터로_변환() {
        Media media = new Media("resources/media/1.jpg", "TESTMD5");

        JSONObject result = media.toJSON();
        JSONObject expected = new JSONObject("{\n" +
                "  \"id\": 1,\n" +
                "  \"enable\": true,\n" +
                "  \"repeatCount\": 1,\n" +
                "  \"layout\": {\n" +
                "    \"y\": \"0\",\n" +
                "    \"height\": \"100%\",\n" +
                "    \"x\": \"0\",\n" +
                "    \"width\": \"100%\"\n" +
                "  },\n" +
                "  \"backgroundColor\": \"#00000000\",\n" +
                "  \"backgroundDrawable\": \"\",\n" +
                "  \"backgroundMusic\": \"\",\n" +
                "  \"zOrder\": 0,\n" +
                "  \"displayRatio\": \"FULL\",\n" +
                "  \"outAnimation\": {\n" +
                "    \"type\": 0,\n" +
                "    \"duration\": 0\n" +
                "  },\n" +
                "  \"dataSource\": \"TESTMD5.jpg\",\n" +
                "  \"type\": \"PICTURE\",\n" +
                "  \"constraints\": [\n" +
                "    {\n" +
                "      \"cron\": [\n" +
                "      ],\n" +
                "      \"endTime\": \"4017-12-30T23:59:59Z+8:00\",\n" +
                "      \"startTime\": \"1970-01-01T00:00:00Z+8:00\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"border\": {\n" +
                "    \"borderThickness\": \"2px,3px,5%,6\",\n" +
                "    \"style\": 0,\n" +
                "    \"backgroundColor\": \"#ff000000\",\n" +
                "    \"name\": \"border\",\n" +
                "    \"cornerRadius\": \"2%\",\n" +
                "    \"effects\": {\n" +
                "      \"headTailSpacing\": \"\",\n" +
                "      \"isHeadTail\": false,\n" +
                "      \"speedByPixelEnable\": true,\n" +
                "      \"speed\": 0,\n" +
                "      \"animation\": \"CLOCK_WISE\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"inAnimation\": {\n" +
                "    \"type\": 0,\n" +
                "    \"duration\": 1000\n" +
                "  },\n" +
                "  \"duration\": 5000,\n" +
                "  \"name\": \"1.jpg\",\n" +
                "  \"originalDataSource\": \"resources/media/1.jpg\",\n" +
                "  \"functionStorage\": \"\",\n" +
                "  \"isSupportSpecialEffects\": false\n" +
                "}");

        JSONAssert.assertEquals(expected, result, true);
    }

    @Test
    void 경로에서_파일_이름_추출() {
        Media media = new Media("resources/media/test.jpg", "TESTMD5");

        String result = media.getFileName();

        assertThat(result).isEqualTo("test");
    }
}