package io.d2a.editor.helper;

import java.util.function.Consumer;

public class IDGAF {

    public static void ifYouFail(final ThrowableRunnable runnable, final Consumer<Exception> onError) {
        try {
            runnable.run();
        } catch (final Exception exception) {
            onError.accept(exception);
        }
    }

    public static <T> T ifYouFail(
        final ThrowableConsumer<T> runnable,
        final Consumer<Exception> onError,
        final T def
    ) {
        try {
            return runnable.apply();
        } catch (final Exception exception) {
            onError.accept(exception);
            return def;
        }
    }


}
