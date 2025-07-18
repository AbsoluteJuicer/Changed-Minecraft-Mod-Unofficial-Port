package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public abstract class AbstractPooltoy extends ChangedEntity implements PowderSnowWalkable {
    protected float crouchImpulse = 0f;
    protected Pose lastPose = Pose.STANDING;

    public AbstractPooltoy(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.NONE; }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return NeoForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return NeoForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_20139_) {
        super.addAdditionalSaveData(p_20139_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.wolfLike(attributes);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    public void applyTerminalVelocity(LivingEntity entity) {
        var delta = entity.getDeltaMovement();
        entity.setDeltaMovement(delta.x, Mth.clamp(delta.y, -0.5f, 0.5f), delta.z);
        entity.resetFallDistance();
    }

    @Override
    public float getVerticalSpringOffset() {
        return crouchImpulse;
    }

    @Override
    public void variantTick(Level level) {
        var currentPose = getPose();

        if (lastPose == Pose.STANDING && currentPose == Pose.CROUCHING) {
            crouchImpulse = -0.7f;
        }

        else if (lastPose == Pose.CROUCHING && currentPose == Pose.STANDING) {
            crouchImpulse = 0.7f;
        }

        else {
            crouchImpulse = 0f;
        }

        lastPose = currentPose;

        super.variantTick(level);
        final var entity = maybeGetUnderlying();
        if (entity.isInWaterOrBubble()) {
            double floatAmount = (double)entity.getAirSupply() / (double)entity.getMaxAirSupply();
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, floatAmount * 0.06, 0.0D));
        }
        applyTerminalVelocity(entity);
    }
}
