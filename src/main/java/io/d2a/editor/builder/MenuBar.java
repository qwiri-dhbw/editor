package io.d2a.editor.builder;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar {

    private final JMenuBar bar;

    private MenuBar() {
        this.bar = new JMenuBar();
    }

    public static MenuBar builder() {
        return new MenuBar();
    }

    public MenuBar enable(final boolean enabled) {
        this.bar.setEnabled(enabled);
        return this;
    }

    public MenuBar with(final JMenu menu) {
        this.bar.add(menu);
        return this;
    }

    public MenuBar with(final Menu builder) {
        return this.with(builder.build());
    }

    public MenuBar with(final JMenuItem item) {
        this.bar.add(item);
        return this;
    }

    public JMenuBar build() {
        return this.bar;
    }

}
