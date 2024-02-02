package ru.dargen.snus.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrafficUnit {

    BYTES("b"),
    KILOBYTES("kb"),
    MEGABYTES("mb"),
    GIGABYTES("gb"),
    TERABYTES("tb");

    private final String name;
    private final long size = (long) Math.pow(1024, ordinal());

}