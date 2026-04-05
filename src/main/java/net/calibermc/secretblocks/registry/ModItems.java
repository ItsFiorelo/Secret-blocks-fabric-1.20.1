package net.calibermc.secretblocks.registry;

import net.calibermc.secretblocks.SecretBlocks;
import net.calibermc.secretblocks.items.SwitchProbe;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item SECRET_GOGGLES = registerItem("secret_goggles",
            new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item SWITCH_PROBE = registerItem("switch_probe",
            new SwitchProbe(new Item.Settings().rarity(Rarity.RARE).maxCount(1)));

    public static final Item SWITCH_PROBE_ROTATION_MODE = registerItem("switch_probe_rotation_mode",
            new SwitchProbe(new Item.Settings().rarity(Rarity.RARE).maxCount(1)));

    public static final Item CAMOUFLAGE_PASTE = registerItem("camouflage_paste",
            new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(SecretBlocks.MOD_ID, name), item);
    }

    public static void registerItems() {
        System.out.println("Registering Items for " + SecretBlocks.MOD_ID);
    }
}
