package io.d2a.editor.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class Recents {

    public static final String RECENTS_FILE_NAME = ".recents.txt";

    public static final File RECENTS_FILE = new File(RECENTS_FILE_NAME);

    static {
        if (!RECENTS_FILE.exists()) {
            try {
                assert RECENTS_FILE.createNewFile();
            } catch (final IOException ex) {
                System.out.println("Oh oh! " + ex.getMessage());
            }
        }
    }

    public static List<String> getRecentFiles() {
        try {
            return Files.readAllLines(RECENTS_FILE.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static List<String> addRecentFile(final String filePath) {
        final List<String> recents = Recents.getRecentFiles();
        recents.removeIf(filePath::equals);
        while (recents.size() > 5) {
            recents.remove(0);
        }
        recents.add(filePath);
        try {
            Files.write(RECENTS_FILE.toPath(), recents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recents;
    }

}
