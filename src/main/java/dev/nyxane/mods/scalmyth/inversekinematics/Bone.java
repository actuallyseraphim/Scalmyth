package dev.nyxane.mods.scalmyth.inversekinematics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bone {
    private String name;
    public Vec3 translation;
    public Bone parent;
    public Map<String, ? extends Bone> endConnections;

    public Vec3 resolvePosition() {
        return translation.subtract(parent.translation)
                .normalize().scale(translation.length()).add(parent.translation);
    }

    public void render(PoseStack poseStack, VertexConsumer buffer) {
        poseStack.pushPose();

        //buffer.addVertex(poseStack.last(),0f,0f,0f).setColor(1f,0f,0f,1f);
        poseStack.translate(translation.x, translation.y, translation.z);
        buffer.addVertex(poseStack.last(),0f,0f,0f).setColor(1f,0f,0f,1f);
        for (Map.Entry<String, ? extends Bone> entry: this.endConnections.entrySet()) {
            entry.getValue().render(poseStack, buffer);
        }
        poseStack.popPose();
    }

    Bone(Builder builder) {
        this.endConnections = builder.endConnections;
        this.translation = builder.translation;
        this.name = builder.name;
    }

    public static Builder builder(String name) {
        return new Builder(null, name);
    }

    public static class Builder {
        private final HashMap<String, Bone> endConnections = new HashMap<>();
        private final Builder parentBuilder;
        private final String name;
        private Vec3 translation = new Vec3(0,0,0);
        private Vec3 cumulitiveTranslation = new Vec3(0,0,0);

        public Builder(Builder parent, String name) {
            this.parentBuilder = parent;
            this.name = name;
        }

        public Builder startChild(String name) {
            return new Builder(this, name);
        }

        public Builder endChild() {
            parentBuilder.endConnections.put(name, this.build());
            return parentBuilder;
        }

        public Builder setEndpoint(Vec3 endpoint) {
            translation = cumulitiveTranslation.subtract(endpoint);
            cumulitiveTranslation = endpoint;
            return this;
        }

        public Bone build() {
            return new Bone(this);
        }
    }
}
