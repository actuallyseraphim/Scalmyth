package dev.nyxane.mods.scalmyth.entity;

import dev.nyxane.mods.scalmyth.inversekinematics.Bone;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ScalmythEntity extends Entity {
    public final Bone rootBone;

    public ScalmythEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.rootBone = new Bone("root",0);
        Bone first = new Bone("first", 0.5);
        rootBone.addChild(first);
        Bone second = new Bone("second", 0.5);
        first.addChild(second);
        Bone third = new Bone("third", 0.5);
        second.addChild(third);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void tick() {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit == null) return;
        Bone last = rootBone
                .getChild("first")
                .getChild("second")
                .getChild("third");
        last.setDesiredPosition(hit.getLocation().subtract(getPosition(0)));
        last.resolveIK();
    }
}
