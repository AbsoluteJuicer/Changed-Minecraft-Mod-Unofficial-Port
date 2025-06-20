package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BloodSyringe extends Item implements SpecializedAnimations {
    public static final DamageSource BLOODLOSS = (new DamageSource("changed:bloodloss")).bypassArmor();

    public BloodSyringe(Properties p_41383_) {
        super(p_41383_.tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addOwnerTooltip(p_43359_, p_43361_);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public int getUseDuration(@NotNull ItemStack p_43001_) {
        return 16;
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }
        ChangedSounds.broadcastSound(entity, ChangedSounds.SWORD1, 1, 1);
        if (player != null) {
            if (ProcessTransfur.isPlayerTransfurred(player) || (stack.getOrCreateTag().contains("owner") && stack.getTag().getUUID("owner").equals(player.getUUID()))) {
                player.heal(1.0f);
            }

            else {
                player.addEffect(new MobEffectInstance(ChangedEffects.HYPERCOAGULATION, 800));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            stack = new ItemStack(ChangedItems.SYRINGE.get());
        }

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (stack.getTag() == null)
            return false;
        return stack.getTag().contains("owner");
    }

    @Nullable
    @Override
    public AnimationHandler getAnimationHandler() {
        return new Syringe.SyringeAnimation(this);
    }

    @Override
    public boolean triggerItemUseEffects(LivingEntity entity, ItemStack itemStack, int particleCount) {
        return true;
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnBlock extends Event {
        public final BlockPos blockPos;
        public final BlockState blockState;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;

        public UsedOnBlock(BlockPos blockPos, BlockState blockState, Level level, Player player, ItemStack syringe) {
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
        }

        @Override public boolean isCancelable() { return true; }
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnEntity extends Event {
        public final LivingEntity entity;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;

        public UsedOnEntity(LivingEntity entity, Level level, Player player, ItemStack syringe) {
            this.entity = entity;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
        }

        @Override public boolean isCancelable() { return true; }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        return Changed.postModEvent(
                new UsedOnBlock(context.getClickedPos(),
                        clickedState,
                        context.getLevel(),
                        context.getPlayer(),
                        context.getItemInHand())) ?
                InteractionResult.sidedSuccess(context.getLevel().isClientSide) :
                super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        return Changed.postModEvent(
                new UsedOnEntity(livingEntity,
                        player.level,
                        player,
                        itemStack)) ?
                InteractionResult.sidedSuccess(player.level.isClientSide) :
                super.interactLivingEntity(itemStack, player, livingEntity, hand);
    }
}
