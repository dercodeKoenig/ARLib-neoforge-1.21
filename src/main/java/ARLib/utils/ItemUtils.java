package ARLib.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class ItemUtils {
    public static boolean matches(String identifier, ItemStack stack) {
        //check for tag first
        ResourceLocation tagLocation = ResourceLocation.tryParse(identifier.substring(1));
        if (tagLocation != null) {
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLocation);
            if (stack.is(tagKey))
                return true;
        }

        // It's a direct item ID
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(identifier));
        return stack.is(item);
    }


    public static boolean matches(String identifier, FluidStack stack) {
        //check for tag first
        ResourceLocation tagLocation = ResourceLocation.tryParse(identifier.substring(1));
        if (tagLocation != null) {
            TagKey<Fluid> tagKey = TagKey.create(Registries.FLUID, tagLocation);
            if (stack.is(tagKey))
                return true;
        }

        // It's a direct fluid ID
        return stack.is(BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(identifier)));
    }
}
