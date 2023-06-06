package engine.stuff.utils;

import java.io.File;
import java.net.URI;

public class Paths {

    public static String getProjectDirectory() { return System.getProperty("user.dir"); }

    public static String getFileNameFromFilepath(String filepath) { return new File(filepath).getName(); }

    public static String getAbsoluteDirectory(String filepath) { return new File(filepath).getAbsolutePath(); }

    public static String getFileDirectory(String filepath) { return new File(filepath).getParent(); }

    public static String getRelativePath(String filepath) {
        // Creating a Files from directories
        File projectDirectory = new File(getProjectDirectory());
        File goToDirectory = new File(filepath);

        // Convert the absolute path to URI
        URI goToURL = goToDirectory.toURI();
        URI projectURL = projectDirectory.toURI();

        // Creating a relative path from the two paths
        URI relativePath = projectURL.relativize(goToURL);

        // Convert the URI to string and set current directory to it
        return relativePath.getPath();
    }
}
