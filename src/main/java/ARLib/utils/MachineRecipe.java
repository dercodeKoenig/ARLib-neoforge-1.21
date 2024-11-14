package ARLib.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineRecipe {
    public Map<String, Integer> inputs = new HashMap<>();
    public int energyPerTick = 0;
    public List<ItemStack> outputItems = new ArrayList<>();
    public List<FluidStack> outputFluids = new ArrayList<>();
    int ticks = 1;

    public void addInput(String input_id_or_tag, int num) {
        inputs.put(input_id_or_tag, num);
    }

    public void addOutput(String output_id, int num) {
        Item n = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(output_id));
        if (n != null && !n.equals(Items.AIR)) {
            ItemStack x = new ItemStack(n, num);
            System.out.println(x);
            outputItems.add(x);
            return;
        }
        Fluid m = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(output_id));
        if (m != null && m != Fluids.EMPTY) {
            FluidStack x = new FluidStack(m, num);
            System.out.println(x);
            outputFluids.add(x);
            return;
        }
    }
}
