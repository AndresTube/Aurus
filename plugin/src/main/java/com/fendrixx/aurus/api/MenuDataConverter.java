package com.fendrixx.aurus.api;

import com.fendrixx.aurus.api.action.MenuAction;
import com.fendrixx.aurus.api.component.AnimationData;
import com.fendrixx.aurus.api.component.ComponentData;
import com.fendrixx.aurus.api.component.HoverData;
import com.fendrixx.aurus.api.menu.AreaData;
import com.fendrixx.aurus.api.menu.MenuData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.List;
import java.util.Map;

public final class MenuDataConverter {

    private MenuDataConverter() {}

    public static ConfigurationSection toSection(MenuData data) {
        MemoryConfiguration root = new MemoryConfiguration();

        root.set("distance", data.getDistance());
        root.set("update-placeholders", data.shouldUpdatePlaceholders());

        if (data.getLocation() != null) {
            root.set("location", data.getLocation());
        }

        if (!data.getOnOpenActions().isEmpty()) {
            root.set("on-open", data.getOnOpenActions().stream().map(MenuAction::getAction).toList());
        }
        if (!data.getOnCloseActions().isEmpty()) {
            root.set("on-close", data.getOnCloseActions().stream().map(MenuAction::getAction).toList());
        }

        for (Map.Entry<String, AreaData> areaEntry : data.getAreas().entrySet()) {
            String areaKey = "areas." + areaEntry.getKey();
            AreaData area = areaEntry.getValue();

            root.set(areaKey + ".type", area.getType().name());
            root.set(areaKey + ".x", area.getX());
            root.set(areaKey + ".y", area.getY());
            root.set(areaKey + ".size-x", area.getSizeX());
            root.set(areaKey + ".size-y", area.getSizeY());
            root.set(areaKey + ".update-ticks", area.getUpdateTicks());
            root.set(areaKey + ".open-animation", area.getOpenAnimation().name());
            root.set(areaKey + ".close-animation", area.getCloseAnimation().name());
            root.set(areaKey + ".animation-duration", area.getAnimationDuration());

            for (Map.Entry<String, ComponentData> entry : area.getComponents().entrySet()) {
                String key = areaKey + ".components." + entry.getKey();
                ComponentData comp = entry.getValue();

                root.set(key + ".type", comp.getType().name());
                root.set(key + ".text", comp.getText());
                root.set(key + ".x", comp.getX());
                root.set(key + ".y", comp.getY());
                root.set(key + ".z", comp.getZ());
                root.set(key + ".size", comp.getSize());
                root.set(key + ".background", comp.hasBackground());
                root.set(key + ".shadow", comp.hasShadow());
                root.set(key + ".align", comp.getAlignment());
                root.set(key + ".material", comp.getMaterial());
                root.set(key + ".sound", comp.getSound());

                if (comp.getModelId() >= 0) {
                    root.set(key + ".model-id", comp.getModelId());
                }
                if (comp.getEntity() != null) {
                    root.set(key + ".entity", comp.getEntity());
                }
                if (comp.getSkin() != null) {
                    root.set(key + ".skin", comp.getSkin());
                }
                if (comp.getNametag() != null && !comp.getNametag().isEmpty()) {
                    root.set(key + ".nametag", comp.getNametag());
                }
                if (comp.getVariableName() != null) {
                    root.set(key + ".variable_name", comp.getVariableName());
                }
                if (comp.getFallbackMessage() != null) {
                    root.set(key + ".fallback-message", comp.getFallbackMessage());
                }

                if (comp.getRotationX() != 0 || comp.getRotationY() != 0 || comp.getRotationZ() != 0) {
                    root.set(key + ".rotation.x", comp.getRotationX());
                    root.set(key + ".rotation.y", comp.getRotationY());
                    root.set(key + ".rotation.z", comp.getRotationZ());
                }
                if (comp.getHeadYaw() != 0 || comp.getHeadPitch() != 0) {
                    root.set(key + ".rotation.x-head", comp.getHeadYaw());
                    root.set(key + ".rotation.y-head", comp.getHeadPitch());
                }

                if (comp.getHitboxWidth() > 0 || comp.getHitboxHeight() > 0) {
                    if (comp.getHitboxWidth() > 0) root.set(key + ".hitbox.width", comp.getHitboxWidth());
                    if (comp.getHitboxHeight() > 0) root.set(key + ".hitbox.height", comp.getHitboxHeight());
                }

                List<MenuAction> actions = comp.getActions();
                if (!actions.isEmpty()) {
                    root.set(key + ".actions", actions.stream().map(MenuAction::getAction).toList());
                }

                HoverData hover = comp.getHoverData();
                if (hover != null) {
                    String hk = key + ".hover";
                    if (hover.getType() != null) root.set(hk + ".type", hover.getType().name());
                    if (hover.getText() != null) root.set(hk + ".text", hover.getText());
                    root.set(hk + ".size", hover.getSize());
                    root.set(hk + ".background", hover.hasBackground());
                    root.set(hk + ".shadow", hover.hasShadow());
                    root.set(hk + ".align", hover.getAlignment());
                    if (hover.getMaterial() != null) root.set(hk + ".material", hover.getMaterial());
                    if (hover.getEntity() != null) root.set(hk + ".entity", hover.getEntity());
                    if (hover.getSkin() != null) root.set(hk + ".skin", hover.getSkin());
                }

                AnimationData anim = comp.getAnimationData();
                if (anim != null) {
                    String ak = key + ".animations";
                    if (anim.getScaleFormula() != null) root.set(ak + ".scale-formula", anim.getScaleFormula());
                    if (anim.getRotationFormula() != null) root.set(ak + ".rotation-formula", anim.getRotationFormula());
                    if (anim.getXFormula() != null) root.set(ak + ".x-formula", anim.getXFormula());
                    if (anim.getYFormula() != null) root.set(ak + ".y-formula", anim.getYFormula());
                    if (anim.getZFormula() != null) root.set(ak + ".z-formula", anim.getZFormula());
                }
            }
        }

        return root;
    }
}
