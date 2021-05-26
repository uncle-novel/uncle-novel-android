package com.unclezs.novel.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import lombok.NoArgsConstructor;

/**
 * @author blog.unclezs.com
 * @date 2021/05/26 12:31
 */
@NoArgsConstructor
public class ObservableValue<T> implements Serializable {
    private final transient List<Consumer<T>> listeners = new ArrayList<>();
    private T value;

    public ObservableValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (Objects.equals(value, this.value)) {
            return;
        }
        this.value = value;
        listeners.forEach(listener -> listener.accept(value));
    }

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void setListener(Consumer<T> listener) {
        listeners.clear();
        listeners.add(listener);
    }

    public void clearListener() {
        listeners.clear();
    }

    public void removeListener(Consumer<T> listener) {
        listeners.remove(listener);
    }
}
