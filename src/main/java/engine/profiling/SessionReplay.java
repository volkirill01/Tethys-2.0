package engine.profiling;

import engine.logging.DebugLog;

import java.io.*;

public class SessionReplay {

    private static String currentSession = "";
    private static int profileCount;

    private static FileOutputStream outputStream;

    public static void beginSession(String name) { beginSession(name, "profileResults.json"); }
    public static void beginSession(String name, String filepath) {
        DebugLog.log("SessionReplay:BeginSession: ", name, ", path: ", filepath);

        currentSession = name; // TODO GET TIME FROM SYSTEM CLOCK

        if (outputStream != null)
            throw new IllegalStateException(String.format("New session(%s, %s) not begin. Session already begin.", name, filepath));

        try {
            outputStream = new FileOutputStream(filepath); // TODO ADD TIME TO FILEPATH
//            outputStream = new FileOutputStream(currentSession + "_" + filepath);
            writeHeader();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void endSession() {
        DebugLog.log("SessionReplay:EndSession: ", currentSession);
        try {
            writeFooter();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        currentSession = null;
        profileCount = 0;
        outputStream = null;
    }

    public static void writeProfile(Timer profileResult) {
        try {
            if (profileCount++ > 0)
                outputStream.write(",".getBytes());

            outputStream.write("{".getBytes());
            outputStream.write("\"cat\":\"function\",".getBytes());
            outputStream.write(("\"dur\":" + (profileResult.getEndPoint() - profileResult.getStartPoint()) + ",").getBytes());
            outputStream.write(("\"name\":\"" + profileResult.getName() + "\",").getBytes());
            outputStream.write(("\"ph\":\"X\",").getBytes());
            outputStream.write(("\"pid\":0,").getBytes());
            outputStream.write(("\"tid\":0,").getBytes());
            outputStream.write(("\"ts\":" + profileResult.getStartPoint()).getBytes());
            outputStream.write("}".getBytes());

            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeHeader() {
        try {
            outputStream.write("{\"otherData\": {},\"traceEvents\":[".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFooter() {
        try {
            outputStream.write("]}".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
