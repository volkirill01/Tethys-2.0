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

    public static void log(String message) { log(message, LogType.Simple); }

    public static void log(String message, Color customColor) {
        DebugLog.log("Console:Log: ", message, ", type: ", LogType.Custom.name());
        ConsoleMessage newMessage = new ConsoleMessage(message, LogType.Custom, customColor);
        EventSystem.notify(new Event(EventType.Console_SendMessage, newMessage));
        messages.add(newMessage);
    }

    public static void log(String message, LogType type) {
        DebugLog.log("Console:Log: ", message, ", type: ", type.name());
        ConsoleMessage newMessage = new ConsoleMessage(message, type);
        EventSystem.notify(new Event(EventType.Console_SendMessage, newMessage));
        messages.add(newMessage);
    }

    public static int getMessagesCount(LogType type) {
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
