package dev.nyxane.mods.scalmyth.inversekinematics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class Bone {
    private String name;
    public Vec3 defaultVector;
    public double defaultDistance;

    public Vec3 position;
    public Vec3 desiredPosition;
    public Bone parent;
    private final Map<String, Bone> children;

    public Vec3 resolvePosition() {
        Vec3 pos = desiredPosition;
        Vec3 vector = pos.subtract(parent.position);
        vector = vector.normalize().scale(defaultDistance);
        double dot = vector.normalize().dot(defaultVector.normalize());
        double d = 0.1;
        Vec3 dir = vector.normalize();
        if (dot > d) {
            double a = 90;
            double cosa = Math.cos(a/180*Math.PI);
            dir = (dir.subtract(defaultVector.scale(dir.dot(defaultVector)))).normalize().scale(Math.sqrt(1 - cosa * cosa)).add(dir.scale(cosa));
        }
        vector = dir.scale(vector.length());
        return vector.add(parent.position);
    }

    public void resolveIK() {
        if (parent == null) return;
        position = resolvePosition();
        parent.desiredPosition = desiredPosition.subtract(position).add(parent.position);
        parent.resolveIK();
    }

    public void render(PoseStack poseStack, VertexConsumer buffer) {
        poseStack.pushPose();
        poseStack.translate(position.x, position.y, position.z);
        buffer.addVertex(poseStack.last(),0f,0f,0f).setColor(1f,0f,0f,1f);
        poseStack.popPose();

        for (Map.Entry<String, Bone> entry: children.entrySet()) {
            entry.getValue().render(poseStack, buffer);
        }
    }

    Bone(Builder builder) {
        this.children = builder.endConnections;
        this.position = builder.position;
        this.desiredPosition = builder.position;
        this.name = builder.name;
        this.defaultVector = builder.vector;
        this.defaultDistance = this.defaultVector.length();
    }

    public static Builder builder(String name) {
        return new Builder(null, name);
    }

    public Bone getChild(String string) {
        return children.get(string);
    }

    public static class Builder {
        private final HashMap<String, Bone> endConnections = new HashMap<>();
        private Vec3 vector = new Vec3(0,0,0);

        private final Builder parentBuilder;
        private final String name;
        private Vec3 position = new Vec3(0,0,0);

        public Builder(Builder parent, String name) {
            this.parentBuilder = parent;
            this.name = name;
        }

        public Builder addChild(Bone child) {
            endConnections.put(name, child);
            return this;
        }

        public Builder startChild(String name) {
            return new Builder(this, name);
        }

        public Builder endChild() {
            parentBuilder.endConnections.put(name, this.build());
            return parentBuilder;
        }

        public Builder setEndpoint(Vec3 endpoint) {
            position = endpoint;
            if (parentBuilder != null) {
                vector = position.subtract(parentBuilder.position);
            }
            return this;
        }

        public Bone build() {
            Bone bone = new Bone(this);
            for (Map.Entry<String, Bone> entry: bone.children.entrySet()) {
                entry.getValue().parent = bone;
            }
            return bone;
        }
    }
}
