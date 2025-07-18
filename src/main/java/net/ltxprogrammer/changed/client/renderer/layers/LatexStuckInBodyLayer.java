package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public abstract class LatexStuckInBodyLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> {
    public LatexStuckInBodyLayer(LivingEntityRenderer<T, M> p_117564_) {
        super(p_117564_);
    }

    protected abstract int numStuck(T p_117565_);

    protected abstract void renderStuckItem(PoseStack p_117566_, MultiBufferSource p_117567_, int p_117568_, Entity p_117569_, float p_117570_, float p_117571_, float p_117572_, float p_117573_);

    public void render(PoseStack p_117586_, MultiBufferSource p_117587_, int p_117588_, T p_117589_, float p_117590_, float p_117591_, float p_117592_, float p_117593_, float p_117594_, float p_117595_) {
        boolean pushedPose = false;

        try {
            int i = this.numStuck(p_117589_);
            Random random = new Random((long)p_117589_.getId());
            if (i > 0) {
                for(int j = 0; j < i; ++j) {
                    p_117586_.pushPose();
                    pushedPose = true;
                    ModelPart modelpart = this.getParentModel().getRandomModelPart(random);
                    ModelPart.Cube modelpart$cube = modelpart.getRandomCube(random);
                    modelpart.translateAndRotate(p_117586_);
                    float f = random.nextFloat();
                    float f1 = random.nextFloat();
                    float f2 = random.nextFloat();
                    float f3 = Mth.lerp(f, modelpart$cube.minX, modelpart$cube.maxX) / 16.0F;
                    float f4 = Mth.lerp(f1, modelpart$cube.minY, modelpart$cube.maxY) / 16.0F;
                    float f5 = Mth.lerp(f2, modelpart$cube.minZ, modelpart$cube.maxZ) / 16.0F;
                    p_117586_.translate((double)f3, (double)f4, (double)f5);
                    f = -1.0F * (f * 2.0F - 1.0F);
                    f1 = -1.0F * (f1 * 2.0F - 1.0F);
                    f2 = -1.0F * (f2 * 2.0F - 1.0F);
                    this.renderStuckItem(p_117586_, p_117587_, p_117588_, p_117589_, f, f1, f2, p_117592_);
                    p_117586_.popPose();
                    pushedPose = false;
                }
            }
        }

        catch (IllegalArgumentException e) {
            if (pushedPose)
                p_117586_.popPose();
        }
    }
}