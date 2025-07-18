package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Lifecycle;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.client.latexparticles.LatexParticleType;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.animation.AnimationEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ChangedRegistry<T> extends Registry<T> {
    private static final Logger LOGGER = LogManager.getLogger(ChangedRegistry.class);

    private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
    private static final HashMap<ResourceKey<Registry<?>>, Supplier<ForgeRegistry<?>>> REGISTRY_HOLDERS = new HashMap<>();

    public static class RegistryHolder<T extends IForgeRegistryEntry<T>>implements Supplier<ForgeRegistry<T>> {
        protected final ResourceKey<Registry<T>> key;

        public RegistryHolder(ResourceKey<Registry<T>> key) {
            this.key = key;
        }

        @Override
        public ForgeRegistry<T> get() {
            if (REGISTRY_HOLDERS.isEmpty())
                throw new IllegalStateException("Cannot access registries before creation");
            return (ForgeRegistry<T>) REGISTRY_HOLDERS.get(key).get();
        }

        public DeferredRegister<T> createDeferred(String modId) {
            return DeferredRegister.create(key, modId);
        }
    }

    // TODO rename registeries to be plural, and have modern names
    public static final RegistryHolder<TransfurVariant<?>> TRANSFUR_VARIANT = new RegistryHolder<TransfurVariant<?>>(registryKey("latex_variant"));
    public static final RegistryHolder<AbstractAbility<?>> ABILITY = new RegistryHolder<AbstractAbility<?>>(registryKey("ability"));
    public static final RegistryHolder<HairStyle> HAIR_STYLE = new RegistryHolder<HairStyle>(registryKey("hair_style"));
    public static final RegistryHolder<PlayerMover<?>> PLAYER_MOVER = new RegistryHolder<PlayerMover<?>>(registryKey("player_mover"));
    public static final RegistryHolder<LatexParticleType<?>> LATEX_PARTICLE_TYPE = new RegistryHolder<LatexParticleType<?>>(registryKey("latex_particle_type"));
    public static final RegistryHolder<AnimationEvent<?>> ANIMATION_EVENTS = new RegistryHolder<AnimationEvent<?>>(registryKey("animation_events"));
    public static final RegistryHolder<AccessorySlotType> ACCESSORY_SLOTS = new RegistryHolder<AccessorySlotType>(registryKey("accessory_slots"));

    @SubscribeEvent
    public static void onCreateRegistries(NewRegistryEvent event) {
        createRegistry(event, TRANSFUR_VARIANT.key, c(TransfurVariant.class), builder -> {
            builder.hasTags();
            builder.missing((key, network) -> ChangedTransfurVariants.FALLBACK_VARIANT.get());
        }, null);
        createRegistry(event, ABILITY.key, c(AbstractAbility.class));
        createRegistry(event, HAIR_STYLE.key, HairStyle.class, builder -> {
            builder.missing((key, network) -> HairStyle.BALD.get());
        }, null);
        createRegistry(event, PLAYER_MOVER.key, c(PlayerMover.class), builder -> {
            builder.missing((key, network) -> PlayerMover.DEFAULT_MOVER.get());
        }, null);
        createRegistry(event, LATEX_PARTICLE_TYPE.key, c(LatexParticleType.class));
        createRegistry(event, ANIMATION_EVENTS.key, c(AnimationEvent.class));
        createRegistry(event, ACCESSORY_SLOTS.key, AccessorySlotType.class);
    }

    private static <T extends IForgeRegistryEntry<T>> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key, Class<T> type) {
        createRegistry(event, key, type, null, null);
    }

    private static <T extends IForgeRegistryEntry<T>> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key, Class<T> type,
                                                                          @Nullable Consumer<RegistryBuilder<T>> additionalBuilder,
                                                                          @Nullable Consumer<IForgeRegistry<T>> onFill) {
        var builder = makeRegistry(key, type);
        if (additionalBuilder != null)
            additionalBuilder.accept(builder);
        Supplier<IForgeRegistry<T>> holder = event.create(builder, onFill);
        REGISTRY_HOLDERS.put((ResourceKey)key, () -> (ForgeRegistry<?>)holder.get());
        LOGGER.info("Created registry {}", key);
    }

    static <T> Class<T> c(Class<?> cls) { return (Class<T>)cls; }
    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key, Class<T> type) {
        return new RegistryBuilder<T>().setName(key.location()).setType(type).setMaxID(MAX_VARINT);
    }

    private ChangedRegistry(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    private static <T> ResourceKey<Registry<T>> registryKey(String name) {
        return ResourceKey.createRegistryKey(Changed.modResource(name));
    }
}
