package io.d2a.editor.builder;

import java.awt.event.ActionListener;

public interface Builder<T, R> {

    T enabled(final boolean enabled);

    T mnemonic(final int mn);

    T mnemonic(final char mn);

    T click(final ActionListener listener);

    R build();

}
