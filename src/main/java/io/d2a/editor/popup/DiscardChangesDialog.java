package io.d2a.editor.popup;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DiscardChangesDialog extends JDialog {

   public enum Result {
        SAVE,
        DISCARD,
        CANCEL;
    }

    private final Consumer<Result> consumer;

    public static void open(
        final JFrame owner,
        final String fileName,
        final Consumer<Result> resultConsumer
    ) {
        final DiscardChangesDialog frame = new DiscardChangesDialog(owner, fileName, resultConsumer);
        frame.setVisible(true);
    }

    private DiscardChangesDialog(
        final JFrame owner,
        final String fileName,
        final Consumer<Result> resultConsumer
    ) {
        super(owner, true);
        this.consumer = resultConsumer;

        final JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        final JPanel headerPanel = new JPanel();
        final JLabel label = new JLabel(String.format(
            "Do you want to save changes to %s?",
            fileName
        ));
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        headerPanel.add(label);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        final JButton saveButton = new JButton("Save");
        saveButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
        saveButton.addActionListener(this::handleSave);
        buttonPanel.add(saveButton);

        final JButton discardButton = new JButton("Don't Save");
        discardButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
        discardButton.addActionListener(this::handleDiscord);
        buttonPanel.add(discardButton);

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
        cancelButton.addActionListener(this::handleCancel);
        buttonPanel.add(cancelButton);

        main.add(headerPanel);
        main.add(buttonPanel);

        this.add(main);
        this.pack();
    }

    private void handleSave(final ActionEvent event) {
        this.dispose();
        this.consumer.accept(Result.SAVE);
    }

    private void handleDiscord(final ActionEvent event) {
        this.dispose();
        this.consumer.accept(Result.DISCARD);
    }

    private void handleCancel(final ActionEvent event) {
        this.dispose();
        this.consumer.accept(Result.CANCEL);
    }

}
