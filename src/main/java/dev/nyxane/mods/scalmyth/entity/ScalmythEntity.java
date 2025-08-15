package dev.nyxane.mods.scalmyth.entity;

import dev.nyxane.mods.scalmyth.inversekinematics.Bone;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ScalmythEntity extends Entity {
    public final Bone rootBone = Bone.builder("root")
            .setEndpoint(new Vec3(0,1,0))
            .startChild("first")
            .setEndpoint(new Vec3(0, 0.5, 0))
            .startChild("second")
            .setEndpoint(new Vec3(0.5, 0.5, 0))
            .endChild()
            .endChild()
            .build();

    public ScalmythEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
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
}
