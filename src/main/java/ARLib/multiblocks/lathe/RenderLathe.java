package ARLib.multiblocks.lathe;

import ARLib.multiblockCore.BlockMultiblockMaster;
import ARLib.obj.ModelFormatException;
import ARLib.obj.WavefrontObject;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import static ARLib.multiblockCore.BlockMultiblockMaster.STATE_MULTIBLOCK_FORMED;

public class RenderLathe implements BlockEntityRenderer<EntityLathe> {
    // Add the constructor parameter for the lambda below. You may also use it to get some context
    // to be stored in local fields, such as the entity renderer dispatcher, if needed.

    ResourceLocation modelsrc = ResourceLocation.fromNamespaceAndPath("arlib", "multiblock/lathe.obj");
    ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("arlib", "multiblock/lathe.png");

    //ResourceLocation modelsrc = ResourceLocation.fromNamespaceAndPath("arlib","multiblock/crystalliser.obj");
    //ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("arlib","multiblock/crystalliser.png");

    WavefrontObject model;

    public RenderLathe(BlockEntityRendererProvider.Context context) {
        try {
            model = new WavefrontObject(modelsrc, tex);
        } catch (ModelFormatException e) {
            throw new RuntimeException(e);
        }
    }

    // This method is called every frame in order to render the block entity. Parameters are:
    // - blockEntity:   The block entity instance being rendered. Uses the generic type passed to the super interface.
    // - partialTick:   The amount of time, in fractions of a tick (0.0 to 1.0), that has passed since the last tick.
    // - poseStack:     The pose stack to render to.
    // - bufferSource:  The buffer source to get vertex buffers from.
    // - packedLight:   The light value of the block entity.
    // - packedOverlay: The current overlay value of the block entity, usually OverlayTexture.NO_OVERLAY.
    @Override
    public void render(EntityLathe tile, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if (state.getBlock() instanceof BlockMultiblockMaster) {
            if (state.getValue(STATE_MULTIBLOCK_FORMED) == false) {
                return;
            }

            model.renderAll(stack, bufferSource, packedLight, packedOverlay);

        }
    }
}

