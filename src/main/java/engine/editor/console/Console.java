package engine.editor.console;

import engine.logging.DebugLog;
import engine.observers.EventSystem;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.stuff.customVariables.Color;

import java.util.ArrayList;
import java.util.List;

public class Console {

    private static final List<ConsoleMessage> messages = new ArrayList<>();
    private static boolean stopOnError = true;

    private static void sendMessage(ConsoleMessage.LogType type, String message) {
        DebugLog.log("Console:Log: ", message, ", type: ", type.name());
        ConsoleMessage newMessage = new ConsoleMessage(type, message);
        EventSystem.notify(new Event(EventType.Console_SendMessage, newMessage));
        messages.add(newMessage);
    }

    private static void sendMessage(Color customColor, String message) {
        DebugLog.log("Console:Log: ", message, ", type: ", ConsoleMessage.LogType.Custom.name());
        ConsoleMessage newMessage = new ConsoleMessage(customColor, message);
        EventSystem.notify(new Event(EventType.Console_SendMessage, newMessage));
        messages.add(newMessage);
    }

    public static void log(String message) { sendMessage(ConsoleMessage.LogType.Simple, message); }

    public static void logInfo(String message) { sendMessage(ConsoleMessage.LogType.Info, message); }

    public static void logWarning(String message) { sendMessage(ConsoleMessage.LogType.Warning, message); }

    public static void logError(String message) { sendMessage(ConsoleMessage.LogType.Error, message); }

    public static void logSuccess(String message) { sendMessage(ConsoleMessage.LogType.Success, message); }

    public static void logCustom(String message, Color customColor) { sendMessage(customColor, message); }

    public static int getMessagesCount(ConsoleMessage.LogType type) {
        int count = 0;
        for (ConsoleMessage message: messages)
            if (message.type == type)
                count++;

        return count;
    }

    public static void clear() {
        DebugLog.log("Console:Clear");
        messages.clear();
    }

    public static void removeMessage(int index) {
        DebugLog.log("Console:RemoveMessage: ", index);
        messages.remove(index);
    }

    public static List<ConsoleMessage> getMessages() { return messages; }

    public static boolean isStopOnError() { return stopOnError; }

    public static void setStopOnError(boolean stopOnError) { Console.stopOnError = stopOnError; }
}
