package ARLib.multiblockCore;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.gui.IGuiHandler;
import ARLib.multiblocks.lathe.EntityLathe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockEntityMultiblock extends BlockEntity{

    // scan by block
    protected static HashMap<Character, List<Block>> charMapping = new HashMap<>();
    boolean isComplete = false;


    public BlockEntityMultiblock(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        isComplete = false;
    }

    public static void addMapping(char character, List<Block> listToAdd) {
        charMapping.put(character, listToAdd);
    }
    public static List<Block> getMapping(char character) {
        return charMapping.get(character);
    }

    public boolean getIsComplete() {
        return isComplete;
    }




    public Object[][][] getStructure() {
        return null;
    }
    protected Vec3i getControllerOffset(Object[][][] structure) {
        for(int y = 0; y < structure.length; y++) {
            for(int z = 0; z < structure[0].length; z++) {
                for(int x = 0; x< structure[0][0].length; x++) {
                    if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
                        return new Vec3i(x, y, z);
                }
            }
        }
        return null;
    }
    public boolean completeStructure(BlockState state) {
        Object[][][] structure = getStructure();

        Direction front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Vec3i offset = getControllerOffset(structure);

        for(int y = 0; y < structure.length; y++) {
            for(int z = 0; z < structure[0].length; z++) {
                for(int x = 0; x< structure[0][0].length; x++) {
                    //Ignore nulls
                    if(structure[y][z][x] == null)
                        continue;

                    int globalX = getBlockPos().getX() + (x - offset.getX())*front.getStepZ() - (z-offset.getZ())*front.getStepX();
                    int globalY = getBlockPos().getY() - y + offset.getY();
                    int globalZ = getBlockPos().getZ() - (x - offset.getX())*front.getStepX()  - (z-offset.getZ())*front.getStepZ();
                    BlockPos globalPos = new BlockPos(globalX, globalY, globalZ);

                    ChunkAccess chunk = level.getChunk(globalPos);
                    boolean isLoaded = level.hasChunk(chunk.getPos().x, chunk.getPos().z);
                    if(!isLoaded)
                        return false;

                    BlockState blockState = level.getBlockState(globalPos);
                    Block block = blockState.getBlock();

                    if (!getAllowableBlocks(structure[y][z][x]).contains(block)){
                        return false;
                    }

                }}}

        isComplete = true;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        setChanged();
        return true;
    }

    public List<Block> getAllowableWildCardBlocks() {
        List<Block> list = new ArrayList<>();
        return list;
    }

    public List<Block> getAllowableBlocks(Object input) {
        if(input instanceof Character && (Character)input == '*') {
            return getAllowableWildCardBlocks();
        }
        else if(input instanceof Character  && charMapping.containsKey(input)) {
            return charMapping.get(input);
        }
        else if(input instanceof String) { //OreDict entry
            // [rage quit #23]
        }
        else if(input instanceof Block) {
            List<Block> list = new ArrayList<>();
            list.add((Block) input);
            return list;
        }
        else if(input instanceof List) {
            return (List<Block>)input;
        }
        return new ArrayList<>();
    }



    // This is for saving to disk - your main save method now
    // Replaces the old writeToNBT basically
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("isComplete", isComplete);
    }
    // LOAD from disk
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        isComplete = tag.getBoolean("isComplete");
    }





    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {

    }
}
