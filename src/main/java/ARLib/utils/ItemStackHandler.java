package ARLib.utils;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemStackHandler extends net.neoforged.neoforge.items.ItemStackHandler {

    BlockEntity e;
    public ItemStackHandler(int size,BlockEntity parentBlockEntity) {
        super(size);
        this.e = parentBlockEntity;
    }
    @Override
    protected void onContentsChanged(int slot){
        e.setChanged();
    }
}
