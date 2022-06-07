package io.d2a.editor;

import io.d2a.editor.builder.Item;
import io.d2a.editor.builder.Menu;
import io.d2a.editor.builder.MenuBar;
import io.d2a.editor.helper.IDGAF;
import io.d2a.editor.helper.Recents;
import io.d2a.editor.popup.DiscardChangesDialog;
import io.d2a.editor.popup.DiscardChangesDialog.Result;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTextPane;

public class EditorFrame extends JFrame {

    public static void main(String[] args) {
        new EditorFrame();
    }

    public static final String DEFAULT_FILE_NAME = "Untitled";

    /**
     * Used to indicate whether we have unsaved changes
     */
    private String prevContent = "";

    /**
     * contains only the file name (displayed in window title)
     */
    private String fileName = DEFAULT_FILE_NAME;

    /**
     * contains the file path (for saving)
     */
    private String filePath = "";

    // Controls

    /**
     * Contains the text from the opened file
     */
    private final JTextPane textPane = new JTextPane();

    /**
     * Contains recently opened files
     */
    private final JMenu recentMenu;

    ///

    /**
     * checks if there are unsaved changes
     *
     * @return true if there are unsaved changes, otherwise false
     */
    private boolean hasUnsavedChanges() {
        return !this.textPane.getText().equals(prevContent);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * updates the 'Open Recent >' menu with the latest files
     */
    private void updateRecentMenu() {
        // remove old recents
        this.recentMenu.removeAll();
        // insert recents
        for (final String recentFile : Recents.getRecentFiles()) {
            final File file = new File(recentFile);
            this.recentMenu.add(Item.builder(recentFile)
                .click(() -> this.openFile(file))
                .build());
        }
    }

    /**
     * updates the window's title to display the current opened file and to display the save state /
     * file name / file path
     */
    private void updateTitle() {
        this.setTitle(String.format("%s%s%s - Editor",
            this.fileName,
            this.hasUnsavedChanges() ? "*" : "",
            this.filePath.isBlank() ? "" : " [" + this.fileName + "]"
        ));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    private void openFile(final File file) {
        // check for opened file
        if (!this.checkRelease()) {
            return;
        }

        this.filePath = file.getAbsolutePath();
        this.fileName = file.getName();
        try {
            this.textPane.setText(String.join("\n", Files.readAllLines(file.toPath())));
            this.prevContent = this.textPane.getText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.updateTitle();
        Recents.addRecentFile(this.filePath);
        this.updateRecentMenu();
    }

    private boolean saveFile(final boolean forceNewFile) {
        final File file;

        // save new file
        if (forceNewFile || this.filePath.isBlank()) {
            // ask where to save file
            final JFileChooser saveDialog = new JFileChooser();
            saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
            if (saveDialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            file = saveDialog.getSelectedFile();
        } else {
            file = new File(this.filePath);
        }

        // create file if not exists
        if (!file.exists()) {
            // noinspection ResultOfMethodCallIgnored
            IDGAF.ifYouFail(file::createNewFile, Exception::printStackTrace);
        }

        // save previous opened file
        IDGAF.ifYouFail(() -> Files.writeString(
            file.toPath(),
            EditorFrame.this.textPane.getText(),
            StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
        ), Exception::printStackTrace);

        // update save indicator
        this.prevContent = this.textPane.getText();
        this.setFileName(file.getName(), file.getAbsolutePath());
        this.updateTitle();

        // add to recent files
        Recents.addRecentFile(this.filePath);
        return true;
    }

    /**
     * closes the current file without saving
     */
    private void discardCurrent() {
        this.textPane.setText(this.prevContent = "");
        this.setFileName(DEFAULT_FILE_NAME, "");
    }

    private void setFileName(final String fileName, final String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.updateTitle();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * File > New - Close current file
     *
     * @param event Action Event
     */
    private void handleFileNew(final ActionEvent event) {
        // ask if we want to discard changes if we have unsaved changes
        if (this.checkRelease()) {
            this.discardCurrent();
        }
    }

    private boolean checkRelease() {
        // ask if we want to discard changes if we have unsaved changes
        if (!this.hasUnsavedChanges()) {
            return true;
        }
        while (true) {
            final Result result = DiscardChangesDialog.open(this, this.fileName);
            switch (result) {
                case CANCEL:
                    return false;
                case SAVE:
                    if (!this.saveFile(false)) {
                        continue;
                    }
                    break;
                case DISCARD:
                    this.discardCurrent();
                    break;
            }
            break;
        }
        return true;
    }

    /**
     * File > Open - Open another file
     *
     * @param event Action Event
     */
    private void handleFileOpen(final ActionEvent event) {
        if (!this.checkRelease()) {
            return;
        }
        // let the user pick a new file to open
        final JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            this.openFile(file);
        }
    }

    /**
     * File > Close - Close Notepad
     *
     * @param event Action Event
     */
    private void handleFileClose(final ActionEvent event) {
        System.exit(0);
    }

    /**
     * File > Save - Save current file
     *
     * @param event Action Event
     */
    private void handleFileSave(final ActionEvent event) {
        this.saveFile(false);
    }

    /**
     * File > Save As - Save file as new file
     *
     * @param event ActionEvent
     */
    private void handleFileSaveAs(final ActionEvent event) {
        this.saveFile(true);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    public EditorFrame() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // create menu
        final JMenuBar menuBar = MenuBar.builder()
            .with(Menu.builder("File")
                // Create new file
                .with(Item.builder("New")
                    .click(this::handleFileNew)
                    .mnemonic('n')
                    .build())
                // Open file
                .with(Item.builder("Open")
                    .click(this::handleFileOpen)
                    .mnemonic('o')
                    .build())
                // Open recent file
                .with(this.recentMenu = Menu.builder("Open Recent...").build())
                .separator()
                // Close editor
                .with(Item.builder("Close")
                    .click(this::handleFileClose)
                    .mnemonic('q')
                    .build())
                .separator()
                // Save current file
                .with(Item.builder("Save")
                    .click(this::handleFileSave)
                    .mnemonic('s')
                    .build())
                // Save current file as new file
                .with(Item.builder("Save As").click(this::handleFileSaveAs).build())
                .build())
            .build();

        // update recent file menu
        this.updateRecentMenu();
        this.setJMenuBar(menuBar);
        this.add(this.textPane);

        // text pane
        this.textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                EditorFrame.this.updateTitle();
            }
        });

        this.updateTitle();

        this.setSize(1280, 720);
        this.setVisible(true);
    }

}
