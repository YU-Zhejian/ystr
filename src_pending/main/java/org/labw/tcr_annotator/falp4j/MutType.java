package org.labw.tcr_annotator.falp4j;

public enum MutType {
    MISSENSE("MISSENSE"),
    NONESENSE("NONSENSE"),
    FRAME_SHIFT("FRAME_SHIFT"),
    INS("INS"),
    NULL("NULL"),
    DEL("DEL");

    private final String name;

    MutType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
