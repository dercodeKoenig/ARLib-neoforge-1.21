package ARLib;

import ARLib.blockentities.EntityEnergyInputBlock;
import ARLib.blockentities.EntityItemInputBlock;
import ARLib.multiblocks.MultiblockRegistry;
import ARLib.multiblocks.lathe.EntityLathe;
import ARLib.network.PacketBlockEntity;
import ARLib.utils.MachineRecipe;
import ARLib.utils.RecipeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mod(ARLib.MODID)
public class ARLib
{
    public static final String MODID = "arlib";

    public ARLib(IEventBus modEventBus, ModContainer modContaine) {
        //NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::RegisterCapabilities);
        modEventBus.addListener(this::registerNetworkStuff);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::registerEntityRenderers);

        ARLibRegistry.register(modEventBus);
        MultiblockRegistry.register(modEventBus);



    }

    public  void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
MultiblockRegistry.registerRenderers(event);
    }

    public void registerNetworkStuff(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
       PacketBlockEntity. register(registrar);
    }

    private void RegisterCapabilities(RegisterCapabilitiesEvent e){
        ARLibRegistry.registerCapabilities(e);
    }
    private void addCreative(BuildCreativeModeTabContentsEvent e){
        ARLibRegistry.addCreative(e);
        MultiblockRegistry.addCreative(e);
    }
    private void loadComplete(FMLLoadCompleteEvent e){

        List<MachineRecipe> latheDefaultRecipes = new ArrayList<>();
        MachineRecipe r = new MachineRecipe();
        r.addInput("c:ingots/iron", 1);
        r.addOutput("immersiveengineering:stick_iron", 1);
        r.energyPerTick = 50;
        r.ticksRequired = 100;
        latheDefaultRecipes.add(r);

        Path configDir = Paths.get(Minecraft.getInstance().gameDirectory.toString(), "config", "arlib");
        String filename = "lathe.xml";
        List<MachineRecipe> recipesLathe =  RecipeLoader.loadRecipes(configDir,filename);
        if (recipesLathe.isEmpty()){
            RecipeLoader.createRecipeFile(configDir,filename,latheDefaultRecipes);
            recipesLathe = latheDefaultRecipes;
        }
        for (MachineRecipe i : recipesLathe) {
            EntityLathe.addRecipe(i);
        }
    }
    }
