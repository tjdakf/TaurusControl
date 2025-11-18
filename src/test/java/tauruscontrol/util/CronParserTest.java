package tauruscontrol.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CronParserTest {
    @Test
    void Cron_표현식을_파싱() {
        String cron = "0 0 9 * * 1";

        String result = CronParser.parse(cron);

        assertThat(result).isEqualTo("일 09시 00분");
    }

    @Test
    void 모든_요일이면_매일로_표시() {
        String cron1 = "0 0 9 * * 1,2,3,4,5,6,7";
        String cron2 = "0 0 12 ? * *";

        String result1 = CronParser.parse(cron1);
        String result2 = CronParser.parse(cron2);

        assertThat(result1).isEqualTo("매일 09시 00분");
        assertThat(result2).isEqualTo("매일 12시 00분");
    }

    @Test
    void 월화수목금이면_평일로_표시() {
        String cron = "0 0 9 * * 2,3,4,5,6";

        String result = CronParser.parse(cron);

        assertThat(result).isEqualTo("평일 09시 00분");
    }

    @Test
    void 토일이면_주말로_표시() {
        String cron = "0 0 9 * * 1,7";

        String result = CronParser.parse(cron);

        assertThat(result).isEqualTo("주말 09시 00분");
    }

    @Test
    void 여러_요일은_이어서_표시() {
        String cron = "0 0 9 * * 1,2,4,6";

        String result = CronParser.parse(cron);

        assertThat(result).isEqualTo("일월수금 09시 00분");
    }
}