package com.fendrixx.aurus.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.builder()
            .hexColors()
            .character('§')
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

    // idk if im gonna to use this, but its there
    public static String formatToJson(String message) {
        if (message == null || message.isEmpty()) return "{\"text\":\"\"}";
        return GsonComponentSerializer.gson().serialize(parse(message));
    }

    public static List<String> formatList(List<String> list) {
        if (list == null) return List.of();
        return list.stream().map(ColorUtils::format).collect(Collectors.toList());
    }
}