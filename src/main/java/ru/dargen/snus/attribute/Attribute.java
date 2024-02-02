package ru.dargen.snus.attribute;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
@Accessors(fluent = true, chain = true)
public class Attribute<T> {

    @Setter(AccessLevel.PACKAGE)
    private AttributeHolder holder;
    @Setter(AccessLevel.PACKAGE)
    private AttributeKey<T> key;

    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        if (!holder.hasAttr(key)) {
            holder.updateAttr(key, this);
        }
    }

    public Optional<T> optional() {
        return Optional.ofNullable(value);
    }

    public void isPresent(Consumer<T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
    }

    public boolean isPresent() {
        return value != null;
    }

    public T clear() {
        holder.removeAttr(key);
        return value;
    }

}
