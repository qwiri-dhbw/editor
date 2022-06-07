package io.d2a.editor.helper;

public interface ThrowableConsumer<T> {

    T apply() throws Exception;

}
