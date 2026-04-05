package net.calibermc.secretblocks.registry;

import net.calibermc.secretblocks.SecretBlocks;
import net.calibermc.secretblocks.blocks.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block SOLID_BLOCK = registerBlock("solid_block",
            new SolidBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL)));

    public static final Block STAIR_BLOCK = registerBlock("stair_block",
            new StairBlock(SOLID_BLOCK.getDefaultState(), FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block SLAB_BLOCK = registerBlock("slab_block",
            new SlabBlock(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block GHOST_BLOCK = registerBlock("ghost_block",
            new GhostBlock(FabricBlockSettings.copy(SOLID_BLOCK).noCollision()));


    public static final Block DOOR_BLOCK = registerBlock("door_block",
            new DoorBlock(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block IRON_DOOR_BLOCK = registerBlock("iron_door_block",
            new DoorBlock(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block TRAPDOOR_BLOCK = registerBlock("trapdoor_block",
            new TrapdoorBlock(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block IRON_TRAPDOOR_BLOCK = registerBlock("iron_trapdoor_block",
            new TrapdoorBlock(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block WOODEN_BUTTON = registerBlock("wooden_button",
            new WoodButton(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block STONE_BUTTON = registerBlock("stone_button",
            new StoneButton(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block SECRET_LEVER = registerBlock("secret_lever",
            new SecretLever(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block SECRET_REDSTONE = registerBlock("secret_redstone",
            new SecretRedstone(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block SECRET_CHEST = registerBlock("secret_chest",
            new SecretChest(FabricBlockSettings.copy(SOLID_BLOCK)));

    public static final Block[] secretBlocksList = {
            SOLID_BLOCK, STAIR_BLOCK, SLAB_BLOCK, GHOST_BLOCK, DOOR_BLOCK,
            IRON_DOOR_BLOCK, TRAPDOOR_BLOCK, IRON_TRAPDOOR_BLOCK, WOODEN_BUTTON, STONE_BUTTON,
            SECRET_LEVER, SECRET_REDSTONE, SECRET_CHEST
    };

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(SecretBlocks.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(SecretBlocks.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(SecretBlocks.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerBlocks() {
        System.out.println("Registering Blocks for " + SecretBlocks.MOD_ID);
    }
}
