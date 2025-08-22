package dev.nyxane.mods.scalmyth.inversekinematics;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

public abstract class IKModel extends Model {
    private List<Bone> bones;
    public IKModel(Function<ResourceLocation, RenderType> renderType) {
        super(renderType);
    }
}
