package engine.stuff.utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Paths {

    public static String getProjectDirectory() { return System.getProperty("user.dir"); }

    public static String getFileNameFromFilepath(String filepath) { return new File(filepath).getName(); }

    public static String getFileExtensionFromFilepath(String filepath) {
        File tmp = new File(filepath);
        return tmp.getName().split("\\.")[tmp.getName().split("\\.").length - 1];
    }

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

    public static void sortFiles(File[] filesList) {
        List<File> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();
        for (File file : filesList) {
            if (file.isDirectory())
                directories.add(file);
            else
                files.add(file);
        }

        for (int i = 0; i < directories.size(); i++)
            filesList[i] = directories.get(i);
        for (int i = 0; i < files.size(); i++)
            filesList[i + directories.size()] = files.get(i);
    }
}
