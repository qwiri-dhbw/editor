package io.d2a.editor.builder;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

public class Item implements Builder<Item, JMenuItem> {

    private final JMenuItem item;

    private Item(final String text) {
        this.item = new JMenuItem(text);
    }

    public static Item builder(final String text) {
        return new Item(text);
    }

    @Override
    public Item enabled(final boolean enabled) {
        this.item.setEnabled(enabled);
        return this;
    }

    @Override
    public Item mnemonic(final int mn) {
        this.item.setMnemonic(mn);
        return this;
    }

    @Override
    public Item mnemonic(final char mn) {
        this.item.setMnemonic(mn);
        return this;
    }

    @Override
    public Item click(final ActionListener listener) {
        this.item.addActionListener(listener);
        return this;
    }

    public Item click(final Runnable runnable) {
        return this.click(event -> runnable.run());
    }

    @Override
    public JMenuItem build() {
        return this.item;
    }

}
