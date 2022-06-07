package io.d2a.editor.builder;

import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Menu implements Builder<Menu, JMenu> {

    private final JMenu menu;

    private Menu(final String text) {
        this.menu = new JMenu(text);
    }

    @Override
    public Menu mnemonic(final int mn) {
        this.menu.setMnemonic(mn);
        return this;
    }

    @Override
    public Menu mnemonic(final char mn) {
        this.menu.setMnemonic(mn);
        return this;
    }

    public Menu selected(final boolean selected) {
        this.menu.setSelected(selected);
        return this;
    }

    public Menu icon(final Icon icon) {
        this.menu.setIcon(icon);
        return this;
    }

    public Menu with(final JMenuItem item) {
        this.menu.add(item);
        return this;
    }

    public Menu with(final Item builder) {
        return this.with(builder.build());
    }

    public Menu with(final JMenu menu) {
        this.menu.add(menu);
        return this;
    }

    public Menu separator() {
        this.menu.addSeparator();
        return this;
    }

    @Override
    public Menu click(final ActionListener listener) {
        this.menu.addActionListener(listener);
        return this;
    }

    @Override
    public Menu enabled(final boolean enabled) {
        this.menu.setEnabled(enabled);
        return this;
    }

    @Override
    public JMenu build() {
        return this.menu;
    }

    public static Menu builder(final String text) {
        return new Menu(text);
    }

}
