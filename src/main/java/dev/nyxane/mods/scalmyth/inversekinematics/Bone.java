package dev.nyxane.mods.scalmyth.inversekinematics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class Bone {
    private String name;
    private Bone parent;

    public HashMap<String, Bone> children;

    private double boneLength = 0;
    private AxisAngle boneRotation = new AxisAngle(new Vec3(0,0,0), 0);

    private Vec3 desiredPosition = new Vec3(0,0,0);
    private double desiredAngle = 0;

    public Bone(String name, double length) {
        this.name = name;
        this.children = new HashMap<>();
        this.boneLength = length;
    }

    public void resolveIK() {
        if (parent == null) return;
        Vec3 err = desiredPosition.subtract(getPosition());
        boneRotation = new AxisAngle(getVector().add(err), desiredAngle);
        parent.desiredPosition = desiredPosition
                .subtract(getVector());
        parent.resolveIK();
    }

    public void render(PoseStack poseStack, VertexConsumer buffer) {
        poseStack.pushPose();
        Vec3 p = getVector();
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
        return desiredPosition;
    }

    public void setDesiredPosition(Vec3 desiredPosition) {
        this.desiredPosition = desiredPosition;
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

    /**
     * <b>NOT O(1)</b>
     * <p>It's O(n) where n is node's distance to root</p>
     * @return Current position of a node
     */
    public Vec3 getPosition() {
        if (parent == null) return new Vec3(0,0,0);
        return getVector().add(parent.getPosition());
    }

    public Vec3 getVector() {
        return boneRotation.getVec().scale(boneLength);
    }

    public double getDesiredAngle() {
        return desiredAngle;
    }

    public void setDesiredAngle(double desiredAngle) {
        this.desiredAngle = desiredAngle;
    }
}
