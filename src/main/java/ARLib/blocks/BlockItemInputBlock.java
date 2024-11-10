package ARLib.blocks;

import ARLib.gui.GuiCapableBlockEntity;
import ARLib.blockentities.EntityItemInputBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static ARLib.ARLibRegistry.ENTITY_ITEM_INPUT_BLOCK;


public class BlockItemInputBlock extends Block  implements EntityBlock {
    public BlockItemInputBlock(Properties p_49795_) {
        super(p_49795_);
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return ENTITY_ITEM_INPUT_BLOCK.get().create(blockPos,blockState);
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity e = world.getBlockEntity(pos);
        if (e instanceof GuiCapableBlockEntity) {

            if (world.isClientSide) {
                ((GuiCapableBlockEntity)e).openGui();
            }
                //player.inventoryMenu.setCarried(new ItemStack(Items.NAME_TAG));


        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // You can return different tickers here, depending on whatever factors you want. A common use case would be
        // to return different tickers on the client or server, only tick one side to begin with,
        // or only return a ticker for some blockstates (e.g. when using a "my machine is working" blockstate property).
        return type == ENTITY_ITEM_INPUT_BLOCK.get() ? EntityItemInputBlock::tick : null;
    }

}
