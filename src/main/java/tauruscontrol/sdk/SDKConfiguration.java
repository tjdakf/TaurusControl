package tauruscontrol.sdk;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class SDKConfiguration {
    public static final String SDK_PATH_PROPERTY = "tauruscontrol.sdk.path";
    public static final String SDK_PATH_ENVIRONMENT_VARIABLE = "TAURUSCONTROL_SDK_PATH";
    public static final String CREDENTIALS_PATH_PROPERTY = "tauruscontrol.credentials.path";
    public static final String CREDENTIALS_PATH_ENVIRONMENT_VARIABLE = "TAURUSCONTROL_CREDENTIALS_PATH";

    private static final String CORE_LIBRARY_FILE = "viplexcore.dll";

    private SDKConfiguration() {
    }

    public static Path resolveSdkDirectory() {
        String configuredPath = firstNonBlank(
                System.getProperty(SDK_PATH_PROPERTY),
                System.getenv(SDK_PATH_ENVIRONMENT_VARIABLE)
        );
        if (configuredPath != null) {
            return normalize(configuredPath);
        }

        Path packagedSdkDirectory = Path.of(System.getProperty("java.home"))
                .resolve("..")
                .resolve("app")
                .resolve("native")
                .normalize()
                .toAbsolutePath();
        if (Files.isRegularFile(packagedSdkDirectory.resolve(CORE_LIBRARY_FILE))) {
            return packagedSdkDirectory;
        }

        return Path.of("sdk", "win32-x86-64").toAbsolutePath().normalize();
    }

    public static Path resolveCredentialsPath() {
        String configuredPath = firstNonBlank(
                System.getProperty(CREDENTIALS_PATH_PROPERTY),
                System.getenv(CREDENTIALS_PATH_ENVIRONMENT_VARIABLE)
        );
        if (configuredPath != null) {
            return normalize(configuredPath);
        }

        return Path.of(
                System.getProperty("user.home"),
                "TaurusControl",
                "config",
                "credentials.json"
        ).toAbsolutePath().normalize();
    }

    public static void validateSupportedPlatform() {
        String operatingSystem = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String architecture = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        boolean isWindows = operatingSystem.contains("win");
        boolean isX64 = architecture.equals("amd64") || architecture.equals("x86_64");

        if (!isWindows || !isX64) {
            throw new IllegalStateException(
                    "TaurusControl의 NovaStar T-SDK 연동은 Windows x64 환경에서만 실행할 수 있습니다. " +
                            "현재 환경: " + System.getProperty("os.name") + " / " + System.getProperty("os.arch")
            );
        }
    }

    public static Path requireSdkDirectory(Path sdkDirectory) {
        Path normalizedDirectory = sdkDirectory.toAbsolutePath().normalize();
        Path coreLibrary = normalizedDirectory.resolve(CORE_LIBRARY_FILE);
        if (!Files.isRegularFile(coreLibrary)) {
            throw new IllegalStateException(
                    "NovaStar T-SDK 3.6.3.0101 Windows x64 런타임을 찾을 수 없습니다: " + coreLibrary + System.lineSeparator() +
                            "SDK를 sdk/win32-x86-64에 배치하거나 -D" + SDK_PATH_PROPERTY +
                            "=<path> 또는 " + SDK_PATH_ENVIRONMENT_VARIABLE + "를 설정하세요."
            );
        }
        return normalizedDirectory;
    }

    public static JSONObject loadCredentials(Path credentialsPath) {
        Path normalizedPath = credentialsPath.toAbsolutePath().normalize();
        if (!Files.isRegularFile(normalizedPath)) {
            throw new IllegalStateException(
                    "SDK credentials 파일을 찾을 수 없습니다: " + normalizedPath + System.lineSeparator() +
                            "config/credentials.example.json을 위 경로로 복사하거나 -D" +
                            CREDENTIALS_PATH_PROPERTY + "=<path> 또는 " +
                            CREDENTIALS_PATH_ENVIRONMENT_VARIABLE + "를 설정하세요."
            );
        }

        try {
            JSONObject credentials = new JSONObject(Files.readString(normalizedPath, StandardCharsets.UTF_8));
            requireNonBlank(credentials, "company", normalizedPath);
            requireNonBlank(credentials, "phone", normalizedPath);
            requireNonBlank(credentials, "email", normalizedPath);
            return credentials;
        } catch (IOException e) {
            throw new IllegalStateException("SDK credentials 파일을 읽지 못했습니다: " + normalizedPath, e);
        } catch (JSONException e) {
            throw new IllegalStateException("SDK credentials JSON 형식이 올바르지 않습니다: " + normalizedPath, e);
        }
    }

    private static void requireNonBlank(JSONObject credentials, String key, Path credentialsPath) {
        String value = credentials.optString(key, "");
        if (value.isBlank()) {
            throw new JSONException("'" + key + "' 값이 필요합니다: " + credentialsPath);
        }
    }

    private static Path normalize(String configuredPath) {
        return Path.of(configuredPath.trim()).toAbsolutePath().normalize();
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
