package com.fendrixx.aurus.util;

import org.bukkit.Location;

public class CameraBasis {
    public final double fx, fy, fz;
    public final double rx, ry, rz;
    public final double ux, uy, uz;
    private final float yaw, pitch;

    public CameraBasis(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;

        double y = Math.toRadians(yaw);
        double p = Math.toRadians(pitch);

        double sinY = Math.sin(y);
        double cosY = Math.cos(y);
        double sinP = Math.sin(p);
        double cosP = Math.cos(p);

        double fxr = -sinY * cosP;
        double fyr = -sinP;
        double fzr = cosY * cosP;
        double fLen = Math.sqrt(fxr * fxr + fyr * fyr + fzr * fzr);
        this.fx = fxr / fLen;
        this.fy = fyr / fLen;
        this.fz = fzr / fLen;

        double rxr = -this.fz;
        double rzr = this.fx;
        double rLen = Math.sqrt(rxr * rxr + rzr * rzr);
        this.rx = rxr / rLen;
        this.ry = 0;
        this.rz = rzr / rLen;

        double uxr = this.ry * this.fz - this.rz * this.fy;
        double uyr = this.rz * this.fx - this.rx * this.fz;
        double uzr = this.rx * this.fy - this.ry * this.fx;
        double uLen = Math.sqrt(uxr * uxr + uyr * uyr + uzr * uzr);
        this.ux = uxr / uLen;
        this.uy = uyr / uLen;
        this.uz = uzr / uLen;
    }

    public Location getMenuOrigin(Location eyeLoc, double distance) {
        return new Location(
                eyeLoc.getWorld(),
                eyeLoc.getX() + fx * distance,
                eyeLoc.getY() + fy * distance,
                eyeLoc.getZ() + fz * distance);
    }

    public Location calculateComponentLocation(Location origin, double x, double y, double z) {
        double dz = z - 1.0;
        return new Location(
                origin.getWorld(),
                origin.getX() + fx * dz + rx * x + ux * y,
                origin.getY() + fy * dz + ry * x + uy * y,
                origin.getZ() + fz * dz + rz * x + uz * y,
                yaw + 180f,
                -pitch);
    }

    public double[] getCursorXY(float playerYaw, float playerPitch, double distance) {
        float dYaw = MathUtil.normalizeAngle(playerYaw - yaw);
        float dPitch = MathUtil.normalizeAngle(playerPitch - pitch);
        double cx = Math.tan(Math.toRadians(dYaw)) * distance;
        double cy = -Math.tan(Math.toRadians(dPitch)) * distance;
        return new double[]{cx, cy};
    }

    public Location getCursorLocation(Location origin, float playerYaw, float playerPitch, double distance) {
        double[] local = getCursorXY(playerYaw, playerPitch, distance);
        double cx = local[0];
        double cy = local[1];

        return new Location(
                origin.getWorld(),
                origin.getX() + rx * cx + ux * cy,
                origin.getY() + ry * cx + uy * cy,
                origin.getZ() + rz * cx + uz * cy,
                yaw + 180f,
                -pitch);
    }
}
