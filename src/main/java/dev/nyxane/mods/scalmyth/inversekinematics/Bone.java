package dev.nyxane.mods.scalmyth.inversekinematics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.nyxane.mods.scalmyth.Scalmyth;
import dev.nyxane.mods.scalmyth.api.ScalmythAPI;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;

public class Bone {
    private String name;
    private Bone parent;
    private ModelPart modelPart;

    public HashMap<String, Bone> children;

    private float boneLength = 1;
    private Quaternionf quaternion = new Quaternionf();
    private Vector3f twistAxis = new Vector3f(0,1,0);
    private float maxSwingConeAngle = 1;

    private final Entity entity;
    private Vector3f desiredPosition = new Vector3f(0,0,0);
    private float desiredAngle = 0;

    // double to float cast for not writing f
    public Bone(String name, double length, Entity entity) {
        this.name = name;
        this.boneLength = (float) length;
        this.entity = entity;
        this.children = new HashMap<>();
    }

    public void resolveIK() {
        if (parent == null) return;
        Vector3f ppos = parent.getPosition();
        Vector3f vec = getVector();
        Vector3f pos = ppos.add(vec, new Vector3f());
        Vector3f err = desiredPosition.sub(pos, new Vector3f());

        // positional correction
        Quaternionf quaternion_err = new Quaternionf().rotateTo(vec, vec.add(err, new Vector3f())).normalize();
        quaternion = quaternion_err.mul(quaternion, new Quaternionf());

        // swing twist decomposition
        Vector3f twistV = new Vector3f(quaternion.x, quaternion.y, quaternion.z);
        twistAxis.mul(twistV.dot(twistAxis)/twistAxis.dot(twistAxis), twistV);
        Quaternionf twist = new Quaternionf(twistV.x, twistV.y, twistV.z, quaternion.w);
        Quaternionf swing = quaternion.mul(twist.invert(new Quaternionf()), new Quaternionf()).normalize();
        twist.normalize();

        // swing constraint
        Vector3f swungAxis = swing.transform(new Vector3f(twistAxis));
        float angle = (float)Math.acos(swungAxis.dot(twistAxis));
        if (angle > maxSwingConeAngle) {
            float t = maxSwingConeAngle/angle;
            swing.slerp(new Quaternionf(), t).normalize();
        }

        // recombination
        quaternion = swing.mul(twist, new Quaternionf()).normalize();
        //ScalmythAPI.LOGGER.debug("q: {}, t: {}", quaternion, swing.mul(twist, new Quaternionf()));

        // propagation
        parent.setDesiredPosition(desiredPosition.sub(getVector(), new Vector3f()));
        //parent.resolveIK();
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

    public Vector3f getDesiredPosition() {
        return new Vector3f(desiredPosition);
    }

    public void setDesiredPosition(Vector3f desiredPosition) {
        this.desiredPosition = desiredPosition;
/*        if (!this.desiredPosition.isFinite()) {
            this.desiredPosition = desiredPosition;
            return;
        }
        ClipContext ctx = new ClipContext(
                new Vec3(this.desiredPosition).add(entity.position()),
                new Vec3(desiredPosition).add(entity.position()),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
        );
        BlockHitResult hit = entity.level().clip(ctx);
        if (hit.isInside()) {
            this.desiredPosition = desiredPosition;
            return;
        }
        Vector3f hit_pos = hit.getLocation().subtract(entity.position()).toVector3f();
        Vec3i inorm = hit.getDirection().getNormal();
        Vector3f normal = new Vector3f(inorm.getX(), inorm.getY(), inorm.getZ());
        Vector3f err = normal.mul(desiredPosition.sub(hit_pos, new Vector3f()), new Vector3f());
        
        this.desiredPosition.set(hit_pos.sub(err, new Vector3f()));*/
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
