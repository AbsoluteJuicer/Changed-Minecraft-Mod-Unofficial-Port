package net.ltxprogrammer.changed.util;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.ltxprogrammer.changed.data.OnlineTexture;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.client.ForgeHooksClient;
import net.neoforged.fml.DistExecutor;

import java.util.function.Supplier;

public class DynamicClient {
    @OnlyIn(Dist.CLIENT)
    private static class Internal {
        public static void lateRegisterLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
            ForgeHooksClient.registerLayerDefinition(layerLocation, supplier);

            Minecraft minecraft = Minecraft.getInstance();
            EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
            ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder = ImmutableMap.builder();
            dispatcher.entityModels.roots.forEach((location, definition) -> {
                if (!location.equals(layerLocation))
                    builder.put(location, definition);
            });
            builder.put(layerLocation, supplier.get());
            dispatcher.entityModels.roots = builder.build();
        }
    }

    public static void lateRegisterLayerDefinition(DeferredModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Internal.lateRegisterLayerDefinition(layerLocation.get(), supplier));
    }

    public static void lateRegisterOnlineTexture(Resource resource) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ChangedTextures.lateRegisterTexture(resource.getLocation(), () -> OnlineTexture.of(resource)));
    }
}
