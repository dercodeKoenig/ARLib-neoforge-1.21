package ARLib.multiblockCore;

import ARLib.blockentities.*;
import ARLib.network.INetworkTagReceiver;
import ARLib.network.PacketBlockEntity;
import ARLib.utils.InventoryUtils;
import ARLib.utils.ItemFluidStacks;
import ARLib.utils.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ARLib.ARLibRegistry.*;
import static ARLib.multiblockCore.BlockMultiblockMaster.STATE_MULTIBLOCK_FORMED;

public abstract class EntityMultiblockMaster extends BlockEntity implements INetworkTagReceiver {

    abstract public  Object[][][] getStructure();
    abstract public HashMap<Character, List<Block>> getCharMapping();

    // set this to true to make the master block gui open for a click on any machine part block
    // must be implemented on the machine part block
    public boolean alwaysOpenMasterGui = false;

    private boolean isMultiblockFormed = false;
    private Direction facing = Direction.EAST;

    protected List<EntityEnergyOutputBlock> energyOutTiles = new ArrayList<>();
    protected List<EntityEnergyInputBlock> energyInTiles = new ArrayList<>();
    protected List<EntityItemInputBlock> itemInTiles = new ArrayList<>();
    protected List<EntityItemOutputBlock> itemOutTiles = new ArrayList<>();
    protected List<EntityFluidInputBlock> fluidInTiles = new ArrayList<>();
    protected List<EntityFluidOutputBlock> fluidOutTiles = new ArrayList<>();


    public EntityMultiblockMaster(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public int getTotalEnergyStored() {
        int totalEnergy = 0;
        for (IEnergyStorage i : energyInTiles) {
            totalEnergy += i.getEnergyStored();
        }
        return totalEnergy;
    }

    public void consumeEnergy(int energyToConsume) {
        int consumed = 0;
        for (IEnergyStorage i : energyInTiles) {
            consumed += i.extractEnergy(energyToConsume - consumed, false);
            if (consumed == energyToConsume) {
                return;
            }
        }
    }

    public ItemFluidStacks consumeInput(List<MachineRecipe.recipePart> inputs, boolean simulate) {
        ItemFluidStacks consumedElements = new ItemFluidStacks();
        for (MachineRecipe.recipePart input : inputs) {
            String identifier = input.id;
            int totalToConsume = input.actual_num;
            if (totalToConsume > 0) {
                ItemFluidStacks ret = InventoryUtils.consumeElements(this.fluidInTiles, this.itemInTiles, identifier, totalToConsume, simulate);
                consumedElements.fluidStacks.addAll(ret.fluidStacks);
                consumedElements.itemStacks.addAll(ret.itemStacks);
            }
        }
        return consumedElements;
    }


    public void produceOutput(List<MachineRecipe.recipePart> outputs) {
        for (MachineRecipe.recipePart output : outputs) {
            String identifier = output.id;
            int totalToProduce = output.actual_num;
            if (totalToProduce > 0) {
                InventoryUtils.createElements(this.fluidOutTiles, this.itemOutTiles, identifier, totalToProduce);
            }
        }
    }


    // both using the max possible inputs/outputs for p >= 1
    public boolean hasinputs(List<MachineRecipe.recipePart> inputs) {
        return InventoryUtils.hasInputs(this.itemInTiles, this.fluidInTiles, inputs);
    }
    public boolean canFitOutputs(List<MachineRecipe.recipePart> outputs) {
        return InventoryUtils.canFitElements(this.itemOutTiles, this.fluidOutTiles, outputs);
    }


    public void setMultiblockFormed(boolean formed) {
        this.isMultiblockFormed = formed;
        setChanged();
        BlockState masterState = level.getBlockState(getBlockPos());
        if (masterState.hasProperty(STATE_MULTIBLOCK_FORMED)) {
            // if the master was removed, this can not be set so it goes in an if()
            level.setBlock(getBlockPos(), masterState.setValue(STATE_MULTIBLOCK_FORMED, formed), 3);
        }
        CompoundTag info = new CompoundTag();
        info.putBoolean("isMultiblockFormed", isMultiblockFormed);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(getBlockPos()), PacketBlockEntity.getBlockEntityPacket(this, info));
    }

