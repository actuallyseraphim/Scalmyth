package dev.nyxane.mods.scalmyth.registry;

import dev.nyxane.mods.scalmyth.api.ScalmythAPI;
import dev.nyxane.mods.scalmyth.entity.ScalmythEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ScalmythAPI.MOD_ID);
    public static final Supplier<EntityType<ScalmythEntity>> SCALMYTH =
            REGISTRY.register("scalmyth", () -> EntityType.Builder.of(ScalmythEntity::new, MobCategory.MONSTER)
                    .sized(1f,1f)
                    .build("scalmyth"));

    public static void register(IEventBus eventBus) {
    REGISTRY.register(eventBus);
    }
    @EventBusSubscriber(modid = ScalmythAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void entityAttributeEvent(final EntityAttributeCreationEvent event) {
        }
    }
}


