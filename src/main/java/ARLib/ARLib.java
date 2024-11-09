package ARLib;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.multiblocks.MultiblockRegistry;
import ARLib.network.PacketBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(ARLib.MODID)
public class ARLib
{
    public static final String MODID = "arlib";

    public ARLib(IEventBus modEventBus, ModContainer modContaine) {
        //NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::RegisterCapabilities);
        modEventBus.addListener(this::registerNetworkStuff);


        ARLibRegistry.register(modEventBus);
        MultiblockRegistry.register(modEventBus);


    }


    public void registerNetworkStuff(RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
       PacketBlockEntity. register(registrar);
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
