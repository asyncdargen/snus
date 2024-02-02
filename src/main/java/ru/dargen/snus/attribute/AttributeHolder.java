package ru.dargen.snus.attribute;

public interface AttributeHolder {

    <T> boolean hasAttr(AttributeKey<T> key);

    <T> Attribute<T> removeAttr(AttributeKey<T> key);

    <T> Attribute<T> attr(AttributeKey<T> key);

    <T> Attribute<T> updateAttr(AttributeKey<T> key, Attribute<T> attr);

}
