package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fluids.FluidAttributes;
import net.neoforged.fluids.ForgeFlowingFluid;

import java.util.List;

public abstract class DarkLatexFluid extends AbstractLatexFluid {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            ChangedFluids.DARK_LATEX,
            ChangedFluids.DARK_LATEX_FLOWING,
            FluidAttributes.builder(Changed.modResource("blocks/dark_latex_block_top"), Changed.modResource("blocks/dark_latex_block_top"))
                    .viscosity(6000).color(0xFFFFFFFF))
            .explosionResistance(100f)
            .tickRate(50).levelDecreasePerBlock(3)
            .bucket(ChangedItems.DARK_LATEX_BUCKET).block(ChangedBlocks.DARK_LATEX_FLUID);

    protected DarkLatexFluid() {
        super(PROPERTIES, LatexType.DARK_LATEX, List.of(ChangedTransfurVariants.DARK_LATEX_WOLF_MALE, ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE, ChangedTransfurVariants.DARK_LATEX_YUFENG));
    }

    @Override
    public Vec3 getFlow(BlockGetter world, BlockPos pos, FluidState fluidstate) {
        return super.getFlow(world, pos, fluidstate).scale(-1);
    }


    public BlockState createLegacyBlock(FluidState p_76466_) {
        return ChangedBlocks.DARK_LATEX_FLUID.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(p_76466_));
    }

    @Override
    public boolean canEntityStandOn(LivingEntity entity) {
        var variant = TransfurVariant.getEntityVariant(entity);
        return variant != null && variant.getLatexType() == LatexType.DARK_LATEX;
    }

    public static class Source extends DarkLatexFluid {
        public Source() {
            super();
        }

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends DarkLatexFluid {
        public Flowing() {
            super();
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
