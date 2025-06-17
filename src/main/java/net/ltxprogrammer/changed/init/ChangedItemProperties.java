package net.ltxprogrammer.changed.init;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedItemProperties {
    @SubscribeEvent
    public static void registerProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ChangedItems.TSC_SHIELD.get(), new ResourceLocation("blocking"), (item, level, entity, i) -> {
                return entity != null && entity.isUsingItem() && entity.getUseItem() == item ? 1.0F : 0.0F;
            });
        });
    }
}
