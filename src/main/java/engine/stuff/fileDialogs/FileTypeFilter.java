package engine.stuff.fileDialogs;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileTypeFilter extends FileFilter {

    private final String extension;
    private final String description;

    public static FileTypeFilter sceneFilter = new FileTypeFilter(".scene", "Tethys Scene");

    public FileTypeFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    public boolean accept(File file) {
        if (file.isDirectory())
            return true;

        return file.getName().endsWith(extension);
    }

    public String getDescription() { return description + String.format(" (*%s)", extension); }

    public String getExtension() { return extension; }
}