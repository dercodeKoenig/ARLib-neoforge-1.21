package ARLib.multiblocks.lathe;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.IGuiHandler;
import ARLib.multiblockCore.BlockEntityMultiblock;
import ARLib.network.INetworkTagReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static ARLib.multiblocks.MultiblockRegistry.BLOCK_LATHE;
import static ARLib.multiblocks.MultiblockRegistry.ENTITY_LATHE;


public class EntityLathe extends BlockEntityMultiblock implements INetworkTagReceiver {

    IGuiHandler guiHandler;

    public EntityLathe(BlockPos pos, BlockState state) {
        super(ENTITY_LATHE.get(), pos, state);
        setupCharmappings();
        guiHandler=new GuiHandlerBlockEntity(this);
    }


    void setupCharmappings(){
        List<Block> c = new ArrayList<>();
        c.add(BLOCK_LATHE.get());
        addMapping('c',c);
    }
    public static final Object[][][] structure = {
            {{'c', Blocks.DIAMOND_BLOCK}},
    };
    @Override
    public Object[][][] getStructure() {
        return structure;
    }


    @Override
    public void readServer(CompoundTag tag) {
        guiHandler.readServer(tag);
    }

    @Override
    public void readClient(CompoundTag tag) {
        guiHandler.readClient(tag);
    }

    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {
        if(!level.isClientSide)
            IGuiHandler.serverTick(((EntityLathe)t).guiHandler);
    }
}
