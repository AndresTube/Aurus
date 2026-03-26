package com.fendrixx.aurus.api;

public final class AurusProvider {
    private static AurusAPI instance;

    private AurusProvider() {}

    public static AurusAPI get() {
        if (instance == null) {
            throw new IllegalStateException("AurusAPI is not loaded! Is the Aurus plugin enabled?");
        }
        return instance;
    }

    public static void set(AurusAPI api) {
        instance = api;
    }

    public static void unset() {
        instance = null;
    }
}
