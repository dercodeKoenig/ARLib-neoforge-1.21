package ARLib.multiblockCore;

import ARLib.blocks.BlockMultiblockPlaceholder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ARLib.ARLibRegistry.*;
import static ARLib.multiblockCore.BlockMultiblockMaster.STATE_MULTIBLOCK_FORMED;

public class BlockEntityMultiblockMaster extends BlockEntity{


    protected static HashMap<Character, List<Block>> charMapping = new HashMap<>();
    private boolean isMultiblockFormed = false;


    public BlockEntityMultiblockMaster(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        setupCharmappings();
    }
    public void setupCharmappings(){
        List<Block> i = new ArrayList<>();
        i.add(BLOCK_ITEM_INPUT_BLOCK.get());
        setMapping('i',i);

        List<Block> I = new ArrayList<>();
        I.add(BLOCK_ITEM_OUTPUT_BLOCK.get());
        setMapping('I',I);
    }
    public static void setMapping(char character, List<Block> listToAdd) {
        charMapping.put(character, listToAdd);
    }


    public void setMultiblockFormed(boolean formed) {
        this.isMultiblockFormed = formed;
        level.setBlock(this.getBlockPos(),level.getBlockState(getBlockPos()).setValue(STATE_MULTIBLOCK_FORMED,formed),3);
    }

    public boolean isMultiblockFormed() {
        return isMultiblockFormed;
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

    void replace_blocks(){
        Object[][][] structure = getStructure();

        BlockState state = level.getBlockState(getBlockPos());
        Direction front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Vec3i offset = getControllerOffset(structure);

        for(int y = 0; y < structure.length; y++) {
            for(int z = 0; z < structure[0].length; z++) {
                for(int x = 0; x< structure[0][0].length; x++) {
                    //Ignore nulls
                    if (structure[y][z][x] == null)
                        continue;

                    int globalX = getBlockPos().getX() + (x - offset.getX()) * front.getStepZ() - (z - offset.getZ()) * front.getStepX();
                    int globalY = getBlockPos().getY() - y + offset.getY();
                    int globalZ = getBlockPos().getZ() - (x - offset.getX()) * front.getStepX() - (z - offset.getZ()) * front.getStepZ();
                    BlockPos globalPos = new BlockPos(globalX, globalY, globalZ);

                    // this should load the chunk if it is not loaded
                    ChunkAccess chunk = level.getChunk(globalPos);
                    level.getChunk(chunk.getPos().x, chunk.getPos().z, ChunkStatus.FULL, true);

                    BlockState blockState = level.getBlockState(globalPos);
                    if (!(blockState.getBlock() instanceof BlockMultiblockPart) &&
                            !(blockState.getBlock() instanceof BlockMultiblockMaster)
                    ) {
                        BlockState newState = BLOCK_PLACEHOLDER.get().defaultBlockState();
                        level.setBlock(globalPos, newState, 3);
                        ((BlockMultiblockPart) newState.getBlock()).setMasterBlockPos(newState, getBlockPos());
                        ((BlockMultiblockPlaceholder) newState.getBlock()).setReplacedBlock(blockState);
                    }
                }}}
    }

    public boolean completeStructure() {
        //Chunk chunk = level.getChunk(x, z, ChunkStatus.FULL, false); to force the chunk to load

        Object[][][] structure = getStructure();

        BlockState state = level.getBlockState(getBlockPos());
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

                    // this should load the chunk if it is not loaded
                    ChunkAccess chunk = level.getChunk(globalPos);
                    level.getChunk(chunk.getPos().x,chunk.getPos().z,ChunkStatus.FULL,true);


                    BlockState blockState = level.getBlockState(globalPos);
                    Block block = blockState.getBlock();

                    if (block instanceof BlockMultiblockPart mbp && !mbp.masterBlockPos.equals(getBlockPos())){
                        return false;
                    }
                    else if (!getAllowableBlocks(structure[y][z][x]).contains(block)){
                        return false;
                    }

                }}}

        replace_blocks();
        setMultiblockFormed(true);
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




    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        completeStructure();
    }





    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {

    }
}
