package ARLib.multiblocks.lathe;

import ARLib.multiblockCore.BlockMultiblockMaster;
import ARLib.obj.ModelFormatException;
import ARLib.obj.WavefrontObject;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
            stack.pushPose();

            // Get the facing direction of the block
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            // Apply rotation to the PoseStack based on the facing direction
            Vector3f axis = new Vector3f(0, 1, 0);
            float angle = 0;

            switch (facing) {
                case NORTH:
                    angle = 90;
                    break;
                case EAST:
                    angle = 0;
                    break;
                case SOUTH:
                    angle = 270;
                    break;
                case WEST:
                    angle = 180;
                    break;
            }
            angle = (float) Math.toRadians(angle);
            Quaternionf quaternion = new Quaternionf().fromAxisAngleRad(axis, angle);
            stack.rotateAround(quaternion, 0.5f, 0, 0.5f);

// move so that the model aligns with the structure
            stack.translate(0,-1,-2);

            model.renderAll(stack, bufferSource, packedLight, packedOverlay);
            stack.popPose();
        }
    }
}

