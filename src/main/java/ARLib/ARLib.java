package ARLib;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.multiblocks.MultiblockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ARLib.MODID)
public class ARLib
{
    public static final String MODID = "arlib";

    public ARLib(IEventBus modEventBus, ModContainer modContaine) {
        //NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
    modEventBus.addListener(this::RegisterCapabilities);

        ARLibRegistry.register(modEventBus);
        MultiblockRegistry.register(modEventBus);


    }

    private void RegisterCapabilities(RegisterCapabilitiesEvent e){
        e.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ARLibRegistry.ENTITY_ENERGY_INPUT_BLOCK.get(),
                (EntityEnergyInputBlock, side) -> ((EntityEnergyInputBlock) EntityEnergyInputBlock)
        );
    }
    private void addCreative(BuildCreativeModeTabContentsEvent e){
        ARLibRegistry.addCreative(e);
        MultiblockRegistry.addCreative(e);
    }
    private void commonSetup(FMLCommonSetupEvent event)
    {
        
    }
}
