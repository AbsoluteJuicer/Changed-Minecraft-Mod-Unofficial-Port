package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class LatexTippedArrowItem extends TippedArrowItem implements VariantHoldingBase {
    public static final String FORM_LOCATION = Changed.modResourceStr("form");

    public LatexTippedArrowItem() {
        super(new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));

        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level p_123420_, Position p_123421_, ItemStack p_123422_) {
                AbstractArrow arrow = createArrow(p_123420_, p_123422_, p_123421_.x(), p_123421_.y(), p_123421_.z());
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return arrow;
            }
        });
    }

    @SubscribeEvent
    public static void onLivingDamaged(LivingDamageEvent event) {
        if (event.getSource() instanceof IndirectEntityDamageSource indirect) {
            if (indirect.getDirectEntity() instanceof Arrow arrow) {
                if (arrow.getPersistentData().contains(FORM_LOCATION)) {
                    final var variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(TagUtil.getResourceLocation(arrow.getPersistentData(), FORM_LOCATION));
                    ProcessTransfur.progressTransfur(event.getEntityLiving(), 8.0f, variant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE));
                    arrow.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
    }

    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) {
            TransfurVariant.getPublicTransfurVariants().forEach(variant -> {
                list.add(Syringe.setOwner(Syringe.setPureVariant(new ItemStack(this), variant.getRegistryName()), UniversalDist.getLocalPlayer()));
            });
        }
    }

    public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, double p_36862_, double p_36863_, double p_36864_) {
        Arrow arrow = new Arrow(p_40513_, p_36862_, p_36863_, p_36864_);
        TagUtil.putResourceLocation(arrow.getPersistentData(), FORM_LOCATION, Syringe.getVariant(p_40514_).getFormId());
        return arrow;
    }

    public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
        Arrow arrow = new Arrow(p_40513_, p_40515_);
        TagUtil.putResourceLocation(arrow.getPersistentData(), FORM_LOCATION, Syringe.getVariant(p_40514_).getFormId());
        return arrow;
    }

    public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    public String getDescriptionId(ItemStack p_43364_) {
        return getOrCreateDescriptionId();
    }

    @Override
    public @NotNull Rarity getRarity(ItemStack stack) {
        if (stack.getTag() == null)
            return Rarity.COMMON;
        return stack.getTag().contains("safe") ? (stack.getTag().getBoolean("safe") ? Rarity.RARE : Rarity.UNCOMMON) : Rarity.UNCOMMON;
    }

    @Override
    public Item getOriginalItem() {
        return Items.ARROW;
    }
}
