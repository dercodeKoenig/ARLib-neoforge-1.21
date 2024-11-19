package ARLib.holoProjector;

import ARLib.network.INetworkTagReceiver;
import ARLib.network.PacketBlockEntity;
import com.mojang.serialization.DataResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ARLib.ARLibRegistry.ENTITY_STRUCTURE_PREVIEW;


public class EntityStructurePreviewBlock extends BlockEntity implements INetworkTagReceiver {
    public EntityStructurePreviewBlock(BlockPos pos, BlockState blockState) {
        super(ENTITY_STRUCTURE_PREVIEW.get(), pos, blockState);
    }

    private List<Block> validBlocks = new ArrayList<>();

    public void setValidBlocks(List<Block> validBlocks) {
        for (Block i : validBlocks) {
            this.validBlocks.add(i);
        }
    }

    long last_sec = 0;
    int i = 0;

    public Block getBlockToRender() {
        long sec = System.currentTimeMillis() / 1000;
        if (sec != last_sec) {
            last_sec = sec;
            i += 1;
            if (i >= validBlocks.size()) {
                i = 0;
            }
        }
        //System.out.println(validBlocks.get(i));
        if (validBlocks.isEmpty()) {
            return Blocks.AIR;
        }

        return validBlocks.get(i);
    }

    @Override
    public void onLoad() {
        if(!level.isClientSide){
            ((ServerLevel)level).scheduleTick(getBlockPos(),getBlockState().getBlock(),24000);
        }
        if (level.isClientSide) {
            CompoundTag info = new CompoundTag();
            info.putUUID("client_onload", Minecraft.getInstance().player.getUUID());
            PacketDistributor.sendToServer(PacketBlockEntity.getBlockEntityPacket(this, info));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // Save the validBlocks list to NBT
        ListTag blockListTag = new ListTag();
        for (Block block : validBlocks) {
            DataResult<CompoundTag> encodedBlockState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, block.defaultBlockState()).map((nbtTag) -> (CompoundTag) nbtTag);
            CompoundTag bt = new CompoundTag();
            bt.put("block", encodedBlockState.getOrThrow());
            blockListTag.add(bt);
        }
        tag.put("ValidBlocks", blockListTag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        // Load the validBlocks list from NBT
        validBlocks.clear();
        if (tag.contains("ValidBlocks")) {
            ListTag blockListTag = tag.getList("ValidBlocks", Tag.TAG_COMPOUND);
            for (int i = 0; i < blockListTag.size(); i++) {
                CompoundTag blockStateNBT = blockListTag.getCompound(i).getCompound("block");
                DataResult<BlockState> decodedBlockState = BlockState.CODEC.parse(NbtOps.INSTANCE, blockStateNBT);
                Block b = decodedBlockState.getOrThrow().getBlock();
                validBlocks.add(b);
            }
        }
    }

    @Override
    public void readServer(CompoundTag compoundTag) {
        if (compoundTag.contains("client_onload")) {
            UUID p = compoundTag.getUUID("client_onload");
            CompoundTag response = new CompoundTag();
            ListTag blockListTag = new ListTag();
            for (Block block : validBlocks) {
                DataResult<CompoundTag> encodedBlockState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, block.defaultBlockState()).map((nbtTag) -> (CompoundTag) nbtTag);
                CompoundTag bt = new CompoundTag();
                bt.put("block", encodedBlockState.getOrThrow());
                blockListTag.add(bt);
            }
            response.put("ValidBlocks", blockListTag);
            PacketDistributor.sendToPlayer(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(p), PacketBlockEntity.getBlockEntityPacket(this, response));
        }
    }

    @Override
    public void readClient(CompoundTag compoundTag) {
        validBlocks.clear();
        if (compoundTag.contains("ValidBlocks")) {
            ListTag blockListTag = compoundTag.getList("ValidBlocks",Tag.TAG_COMPOUND);
            for (int i = 0; i < blockListTag.size(); i++) {
                CompoundTag blockStateNBT = blockListTag.getCompound(i).getCompound("block");
                DataResult<BlockState> decodedBlockState = BlockState.CODEC.parse(NbtOps.INSTANCE, blockStateNBT);
                Block b = decodedBlockState.getOrThrow().getBlock();
                validBlocks.add(b);
            }
        }
    }


}