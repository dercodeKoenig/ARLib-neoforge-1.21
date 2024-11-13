package ARLib.blockentities;

import ARLib.gui.GuiHandlerBlockEntity;
import ARLib.gui.IGuiHandler;
import ARLib.gui.modules.guiModuleFluidTankDisplay;
import ARLib.network.INetworkTagReceiver;
import ARLib.utils.simpleOneTankFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import static ARLib.ARLibRegistry.ENTITY_FLUID_INPUT_BLOCK;

public class EntityFluidInputBlock extends BlockEntity implements IFluidHandler, INetworkTagReceiver {

    simpleOneTankFluidHandler myTank;
    IGuiHandler guiHandler;
    guiModuleFluidTankDisplay fluidDisplay;
    public EntityFluidInputBlock(BlockPos pos, BlockState blockState) {
        this(ENTITY_FLUID_INPUT_BLOCK.get(), pos, blockState);
    }

    public EntityFluidInputBlock(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        myTank = new simpleOneTankFluidHandler(4000);
        guiHandler = new GuiHandlerBlockEntity(this);
        fluidDisplay = new guiModuleFluidTankDisplay(0,this,0,guiHandler,10,10);
        guiHandler.registerModule(fluidDisplay);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        myTank.loadFromTag(tag.getCompound("tank"), registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("tank", myTank.save(registries));
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return myTank.getFluidInTank();
    }

    @Override
    public int getTankCapacity(int tank) {
        return myTank.getTankCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = myTank.fill(resource, action);
        if (filled > 0)
            this.setChanged();
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained = myTank.drain(resource, action);
        if (drained != FluidStack.EMPTY)
            this.setChanged();
        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = myTank.drain(maxDrain, action);
        if (drained != FluidStack.EMPTY)
            this.setChanged();
        return drained;
    }

    @Override
    public void readServer(CompoundTag tag) {
        guiHandler.readServer(tag);
    }

    @Override
    public void readClient(CompoundTag tag) {
        guiHandler.readClient(tag);
    }

    public void openGui() {
        guiHandler.openGui(100, 74);
    }

    public static <x extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, x t) {
        if (!level.isClientSide)
            IGuiHandler.serverTick(((EntityFluidInputBlock) t).guiHandler);
    }
}
