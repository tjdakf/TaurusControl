package tauruscontrol.sdk;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SDKConfigurationTest {

    @TempDir
    Path tempDirectory;

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty(SDKConfiguration.SDK_PATH_PROPERTY);
        System.clearProperty(SDKConfiguration.CREDENTIALS_PATH_PROPERTY);
    }

    @Test
    void systemProperty로_SDK_경로를_지정할_수_있다() {
        Path configuredDirectory = tempDirectory.resolve("sdk");
        System.setProperty(SDKConfiguration.SDK_PATH_PROPERTY, configuredDirectory.toString());

        Path result = SDKConfiguration.resolveSdkDirectory();

        assertThat(result).isEqualTo(configuredDirectory.toAbsolutePath().normalize());
    }

    @Test
    void SDK_핵심_라이브러리가_없으면_설정_방법을_안내한다() {
        Path sdkDirectory = tempDirectory.resolve("sdk");

        assertThatThrownBy(() -> SDKConfiguration.requireSdkDirectory(sdkDirectory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("viplexcore.dll")
                .hasMessageContaining(SDKConfiguration.SDK_PATH_ENVIRONMENT_VARIABLE);
    }

    @Test
    void SDK_핵심_라이브러리가_있으면_경로를_반환한다() throws IOException {
        Path sdkDirectory = Files.createDirectories(tempDirectory.resolve("sdk"));
        Files.createFile(sdkDirectory.resolve("viplexcore.dll"));

        Path result = SDKConfiguration.requireSdkDirectory(sdkDirectory);

        assertThat(result).isEqualTo(sdkDirectory.toAbsolutePath().normalize());
    }

    @Test
    void systemProperty로_credentials_경로를_지정할_수_있다() {
        Path credentialsPath = tempDirectory.resolve("credentials.json");
        System.setProperty(SDKConfiguration.CREDENTIALS_PATH_PROPERTY, credentialsPath.toString());

        Path result = SDKConfiguration.resolveCredentialsPath();

        assertThat(result).isEqualTo(credentialsPath.toAbsolutePath().normalize());
    }

    @Test
    void credentials_JSON을_외부_파일에서_로드한다() throws IOException {
        Path credentialsPath = tempDirectory.resolve("credentials.json");
        Files.writeString(credentialsPath, """
                {
                  "company": "example-company",
                  "phone": "010-0000-0000",
                  "email": "developer@example.com"
                }
                """);

        JSONObject result = SDKConfiguration.loadCredentials(credentialsPath);

        assertThat(result.getString("company")).isEqualTo("example-company");
        assertThat(result.getString("email")).isEqualTo("developer@example.com");
    }

    @Test
    void credentials_파일이_없으면_example_복사를_안내한다() {
        Path credentialsPath = tempDirectory.resolve("credentials.json");

        assertThatThrownBy(() -> SDKConfiguration.loadCredentials(credentialsPath))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("credentials.example.json")
                .hasMessageContaining(SDKConfiguration.CREDENTIALS_PATH_ENVIRONMENT_VARIABLE);
    }

    @Test
    void Windows_ARM64는_x64_SDK_지원_환경으로_판단하지_않는다() {
        String originalOperatingSystem = System.getProperty("os.name");
        String originalArchitecture = System.getProperty("os.arch");
        System.setProperty("os.name", "Windows 11");
        System.setProperty("os.arch", "aarch64");

        try {
            assertThatThrownBy(SDKConfiguration::validateSupportedPlatform)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Windows x64");
        } finally {
            restoreSystemProperty("os.name", originalOperatingSystem);
            restoreSystemProperty("os.arch", originalArchitecture);
        }
    }

    private void restoreSystemProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
            return;
        }
        System.setProperty(key, value);
    }
}
