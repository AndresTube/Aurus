package com.fendrixx.aurus.api.cursor;

public class CursorConfig {
    private final CursorType type;
    private final String value;
    private final float size;

    private CursorConfig(CursorType type, String value, float size) {
        this.type = type;
        this.value = value;
        this.size = size;
    }

    public CursorType getType() { return type; }
    public String getValue() { return value; }
    public float getSize() { return size; }

    public static CursorConfig text(String symbol, float size) {
        return new CursorConfig(CursorType.TEXT, symbol, size);
    }

    public static CursorConfig item(String material, float size) {
        return new CursorConfig(CursorType.ITEM, material, size);
    }

    public static CursorConfig block(String material, float size) {
        return new CursorConfig(CursorType.BLOCK, material, size);
    }

    public static CursorConfig text(String symbol) {
        return text(symbol, 1.0f);
    }

    public static CursorConfig item(String material) {
        return item(material, 1.0f);
    }

    public static CursorConfig block(String material) {
        return block(material, 1.0f);
    }
}
