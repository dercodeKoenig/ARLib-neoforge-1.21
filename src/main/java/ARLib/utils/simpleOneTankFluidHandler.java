package ARLib.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.awt.*;

public class simpleOneTankFluidHandler implements IFluidHandler {

    FluidStack myFluid;
    int maxCapacity;

    public simpleOneTankFluidHandler(int capacity) {
        this.maxCapacity = capacity;
        this.myFluid = FluidStack.EMPTY;
    }

    public Tag save(HolderLookup.Provider registries) {
        CompoundTag t = new CompoundTag();
        if (myFluid.isEmpty()){
            t.putBoolean("hasFluid",false);
        }else{
            t.putBoolean("hasFluid",true);
            t.put("fluid", myFluid.save(registries));
        }
        return  t;
    }
    public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.getBoolean("hasFluid")) {
            myFluid = FluidStack.parse(registries, tag.getCompound("fluid")).get();
        }
    }

    public int getTancCapacity(){
        return this.getTankCapacity(0);
    }
    public FluidStack getFluidInTank(){
        return this.getFluidInTank(0);
    }
    public int getTankCapacity() {
        return getTankCapacity(0);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return myFluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return maxCapacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (myFluid.getFluid().isSame(resource.getFluid())) {
            int toFill = Math.min(maxCapacity - myFluid.getAmount(), resource.getAmount());
            if (action.execute())
                myFluid.grow(toFill);
            return toFill;
        }else if (myFluid.isEmpty()){
            int toFill = Math.min(maxCapacity, resource.getAmount());
            if (action.execute())
                myFluid = resource.copyWithAmount(toFill);
            return toFill;
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (myFluid.getFluid().isSame(resource.getFluid())) {
            int toDrain = Math.min(resource.getAmount(), myFluid.getAmount());
            FluidStack ret = myFluid.copyWithAmount(toDrain);
            if (action.execute())
                myFluid.shrink(toDrain);
            return ret;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int toDrain = Math.min(maxDrain, myFluid.getAmount());
        FluidStack ret = myFluid.copyWithAmount(toDrain);
        if (action.execute())
            myFluid.shrink(toDrain);
        return ret;

    }
}
