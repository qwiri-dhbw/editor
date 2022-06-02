package io.d2a.editor;

import io.d2a.editor.builder.Item;
import io.d2a.editor.builder.Menu;
import io.d2a.editor.builder.MenuBar;
import io.d2a.editor.popup.DiscardChangesDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

    private String fileName = DEFAULT_FILE_NAME;

    private String filePath = "";

    private final JTextPane textPane = new JTextPane();

    private boolean hasUnsavedChanges() {
        return !this.textPane.getText().equals(prevContent);
    }

    private void updateTitle() {
        this.setTitle(String.format("%s%s%s - Editor",
            this.fileName,
            this.hasUnsavedChanges() ? "*" : "",
            this.filePath.isBlank() ? "" : " [" + this.fileName + "]"
        ));
    }

    private JMenuItem createMenuItem(final String text, final char mnem, final Runnable onClick) {
        final JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> onClick.run());
        item.setMnemonic(mnem);
        return item;
    }

    final JMenu recentMenu;

    final JMenuBar menuBar = MenuBar.builder()
        .with(Menu.builder("File")
            .with(Item.builder("New").click(this::handleFileNew).build())
            .with(Item.builder("Open").click(this::handleFileOpen).build())
            .with(this.recentMenu = Menu.builder("Open Recent...")
                .with(Item.builder("test.txt").build())
                .with(Item.builder("hello.log").build())
                .build())
            .separator()
            .with(Item.builder("Close").click(this::handleFileClose).build())
            .separator()
            .with(Item.builder("Save").click(this::handleFileSave).build())
            .with(Item.builder("Save As").click(this::handleFileSaveAs).build())
            .build())
        .build();

    ///

    private void handleFileNew(final ActionEvent event) {
        // ask if we want to discard changes if we have unsaved changes
        if (this.hasUnsavedChanges()) {
            DiscardChangesDialog.open(this, this.fileName, result -> {
                switch (result) {
                    case SAVE -> this.handleFileSave(event);
                    case DISCARD -> this.handleActionDiscard();
                }
            });
        } else {
            this.handleActionDiscard();
        }
    }

    private void handleFileOpen(final ActionEvent event) {
        // check for unsaved changes
        if (this.hasUnsavedChanges()) {
            this.handleFileNew(event);
            return;
        }

        // let the user pick a new file to open
        final JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            this.filePath = file.getAbsolutePath();
            this.fileName = file.getName();
            try {
                this.textPane.setText(String.join("\n", Files.readAllLines(file.toPath())));
                this.prevContent = this.textPane.getText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.updateTitle();
        }
    }

    private void handleFileClose(final ActionEvent event) {
    }

    private void handleFileSave(final ActionEvent event) {

    }

    private void handleFileSaveAs(final ActionEvent event) {

    }

    private void handleActionDiscard() {
        this.textPane.setText(this.prevContent = "");
        this.setFileName(DEFAULT_FILE_NAME, "");
    }

    private void setFileName(final String fileName, final String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.updateTitle();
    }

    private void handleTextChange() {

    }

    ///

    public EditorFrame() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // create menu
        this.setJMenuBar(this.menuBar);
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
