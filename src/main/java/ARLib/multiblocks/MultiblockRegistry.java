package ARLib.multiblocks;

import ARLib.ARLib;
import ARLib.multiblocks.lathe.BlockLathe;
import ARLib.multiblocks.lathe.EntityLathe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MultiblockRegistry {
    public static final net.neoforged.neoforge.registries.DeferredRegister<Block> BLOCKS = net.neoforged.neoforge.registries.DeferredRegister.create(BuiltInRegistries.BLOCK, ARLib.MODID);
    public static final net.neoforged.neoforge.registries.DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = net.neoforged.neoforge.registries.DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ARLib.MODID);
    public static final net.neoforged.neoforge.registries.DeferredRegister<Item> ITEMS = net.neoforged.neoforge.registries.DeferredRegister.create(BuiltInRegistries.ITEM, ARLib.MODID);

    public static void registerBlockItem(String name, DeferredHolder<Block,Block> b){
        ITEMS.register(name,() -> new BlockItem(b.get(), new Item.Properties()));
    }

    // lathe
    public static final DeferredHolder<Block, Block> BLOCK_LATHE = BLOCKS.register(
            "block_lathe",
            () -> new BlockLathe(BlockBehaviour.Properties.of().strength(5.0F))
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ENTITY_LATHE = BLOCK_ENTITIES.register(
            "entity_lathe",
            () -> BlockEntityType.Builder.of(EntityLathe::new, BLOCK_LATHE.get()).build(null)
    );


    public static void register(IEventBus modBus) {
        registerBlockItem("block_lathe", BLOCK_LATHE);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent e){
        if (e.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){
            e.accept(BLOCK_LATHE.get());
        }
    }
}
