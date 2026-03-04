package com.fendrixx.aurus.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtil {

    public static Vector[] getCameraBasis(float yaw, float pitch) {
        double y = Math.toRadians(yaw);
        double p = Math.toRadians(pitch);

        Vector forward = new Vector(
                -Math.sin(y) * Math.cos(p),
                -Math.sin(p),
                Math.cos(y) * Math.cos(p)).normalize();

        Vector worldUp = new Vector(0, 1, 0);
        Vector right = forward.clone().crossProduct(worldUp).normalize();
        Vector up = right.clone().crossProduct(forward).normalize();

        return new Vector[] { forward, right, up };
    }

    public static Location getMenuOrigin(Location eyeLoc, float yaw, float pitch, double distance) {
        Vector[] basis = getCameraBasis(yaw, pitch);
        return eyeLoc.clone().add(basis[0].clone().multiply(distance));
    }

    public static Location calculateComponentLocation(Location origin, float yaw, float pitch, double x, double y) {
        Vector[] basis = getCameraBasis(yaw, pitch);

        Location loc = origin.clone()
                .add(basis[1].clone().multiply(x))
                .add(basis[2].clone().multiply(y));

        loc.setYaw(yaw + 180f);
        loc.setPitch(-pitch);

        return loc;
    }

    public static Location getCursorLocation(Location origin, float cameraYaw, float cameraPitch,
            float playerYaw, float playerPitch, double distance) {
        Vector[] basis = getCameraBasis(cameraYaw, cameraPitch);

        float dYaw = normalizeAngle(playerYaw - cameraYaw);
        float dPitch = normalizeAngle(playerPitch - cameraPitch);

        double cx = Math.tan(Math.toRadians(dYaw)) * distance;
        double cy = -Math.tan(Math.toRadians(dPitch)) * distance;

        Location loc = origin.clone()
                .add(basis[1].clone().multiply(cx))
                .add(basis[2].clone().multiply(cy));

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