package ARLib.multiblockCore;

import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

public class BlockEntityMultiblockMaster extends BlockEntity {


    protected static HashMap<Character, List<Block>> charMapping = new HashMap<>();
    private boolean isMultiblockFormed = false;


    public BlockEntityMultiblockMaster(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        setupCharmappings();
    }

    public void setupCharmappings() {
        List<Block> I = new ArrayList<>();
        I.add(BLOCK_ITEM_INPUT_BLOCK.get());
        setMapping('I', I);

        List<Block> O = new ArrayList<>();
        O.add(BLOCK_ITEM_OUTPUT_BLOCK.get());
        setMapping('O', O);

        List<Block> P = new ArrayList<>();
        P.add(BLOCK_ENERGY_INPUT_BLOCK.get());
        setMapping('P', P);
    }

    public static void setMapping(char character, List<Block> listToAdd) {
        charMapping.put(character, listToAdd);
    }


    public void setMultiblockFormed(boolean formed) {
        this.isMultiblockFormed = formed;
        BlockState masterState = level.getBlockState(getBlockPos());
        if (masterState.hasProperty(STATE_MULTIBLOCK_FORMED)) {
            // if the master was removed, this can not be set so it goes in an if()
            level.setBlock(getBlockPos(), masterState.setValue(STATE_MULTIBLOCK_FORMED, formed), 3);
        }
    }

    public boolean isMultiblockFormed() {
        return isMultiblockFormed;
    }

    public Object[][][] getStructure() {
        return null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(!level.isClientSide) {
            scanStructure();
        }
    }

    protected Vec3i getControllerOffset(Object[][][] structure) {
        for (int y = 0; y < structure.length; y++) {
            for (int z = 0; z < structure[0].length; z++) {
                for (int x = 0; x < structure[0][0].length; x++) {
                    if (structure[y][z][x] instanceof Character && (Character) structure[y][z][x] == 'c')
                        return new Vec3i(x, y, z);
                }
            }
        }
        return null;
    }

    void un_replace_blocks() {
        Object[][][] structure = getStructure();

        Direction front = getFront();
        if (front == null) return;

        Vec3i offset = getControllerOffset(structure);

        setMultiblockFormed(false);

        for (int y = 0; y < structure.length; y++) {
            for (int z = 0; z < structure[y].length; z++) {
                for (int x = 0; x < structure[y][z].length; x++) {
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

                    BlockEntity tile = level.getBlockEntity(globalPos);
                    if (tile instanceof EntityMultiblockPlaceholder t) {
                        if (t.replacedState != null) {
                            level.setBlock(globalPos, t.replacedState, 3);
                        } else {
                            level.setBlock(globalPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }

                    BlockState blockState = level.getBlockState(globalPos);
                    Block newBlock = blockState.getBlock();
                    if (newBlock instanceof BlockMultiblockPart bmp) {
                        bmp.setMaster(globalPos,null);
                        level.setBlock(globalPos, blockState.setValue(STATE_MULTIBLOCK_FORMED, false), 3);
                    }
                }
            }
        }
    }

    void replace_blocks() {
        Object[][][] structure = getStructure();

        Direction front = getFront();
        if (front == null) return;

        Vec3i offset = getControllerOffset(structure);

        setMultiblockFormed(true);

        for (int y = 0; y < structure.length; y++) {
            for (int z = 0; z < structure[y].length; z++) {
                for (int x = 0; x < structure[y][z].length; x++) {
                    //Ignore nulls / air blocks
                    if (structure[y][z][x] == null || structure[y][z][x].equals(Blocks.AIR))
                        continue;

                    int globalX = getBlockPos().getX() + (x - offset.getX()) * front.getStepZ() - (z - offset.getZ()) * front.getStepX();
                    int globalY = getBlockPos().getY() - y + offset.getY();
                    int globalZ = getBlockPos().getZ() - (x - offset.getX()) * front.getStepX() - (z - offset.getZ()) * front.getStepZ();
                    BlockPos globalPos = new BlockPos(globalX, globalY, globalZ);

                    // this should load the chunk if it is not loaded
                    ChunkAccess chunk = level.getChunk(globalPos);
                    level.getChunk(chunk.getPos().x, chunk.getPos().z, ChunkStatus.FULL, true);


                    // replace blocks that are not multiblock parts with placeholders to make them not render
                    BlockState blockState = level.getBlockState(globalPos);
                    if (!(blockState.getBlock() instanceof BlockMultiblockPart) &&
                            !(blockState.getBlock() instanceof BlockMultiblockMaster)
                    ) {
                        BlockMultiblockPlaceholder p = (BlockMultiblockPlaceholder) BLOCK_PLACEHOLDER.get();
                        BlockState newState = p.defaultBlockState();
                        level.setBlock(globalPos, newState, 3);
                        EntityMultiblockPlaceholder tile = (EntityMultiblockPlaceholder)level.getBlockEntity(globalPos);
                        tile.replacedState =blockState;
                        blockState = newState;
                    }

                    if (blockState.getBlock() instanceof BlockMultiblockPart t) {
                        t.setMaster(globalPos, getBlockPos());
                    }
                    level.setBlock(globalPos, blockState.setValue(STATE_MULTIBLOCK_FORMED, true), 3);
                }
            }
        }
    }

    public boolean scanStructure() {
        boolean canComplete = canCompleteStructure();

        if (!canComplete) {
            un_replace_blocks();
        } else {
            replace_blocks();
        }
        return canComplete;
    }

    Direction directionFallbackWhenAfterDestroy;

    Direction getFront() {
        BlockState state = level.getBlockState(getBlockPos());
        Direction front;
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            directionFallbackWhenAfterDestroy = front;
            return front;
        } else {
            if (directionFallbackWhenAfterDestroy != null) {
                front = directionFallbackWhenAfterDestroy;
                return front;
            } else {
                return null;
            }
        }
    }

    public boolean canCompleteStructure() {
        Object[][][] structure = getStructure();

        Direction front = getFront();
        if (front == null) return false;


        Vec3i offset = getControllerOffset(structure);

        for (int y = 0; y < structure.length; y++) {
            for (int z = 0; z < structure[y].length; z++) {
                for (int x = 0; x < structure[y][z].length; x++) {
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
                    Block block = blockState.getBlock();
                    BlockEntity tile = level.getBlockEntity(globalPos);

                    if (tile instanceof EntityMultiblockPlaceholder t && t.replacedState != null) {
                        block = t.replacedState.getBlock();
                    }
                    if (!getAllowableBlocks(structure[y][z][x]).contains(block)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<Block> getAllowableWildCardBlocks() {
        List<Block> list = new ArrayList<>();
        return list;
    }

    public List<Block> getAllowableBlocks(Object input) {
        if (input instanceof Character && (Character) input == '*') {
            return getAllowableWildCardBlocks();
        } else if (input instanceof Character && charMapping.containsKey(input)) {
            return charMapping.get(input);
        } else if (input instanceof String) { //OreDict entry
            // [rage quit #23]
        } else if (input instanceof Block) {
            List<Block> list = new ArrayList<>();
            list.add((Block) input);
            return list;
        } else if (input instanceof List) {
            return (List<Block>) input;
        }
        return new ArrayList<>();
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }


    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {

    }
}
