package dev.nyxane.mods.scalmyth.inversekinematics;

import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4d;

public class AxisAngle {
    private Vec3 vec;
    private double angle;

    AxisAngle(Vec3 vec, double angle) {
        setVec(vec);
        this.angle = angle;
    }

    public Vec3 getVec() {
        return vec;
    }

    public void setVec(Vec3 vec) {
        this.vec = vec.normalize();
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
