package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.fluid.*;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.RegistryEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedFluids {
    public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(NeoForgeRegistries.FLUIDS, Changed.MODID);
    private static final Map<RegistryObject<? extends Fluid>, Consumer<Fluid>> REGISTRY_CRL = new HashMap<>();

    public static final RegistryObject<AbstractLatexFluid> DARK_LATEX = register("dark_latex", DarkLatexFluid.Source::new);
    public static final RegistryObject<AbstractLatexFluid> DARK_LATEX_FLOWING = register("dark_latex_flowing", DarkLatexFluid.Flowing::new);
    public static final RegistryObject<AbstractLatexFluid> WHITE_LATEX = register("white_latex", WhiteLatexFluid.Source::new);
    public static final RegistryObject<AbstractLatexFluid> WHITE_LATEX_FLOWING = register("white_latex_flowing", WhiteLatexFluid.Flowing::new);

    public static final RegistryObject<TransfurGas> WOLF_GAS = register("wolf_gas", WolfGas.Source::new, ChangedFluids::translucentRenderer);
    public static final RegistryObject<TransfurGas> WOLF_GAS_FLOWING = register("wolf_gas_flowing", WolfGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static final RegistryObject<TransfurGas> TIGER_GAS = register("tiger_gas", TigerGas.Source::new, ChangedFluids::translucentRenderer);
    public static final RegistryObject<TransfurGas> TIGER_GAS_FLOWING = register("tiger_gas_flowing", TigerGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static final RegistryObject<TransfurGas> SKUNK_GAS = register("skunk_gas", SkunkGas.Source::new, ChangedFluids::translucentRenderer);
    public static final RegistryObject<TransfurGas> SKUNK_GAS_FLOWING = register("skunk_gas_flowing", SkunkGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static void cutoutRenderer(Fluid fluid) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(fluid, renderType -> renderType == RenderType.cutout()));
    }

    public static void translucentRenderer(Fluid fluid) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(fluid, renderType -> renderType == RenderType.translucent()));
    }

    public static <T extends Fluid, F extends T> RegistryObject<T> register(String name, Supplier<F> fluid) {
        return register(name, fluid, null);
    }

    public static <T extends Fluid, F extends T> RegistryObject<T> register(String name, Supplier<F> fluid, @Nullable Consumer<Fluid> renderLayer) {
        RegistryObject<T> fluidObject = REGISTRY.register(name, fluid);
        if (renderLayer != null)
            REGISTRY_CRL.put(fluidObject, renderLayer);
        return fluidObject;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Fluid> event) {
        REGISTRY_CRL.forEach((fluid, fluidConsumer) -> fluidConsumer.accept(fluid.get()));
    }
}
