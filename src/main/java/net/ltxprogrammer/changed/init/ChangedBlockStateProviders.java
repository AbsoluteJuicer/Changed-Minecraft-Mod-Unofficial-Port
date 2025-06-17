package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.DeferredStateProvider;
import net.ltxprogrammer.changed.data.MixedStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ChangedBlockStateProviders {
    public static final DeferredRegister<BlockStateProviderType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, Changed.MODID);
    public static final RegistryObject<BlockStateProviderType<DeferredStateProvider>> DEFERRED_STATE_PROVIDER
            = REGISTRY.register("deferred_state_provider", () -> new BlockStateProviderType<>(DeferredStateProvider.CODEC));
    public static final RegistryObject<BlockStateProviderType<MixedStateProvider>> MIXED_STATE_PROVIDER
            = REGISTRY.register("mixed_state_provider", () -> new BlockStateProviderType<>(MixedStateProvider.CODEC));
}
