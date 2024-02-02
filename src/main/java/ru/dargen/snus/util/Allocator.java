package ru.dargen.snus.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import sun.misc.Unsafe;

import java.util.function.Supplier;

@UtilityClass
@SuppressWarnings("unchecked")
public class Allocator {

    public <T> T allocateInstance(Class<T> declaredClass) {
        return isSupportedUnsafe() ? allocateUnsafe(declaredClass) : allocateReflect(declaredClass);
    }

    public <T> Supplier<T> allocator(Class<T> declaredClass) {
        return isSupportedUnsafe() ? allocatorUnsafe(declaredClass) : allocatorReflect(declaredClass);
    }

    @SneakyThrows
    private <T> Supplier<T> allocatorReflect(Class<T> declaredClass) {
        var constructor = declaredClass.getDeclaredConstructor();
        constructor.trySetAccessible();

        return () -> {
            try {
                return constructor.newInstance();
            } catch (Throwable e) {
                return null;
            }
        };
    }

    @SneakyThrows
    private <T> Supplier<T> allocatorUnsafe(Class<T> declaredClass) {
        return () -> {
            try {
                return allocateInstance(declaredClass);
            } catch (Throwable e) {
                return null;
            }
        };
    }

    @SneakyThrows
    private <T> T allocateReflect(Class<T> declaredClass) {
        var constructor = declaredClass.getDeclaredConstructor();
        constructor.trySetAccessible();
        return constructor.newInstance();
    }

    @SneakyThrows
    private <T> T allocateUnsafe(Class<T> declaredClass) {
        return (T) UNSAFE.allocateInstance(declaredClass);
    }

    public final Unsafe UNSAFE = findUnsafe();

    public boolean isSupportedUnsafe() {
        return UNSAFE != null;
    }

    @SneakyThrows
    private Unsafe findUnsafe() {
        try {
            val unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);
        } catch (Throwable throwable) {
            System.err.println("Unsafe not supported");
            return null;
        }
    }

}
