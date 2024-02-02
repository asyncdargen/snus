package ru.dargen.snus.attribute;

public interface DelegateAttributeHolder extends AttributeHolder {

    AttributeHolder attributeHolder();

    @Override
    default <T> boolean hasAttr(AttributeKey<T> key) {
        return attributeHolder().hasAttr(key);
    }

    @Override
    default <T> Attribute<T> removeAttr(AttributeKey<T> key) {
        return attributeHolder().removeAttr(key);
    }

    @Override
    default <T> Attribute<T> updateAttr(AttributeKey<T> key, Attribute<T> attr) {
        return attributeHolder().updateAttr(key, attr);
    }

    @Override
    default <T> Attribute<T> attr(AttributeKey<T> key) {
        return attributeHolder().attr(key);
    }

}
