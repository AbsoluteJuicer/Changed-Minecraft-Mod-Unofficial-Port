package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.client.event.ColorHandlerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

public class DyeableAbdomenArmor extends AbdomenArmor implements DyeableLeatherItem {
    public DyeableAbdomenArmor(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot);
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientInitializer {
        @SubscribeEvent
        public static void onItemColorsInit(ColorHandlerEvent.Item event) {
            event.getItemColors().register(
                    (stack, layer) -> layer > 0 ? -1 : ((DyeableLeatherItem)stack.getItem()).getColor(stack),
                    ChangedItems.LEATHER_LOWER_ABDOMEN_ARMOR.get(), ChangedItems.LEATHER_UPPER_ABDOMEN_ARMOR.get());
        }
    }
}
