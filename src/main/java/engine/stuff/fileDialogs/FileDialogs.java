package engine.stuff.fileDialogs;

import engine.logging.DebugLog;

import javax.swing.*;
import java.io.File;

public class FileDialogs {

    private static String targetPath = "";
    private static JFileChooser chooser;

    public static String openFile(FileTypeFilter filter, String currentDirectory) { // TODO FIX BUG, JFileChooser WINDOWS NOT APPEAR ON FRONT OF MAIN WINDOW(BECAUSE ITS PARENT IS NULL)
        DebugLog.log("FileDialogs:OpenFile: filter: ", filter, ", current directory: ", currentDirectory);

        chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Specify a file to open");

        chooser.setFileFilter(filter);
        chooser.addChoosableFileFilter(filter);
        chooser.setCurrentDirectory(new File(currentDirectory));

        if (!targetPath.equals(""))
            chooser.setSelectedFile(new File(targetPath));

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            targetPath = chooser.getSelectedFile().getAbsolutePath();
            return chooser.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

    public static String saveFile(File file, FileTypeFilter filter, String currentDirectory) {
        DebugLog.log("FileDialogs:SaveFile: file: ", file.getAbsoluteFile(), ", filter: ", filter, ", current directory: ", currentDirectory);

        chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Specify a file to save");

        chooser.setFileFilter(filter);
        chooser.addChoosableFileFilter(filter);

        chooser.setSelectedFile(file);
        chooser.setCurrentDirectory(new File(currentDirectory));

        int userSelection = chooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();

            if (!filter.accept(fileToSave))
                fileToSave = new File(fileToSave.getAbsolutePath() + filter.getExtension());

            return fileToSave.getAbsolutePath();
        }

        return null;
    }
}
