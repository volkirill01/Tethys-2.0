package engine.editor.console;

import engine.stuff.customVariables.Color;

public class ConsoleMessage {

    public final LogType type;
    public final String message;
    public final Color color;

    public ConsoleMessage(String message, LogType type) {
        this.type = type;
        this.message = message;
        this.color = null;
    }

    public ConsoleMessage(String message, LogType type, Color customColor) {
        this.type = type;
        this.message = message;
        this.color = customColor;
    }

    public Color getMessageColor() {
        if (this.type == LogType.Custom)
            return this.color;
        return getMessageColor(this.type);
    }

    public static Color getMessageColor(LogType type) {
        return switch (type) {
            case Simple -> Color.WHITE;
            case Info -> new Color(75.0f, 161.0f, 234.0f);
            case Warning -> new Color(247.0f, 166.0f, 42.0f);
            case Error -> new Color(236.0f, 101.0f, 63.0f);
            case Success -> new Color(143.0f, 193.0f, 73.0f);
            case Custom -> new Color(229.0f, 67.0f, 189.0f);
            default -> throw new IllegalStateException(String.format("No color for message type - '%s'", type.name()));
        };
    }

    public String getMessageIcon() { return getMessageIcon(this.type); }

    public static String getMessageIcon(LogType type) {
        return switch (type) {
            case Simple -> "\uEED5";
            case Info -> "\uEF4E";
            case Warning -> "\uEF1B";
            case Error -> "\uEF19";
            case Success -> "\uEED7";
            case Custom -> "\uEEF3";
            default -> throw new IllegalStateException(String.format("No icon for message type - '%s'", type.name()));
        };
    }
}
