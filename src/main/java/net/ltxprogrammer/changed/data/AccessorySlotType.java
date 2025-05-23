package net.ltxprogrammer.changed.data;

import net.ltxprogrammer.changed.item.AccessoryItem;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AccessorySlotType extends ForgeRegistryEntry<AccessorySlotType> {
    private TagKey<Item> itemTagKey = null;
    private ResourceLocation noItemIcon = null;
    private final EquipmentSlot equivalentSlot;

    public AccessorySlotType(EquipmentSlot equivalentSlot) {
        this.equivalentSlot = equivalentSlot;
    }

    public TagKey<Item> getItemTag() {
        if (itemTagKey != null)
            return itemTagKey;

        itemTagKey = TagKey.create(Registry.ITEM_REGISTRY, this.getRegistryName());
        return itemTagKey;
    }

    public boolean canHoldItem(ItemStack itemStack, LivingEntity wearer) {
        if (!itemStack.is(this.getItemTag()))
            return false;
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && !ext.allowedInSlot(itemStack, wearer, this))
            return false;
        return true;
    }

    public ResourceLocation getNoItemIcon() {
        if (noItemIcon != null)
            return noItemIcon;

        ResourceLocation id = this.getRegistryName();
        noItemIcon = new ResourceLocation(id.getNamespace(), "items/empty_" + id.getPath() + "_slot");
        return noItemIcon;
    }

    public EquipmentSlot getEquivalentSlot() {
        return equivalentSlot;
    }

    public void handleEvent(LivingEntity wearer, ItemStack itemStack, int event) {
        if (event == 1 && itemStack.getItem() instanceof AccessoryItem accessoryItem)
            accessoryItem.accessoryBreak(new AccessorySlotContext<>(wearer, this, itemStack));
    }
}