    public boolean isMultiblockFormed() {
        return isMultiblockFormed;
    }

    public Direction getFacing() {
        return this.facing;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            scanStructure();
        }
        if (level.isClientSide) {
            // set isMultiblockFormed from blockstate - after this it will be updated in network packet when it changes
            isMultiblockFormed = level.getBlockState(getBlockPos()).getValue(STATE_MULTIBLOCK_FORMED);
            if (isMultiblockFormed)
                onStructureComplete();
        }
        this.facing = level.getBlockState(getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public Vec3i getControllerOffset(Object[][][] structure) {
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
                        bmp.setMaster(globalPos, null);
                        level.setBlock(globalPos, blockState.setValue(STATE_MULTIBLOCK_FORMED, false), 3);
                    }
                }
            }
        }
    }

    void addStructureTiles(BlockEntity tile) {
        // make sure order is correct, out tiles extend in tiles!
        if (tile instanceof EntityEnergyOutputBlock t)
            energyOutTiles.add(t);
        else if (tile instanceof EntityEnergyInputBlock t)
            energyInTiles.add(t);
        else if (tile instanceof EntityItemOutputBlock t)
            itemOutTiles.add(t);
        else if (tile instanceof EntityItemInputBlock t)
            itemInTiles.add(t);
        else if (tile instanceof EntityFluidOutputBlock t)
            fluidOutTiles.add(t);
        else if (tile instanceof EntityFluidInputBlock t)
            fluidInTiles.add(t);
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
                        EntityMultiblockPlaceholder tile = (EntityMultiblockPlaceholder) level.getBlockEntity(globalPos);
                        tile.replacedState = blockState;
                        blockState = level.getBlockState(globalPos);
                    }

                    // at this point the block is a multiBlockPart or multiBlockMaster
                    level.setBlock(globalPos, blockState.setValue(STATE_MULTIBLOCK_FORMED, true), 3);

                    blockState = level.getBlockState(globalPos);
                    if (blockState.getBlock() instanceof BlockMultiblockPart t) {
                        t.setMaster(globalPos, getBlockPos());
                    }

                    // scan blockentity if any to add to in/out tiles
                    BlockEntity tile = level.getBlockEntity(globalPos);
                    addStructureTiles(tile);
                }
            }
        }
    }

    public void onStructureComplete() {

    }

    // when structure is assembled it sets all blockstates new because it changes the STATE_MULTIBLOCK_FORMED
    // this triggers re-scan and messes up tiles so block scanning while scanning
boolean isScanning = false;
    public void scanStructure() {
        System.out.println("try scan");
        if (level.isClientSide) return;
        if(isScanning)return;
        isScanning = true;

        energyInTiles.clear();
        energyOutTiles.clear();
        itemInTiles.clear();
        itemOutTiles.clear();
        fluidInTiles.clear();
        fluidOutTiles.clear();

        boolean canComplete = canCompleteStructure();
        System.out.println(canComplete);
        if (!canComplete) {
            un_replace_blocks();
        } else {
            replace_blocks();
            onStructureComplete();
        }
        isScanning = false;
    }

    Direction directionFallbackWhenAfterDestroy;

    public Direction getFront() {
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


    public List<Block> getAllowableBlocks(Object input) {
        if (input instanceof Character && getCharMapping().containsKey(input)) {
            return getCharMapping().get(input);
        } else if (input instanceof String) { //OreDict entry

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

    @Override
    public void readServer(CompoundTag tag) {

    }

    @Override
    public void readClient(CompoundTag tag) {
        if (tag.contains("isMultiblockFormed")) {
            this.isMultiblockFormed = tag.getBoolean("isMultiblockFormed");
            if (this.isMultiblockFormed)
                onStructureComplete();
        }
    }
}
