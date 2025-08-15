package dev.nyxane.mods.scalmyth.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.nyxane.mods.scalmyth.api.ScalmythAPI;
import dev.nyxane.mods.scalmyth.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import oshi.util.tuples.Pair;

import java.util.Iterator;

@EventBusSubscriber(modid = ScalmythAPI.MOD_ID, value = Dist.CLIENT)
public class RenderEventHandler {
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (VisualDebug.lines.isEmpty()) {
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        if (player.getMainHandItem().getItem() != ModItems.ASH_DUST.get()) return;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.DEBUG_LINES,
                VertexFormat.builder()
                        .add("Position", VertexFormatElement.POSITION)
                        .add("Color", VertexFormatElement.COLOR)
                        .build()
        );

        for (Pair<Position, Position> line : VisualDebug.lines) {
            Position start = line.getA();
            Position end = line.getB();
            buffer.addVertex(
                    (float) start.x(),
                    (float) start.y(),
                    (float) start.z()
            ).setColor(1f, 0f, 0f, 1f);
            buffer.addVertex(
                    (float) end.x(),
                    (float) end.y(),
                    (float) end.z()
            ).setColor(1f, 0f, 0f, 1f);
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        vertexBuffer.bind();
        vertexBuffer.upload(buffer.buildOrThrow());
        vertexBuffer.draw();
    }
}
