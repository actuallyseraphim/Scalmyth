package dev.nyxane.mods.scalmyth.inversekinematics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;

public class Bone {
    private String name;
    private Bone parent;

    public HashMap<String, Bone> children;

    private float boneLength;
    private Quaternionf quaternion = new Quaternionf();

    private Vector3f desiredPosition = new Vector3f(0,0,0);
    private float desiredAngle = 0;

    // double to float cast for not writing f
    public Bone(String name, double length) {
        this.name = name;
        this.boneLength = (float) length;
        this.children = new HashMap<>();
    }

    public void resolveIK() {
        if (parent == null) return;
        Vector3f err = desiredPosition.sub(getPosition(), new Vector3f());
        Quaternionf quaternion_err = new Quaternionf().rotateTo(getVector(), getVector().add(err)).normalize();
        quaternion = quaternion_err.mul(quaternion, new Quaternionf());
        parent.desiredPosition = desiredPosition
                .sub(getVector(), new Vector3f());
        parent.resolveIK();
    }

    public void render(PoseStack poseStack, VertexConsumer buffer) {
        poseStack.pushPose();
        Vec3 p = new Vec3(getVector());
        poseStack.translate(p.x, p.y, p.z);
        buffer.addVertex(poseStack.last(),0f,0f,0f).setColor(1f,0f,0f,1f);

        for (HashMap.Entry<String, Bone> entry: children.entrySet()) {
            entry.getValue().render(poseStack, buffer);
        }
        poseStack.popPose();
    }

    public Bone getChild(String string) {
        return children.get(string);
    }

    public void addChild(Bone bone) {
        bone.parent = this;
        children.put(bone.name, bone);
    }

    public Vec3 getDesiredPosition() {
        return new Vec3(desiredPosition);
    }

    public void setDesiredPosition(Vec3 desiredPosition) {
        this.desiredPosition = desiredPosition.toVector3f();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bone getParent() {
        return parent;
    }

    private void setParent(Bone parent) {
        this.parent = parent;
    }

    private Vector3f getPosition() {
        if (parent == null) return new Vector3f(0,0,0);
        return parent.getPosition().add(getVector(), new Vector3f());
    }

    private Vector3f getVector() {
        return getCumulativeQuaternion().transform(new Vector3f(0,boneLength,0), new Vector3f());
    }

    public Quaternionf getCumulativeQuaternion() {
        if (parent == null) return quaternion;
        return quaternion.mul(parent.getCumulativeQuaternion(), new Quaternionf());
    }

    public float getDesiredAngle() {
        return desiredAngle;
    }

    public void setDesiredAngle(float desiredAngle) {
        this.desiredAngle = desiredAngle;
    }
}
