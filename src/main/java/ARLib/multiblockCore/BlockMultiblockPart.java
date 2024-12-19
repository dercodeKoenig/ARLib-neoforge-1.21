package ARLib.multiblockCore;

import ARLib.utils.DimensionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

import static ARLib.multiblockCore.BlockMultiblockMaster.STATE_MULTIBLOCK_FORMED;

public class BlockMultiblockPart extends Block {

    public static class BlockIdentifier{
        String levelId;
        BlockPos pos;
        public BlockIdentifier(String level, BlockPos pos){
            this.levelId = level;
            this.pos = pos;
        }
    }

    static final Map<BlockIdentifier, BlockPos> multiblockMasterPositions = new HashMap<>();

    public BlockMultiblockPart(Properties properties) {
        super(properties.noOcclusion().pushReaction(PushReaction.IGNORE));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STATE_MULTIBLOCK_FORMED, false));

    }

    public void setMaster(BlockIdentifier mypos, BlockPos masterpos) {
        if (masterpos == null && multiblockMasterPositions.containsKey(mypos))
            multiblockMasterPositions.remove(mypos);
        else
            multiblockMasterPositions.put(mypos, masterpos);
    }
    public BlockPos getMaster(BlockIdentifier mypos){
        return multiblockMasterPositions.get(mypos);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE_MULTIBLOCK_FORMED); // Define the state property
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(STATE_MULTIBLOCK_FORMED, false);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack) {
        world.setBlock(pos, state.setValue(STATE_MULTIBLOCK_FORMED, false), 3);
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if(!level.isClientSide) {
            if (state.getBlock() instanceof BlockMultiblockPart t) {
                BlockPos master = t.getMaster(new BlockIdentifier(DimensionUtils.getLevelId(level),pos));
                if (master != null && level.getBlockEntity(master) instanceof EntityMultiblockMaster masterTile) {
                    masterTile.scanStructure();
                }
                multiblockMasterPositions.remove(new BlockIdentifier(DimensionUtils.getLevelId(level), pos));
            }
        }
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockPos master = getMaster(new BlockIdentifier(DimensionUtils.getLevelId(level),pos));
            if (master != null && level.getBlockEntity(master) instanceof EntityMultiblockMaster masterTile && masterTile.forwardInteractionToMaster) {
                return masterTile.useWithoutItem(state, level, pos, player, hitResult);
            }
        }
        return InteractionResult.PASS;
    }
}
