package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ForgeMod;

public class Shark extends AbstractAquaticEntity {
    public Shark(EntityType<? extends Shark> p_28316_, Level p_28317_) {
        super(p_28316_, p_28317_);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(10.0D);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.2D);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(2.4D);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public TransfurVariant<?> getSelfVariant() {
        return null;
    }

    @Override
    public TransfurVariant<?> getTransfurVariant() {
        return this.level.getSeaLevel() - 6 > this.getBlockY() ? ChangedTransfurVariants.Gendered.LATEX_MERMAID_SHARKS.getRandomVariant(this.random) : ChangedTransfurVariants.LATEX_SHARK.get();
    }

    protected PathNavigation createNavigation(Level p_28362_) {
        return new WaterBoundPathNavigation(this, p_28362_);
    }

    protected float getStandingEyeHeight(Pose p_28352_, EntityDimensions p_28353_) {
        return 0.3F;
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    public void tick() {
        super.tick();
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
        } else {
            if (!this.isInWaterRainOrBubble()) {
                if (this.onGround) {
                    this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
                    this.setYRot(this.random.nextFloat() * 360.0F);
                    this.onGround = false;
                    this.hasImpulse = true;
                }
            }

            if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
                Vec3 vec3 = this.getViewVector(0.0F);
                float f = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * 0.3F;
                float f1 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * 0.3F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;

                for(int i = 0; i < 2; ++i) {
                    this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * (double)f2 + (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f2 + (double)f1, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * (double)f2 - (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f2 - (double)f1, 0.0D, 0.0D, 0.0D);
                }
            }

        }
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }
}