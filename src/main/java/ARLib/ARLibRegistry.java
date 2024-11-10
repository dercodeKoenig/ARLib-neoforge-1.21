package ARLib;


import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.blockentities.EntityItemInputBlock;
import ARLib.blocks.BlockEnergyInputBlock;
import ARLib.blocks.BlockItemInputBlock;
import ARLib.multiblocks.lathe.BlockLathe;
import ARLib.multiblocks.lathe.EntityLathe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

//@Mod.EventBusSubscriber(modid = ARLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ARLibRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ARLib.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ARLib.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ARLib.MODID);

    public static void registerBlockItem(String name, Supplier<Block> b){
        ITEMS.register(name,() -> new BlockItem(b.get(), new Item.Properties()));
    }


    public static final Supplier<Block> BLOCK_ENERGY_INPUT_BLOCK = BLOCKS.register(
            "block_energy_input_block",
            () -> new BlockEnergyInputBlock(BlockBehaviour.Properties.of())
    );
    public static final Supplier<BlockEntityType<?>> ENTITY_ENERGY_INPUT_BLOCK  = BLOCK_ENTITIES.register(
            "entity_energy_input_block",
            () -> BlockEntityType.Builder.of(EntityEnergyInputBlock::new, BLOCK_ENERGY_INPUT_BLOCK.get()).build(null)
    );


    public static final Supplier<Block> BLOCK_ITEM_INPUT_BLOCK = BLOCKS.register(
            "block_item_input_block",
            () -> new BlockItemInputBlock(BlockBehaviour.Properties.of())
    );
    public static final Supplier<BlockEntityType<?>> ENTITY_ITEM_INPUT_BLOCK  = BLOCK_ENTITIES.register(
            "entity_item_input_block",
            () -> BlockEntityType.Builder.of(EntityItemInputBlock::new, BLOCK_ITEM_INPUT_BLOCK.get()).build(null)
    );




    public static void register(IEventBus modBus) {
        registerBlockItem("block_energy_input_block",BLOCK_ENERGY_INPUT_BLOCK);
        registerBlockItem("block_item_input_block",BLOCK_ITEM_INPUT_BLOCK);

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent e){
        if (e.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){
            e.accept(BLOCK_ENERGY_INPUT_BLOCK.get());
            e.accept(BLOCK_ITEM_INPUT_BLOCK.get());
        }
    }
}
