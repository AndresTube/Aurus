package com.fendrixx.aurus.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;


public class ColorUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.builder()
            .hexColors()
            .character('§')
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    @NotNull
    public static Component parse(@NotNull String message) {
        if (message.isEmpty()) return Component.empty();

        String processed = message.replace("&", "§");

        return MINI_MESSAGE.deserialize(processed)
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    @NotNull
    public static String format(String message) {
        if (message == null || message.isEmpty()) return "";

        return LEGACY_SECTION.serialize(parse(message));
    }

}