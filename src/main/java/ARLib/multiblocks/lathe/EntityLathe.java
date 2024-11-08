package ARLib.multiblocks.lathe;

import ARLib.multiblockCore.BlockEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static ARLib.multiblocks.MultiblockRegistry.BLOCK_LATHE;
import static ARLib.multiblocks.MultiblockRegistry.ENTITY_LATHE;


public class EntityLathe extends BlockEntityMultiblock  {

    public EntityLathe(BlockPos pos, BlockState state) {
        super(ENTITY_LATHE.get(), pos, state);
        setupCharmappings();
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
    public void tick(Level level, BlockPos blockPos, BlockState blockState, EntityLathe entityLathe) {
System.out.println("tick");
    }
}
