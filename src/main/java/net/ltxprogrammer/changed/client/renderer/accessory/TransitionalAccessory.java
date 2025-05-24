package net.ltxprogrammer.changed.client.renderer.accessory;

import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.stream.Stream;

public interface TransitionalAccessory {
    Optional<HumanoidModel<?>> getBeforeModel(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent);
    Stream<AdvancedHumanoidModel<?>> getAfterModels(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent);

    Optional<ResourceLocation> getModelTexture(AccessorySlotContext<?> slotContext);
}
