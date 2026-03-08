package com.fendrixx.aurus.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SkinFetcher {

    public record SkinData(String textureValue, String textureSignature) {}

    private static final ConcurrentHashMap<String, SkinData> CACHE = new ConcurrentHashMap<>();

    public static CompletableFuture<SkinData> fetchAsync(String username) {
        SkinData cached = CACHE.get(username.toLowerCase());
        if (cached != null) return CompletableFuture.completedFuture(cached);
        return CompletableFuture.supplyAsync(() -> fetchBlocking(username));
    }

    public static SkinData fetchBlocking(String username) {
        SkinData cached = CACHE.get(username.toLowerCase());
        if (cached != null) return cached;

        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(
                    "https://api.mojang.com/users/profiles/minecraft/" + username).toURL().openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() != 200) return null;

            JsonObject profile;
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                profile = JsonParser.parseReader(reader).getAsJsonObject();
            }
            String uuid = profile.get("id").getAsString();

            HttpURLConnection conn2 = (HttpURLConnection) URI.create(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").toURL().openConnection();
            conn2.setConnectTimeout(5000);
            conn2.setReadTimeout(5000);
            if (conn2.getResponseCode() != 200) return null;

            JsonObject session;
            try (InputStreamReader reader = new InputStreamReader(conn2.getInputStream())) {
                session = JsonParser.parseReader(reader).getAsJsonObject();
            }

            JsonArray properties = session.getAsJsonArray("properties");
            for (int i = 0; i < properties.size(); i++) {
                JsonObject prop = properties.get(i).getAsJsonObject();
                if ("textures".equals(prop.get("name").getAsString())) {
                    SkinData data = new SkinData(
                            prop.get("value").getAsString(),
                            prop.get("signature").getAsString());
                    CACHE.put(username.toLowerCase(), data);
                    return data;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
