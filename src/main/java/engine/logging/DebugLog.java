package engine.logging;

import engine.stuff.ColoredText;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class DebugLog {

    private static final boolean sendLogMessages = true;

    private static void logTime() {
        LocalDateTime time = LocalDateTime.now();
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        int millis = time.get(ChronoField.MILLI_OF_SECOND);

        System.out.printf("%02d:%02d:%02d.%03d: ", hour, minute, second, millis);
    }

    public static void log(Object... arguments) {
        if (!sendLogMessages)
            return;

        logTime();
        log_WithoutTime(arguments);
    }
    public static void log_WithoutTime(Object... arguments) {
        if (!sendLogMessages)
            return;

        for (Object argument : arguments)
            System.out.print(argument);
        System.out.print("\n");
    }

    public static void logInfo(Object... arguments) {
        System.out.print(ColoredText.GREEN);
        log(arguments);
        System.out.print(ColoredText.CLEAR);
    }
    public static void logInfo_WithoutTime(Object... arguments) {
        System.out.print(ColoredText.GREEN);
        log_WithoutTime(arguments);
        System.out.print(ColoredText.CLEAR);
    }

    public static void logWarning(Object... arguments) {
        System.out.print(ColoredText.YELLOW);
        log(arguments);
        System.out.print(ColoredText.CLEAR);
    }
    public static void logWarning_WithoutTime(Object... arguments) {
        System.out.print(ColoredText.YELLOW);
        log_WithoutTime(arguments);
        System.out.print(ColoredText.CLEAR);
    }

    public static void logError(Object... arguments) {
        System.out.print(ColoredText.RED);
        log(arguments);
        System.out.print(ColoredText.CLEAR);
    }

    public static void logError_WithoutTime(Object... arguments) {
        System.out.print(ColoredText.RED);
        log_WithoutTime(arguments);
        System.out.print(ColoredText.CLEAR);
    }
}
