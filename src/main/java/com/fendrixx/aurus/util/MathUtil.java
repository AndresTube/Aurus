package com.fendrixx.aurus.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class MathUtil {

    public static Location getCursorLocation(Location cameraLoc, float cameraYaw, float playerYaw, float playerPitch,
            double distance) {
        double yawRad = Math.toRadians(cameraYaw);

        Vector forward = new Vector(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        Vector right = new Vector(forward.getZ(), 0, -forward.getX()).normalize();
        Vector up = new Vector(0, 1, 0);

        float deltaYaw = normalizeAngle(playerYaw - cameraYaw);
        float limitedPitch = Math.max(-80, Math.min(80, playerPitch));

        double x = -Math.tan(Math.toRadians(deltaYaw)) * distance;
        double y = Math.tan(Math.toRadians(-limitedPitch)) * distance;

        Location loc = cameraLoc.clone()
                .add(forward.multiply(distance))
                .add(right.multiply(x))
                .add(up.multiply(y));

        loc.setYaw(playerYaw + 180f);
        loc.setPitch(-playerPitch);

        return loc;
    }

    public static float normalizeAngle(float angle) {
        while (angle <= -180)
            angle += 360;
        while (angle > 180)
            angle -= 360;
        return angle;
    }

    public static double evaluate(String formula, double t) {
        try {
            return new net.objecthunter.exp4j.ExpressionBuilder(formula)
                    .variable("t")
                    .build()
                    .setVariable("t", t)
                    .evaluate();
        } catch (Exception e) {
            return 0;
        }
    }
}