package tauruscontrol.util;

/**
 * UI 관련 상수 정의
 * 모든 크기, 간격, 타임아웃 값을 중앙에서 관리
 */
public class UIConstants {

    // ===== Panel Dimensions =====
    public static final int PANEL_WIDTH = 500;
    public static final int PANEL_GAP = 30;
    public static final int PANEL_PADDING_SMALL = 20;
    public static final int PANEL_PADDING_LARGE = 40;

    // ===== Row Dimensions =====
    public static final int ROW_HEIGHT = 40;
    public static final int TITLE_HEIGHT = 35;
    public static final int CONTAINER_HEIGHT = 402;
    public static final int SCHEDULE_CONTAINER_HEIGHT = 242;

    // ===== Label Widths =====
    public static final int NAME_LABEL_WIDTH = 200;
    public static final int RESOLUTION_LABEL_WIDTH = 150;
    public static final int STATUS_WIDTH = 60;
    public static final int ACTION_WIDTH = 100;
    public static final int FILE_LABEL_WIDTH = 300;
    public static final int SCHEDULE_LABEL_WIDTH = 300;

    // ===== Button Sizes =====
    public static final int BUTTON_SMALL = 80;
    public static final int BUTTON_MEDIUM = 100;
    public static final int BUTTON_LARGE = 130;
    public static final int ICON_BUTTON_SIZE = 35;
    public static final int EDIT_BUTTON_WIDTH = 60;

    // ===== Spacing & Padding =====
    public static final int SPACING_SMALL = 5;
    public static final int SPACING_MEDIUM = 10;
    public static final int SPACING_LARGE = 20;
    public static final int SPACING_XLARGE = 30;

    // ===== Border & Radius =====
    public static final int BORDER_WIDTH = 1;
    public static final int BORDER_RADIUS_SMALL = 5;
    public static final int BORDER_RADIUS_MEDIUM = 10;
    public static final int BORDER_RADIUS_CIRCLE = 50;

    // ===== Timeouts (milliseconds) =====
    public static final int DIALOG_TIMEOUT_SHORT = 2000;
    public static final int DIALOG_TIMEOUT_LONG = 3000;

    // ===== Colors (for programmatic use only - prefer CSS) =====
    public static final String COLOR_PRIMARY = "#1E88E5";
    public static final String COLOR_PRIMARY_HOVER = "#1976D2";
    public static final String COLOR_BACKGROUND_DARK = "#323232";
    public static final String COLOR_BACKGROUND_ALT = "#3a3a3a";
    public static final String COLOR_BACKGROUND_CONTAINER = "#3e3e3e";
    public static final String COLOR_BORDER = "#6a6a6a";
    public static final String COLOR_BORDER_LIGHT = "#999999";
    public static final String COLOR_BUTTON_SECONDARY = "#5a5a5a";
    public static final String COLOR_BUTTON_SECONDARY_HOVER = "#4a4a4a";
    public static final String COLOR_TEXT_PRIMARY = "white";
    public static final String COLOR_TEXT_SECONDARY = "#cccccc";
    public static final String COLOR_TEXT_DISABLED = "#888888";
    public static final String COLOR_ERROR = "#ff6b6b";
    public static final String COLOR_SUCCESS = "limegreen";
    public static final String COLOR_WARNING = "yellow";

    // ===== Dialog Sizes =====
    public static final int DIALOG_SMALL_WIDTH = 180;
    public static final int DIALOG_SMALL_HEIGHT = 120;
    public static final int DIALOG_MEDIUM_WIDTH = 250;
    public static final int DIALOG_MEDIUM_HEIGHT = 150;
    public static final int DIALOG_LARGE_WIDTH = 350;
    public static final int DIALOG_LARGE_HEIGHT = 200;

    // ===== Icon Sizes =====
    public static final int ICON_SIZE_SMALL = 80;
    public static final int ICON_SIZE_MEDIUM = 100;
    public static final int ICON_SIZE_LARGE = 120;

    private UIConstants() {
        // Utility class - prevent instantiation
    }
}
