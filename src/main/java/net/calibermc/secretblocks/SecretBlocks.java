package net.calibermc.secretblocks;

import io.netty.buffer.Unpooled;
import net.calibermc.secretblocks.blocks.SecretBlock;
import net.calibermc.secretblocks.blocks.entity.SecretBlockEntity;
import net.calibermc.secretblocks.registry.ModBlocks;
import net.calibermc.secretblocks.registry.ModEntities;
import net.calibermc.secretblocks.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collection;

@SuppressWarnings("resource")
public class SecretBlocks implements ModInitializer {

	public static final String MOD_ID = "secretblocks";
	public static Identifier id(String id) { return new Identifier(MOD_ID, id); }

	public static final ItemGroup SECRET_BLOCKS_GROUP = Registry.register(
			Registries.ITEM_GROUP,
			id("secret_blocks"),
			FabricItemGroup.builder()
				.displayName(Text.translatable("itemGroup.secretblocks.secret_blocks"))
				.icon(() -> new ItemStack(ModItems.CAMOUFLAGE_PASTE))
				.entries((context, entries) -> {
					entries.add(ModBlocks.SOLID_BLOCK);
					entries.add(ModBlocks.STAIR_BLOCK);
					entries.add(ModBlocks.SLAB_BLOCK);
					entries.add(ModBlocks.GHOST_BLOCK);
					entries.add(ModBlocks.DOOR_BLOCK);
					entries.add(ModBlocks.IRON_DOOR_BLOCK);
					entries.add(ModBlocks.TRAPDOOR_BLOCK);
					entries.add(ModBlocks.IRON_TRAPDOOR_BLOCK);
					entries.add(ModBlocks.WOODEN_BUTTON);
					entries.add(ModBlocks.STONE_BUTTON);
					entries.add(ModBlocks.SECRET_LEVER);
					entries.add(ModBlocks.SECRET_REDSTONE);
					entries.add(ModBlocks.SECRET_CHEST);
					entries.add(ModItems.SECRET_GOGGLES);
					entries.add(ModItems.SWITCH_PROBE);
					entries.add(ModItems.SWITCH_PROBE_ROTATION_MODE);
					entries.add(ModItems.CAMOUFLAGE_PASTE);
				})
				.build()
	);

	@Override
	public void onInitialize() {

		ModBlocks.registerBlocks();
		ModItems.registerItems();
		ModEntities.registerAllEntities();

		ServerPlayNetworking.registerGlobalReceiver(SecretBlocks.id("hit_setter"), (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			BlockHitResult hit = buf.readBlockHitResult();
			boolean glass = buf.readBoolean();
			World world = player.getWorld();
			Direction facing = player.getHorizontalFacing().getOpposite();
			server.execute(() -> {

				if (world.getBlockEntity(pos) instanceof SecretBlockEntity) {

					SecretBlockEntity blockEntity = (SecretBlockEntity) world.getBlockEntity(pos);

					if (hit.getType() != HitResult.Type.MISS) {

						BlockPos blockPos = hit.getBlockPos();

					// In 1.20.1 the client crosshair target can sometimes resolve to the
					// newly placed secret block itself during onPlaced(). When that happens
					// we need to step back to the block face the player actually clicked,
					// otherwise we just read our own fresh BlockEntity and everything
					// falls back to stone.
					if (blockPos.equals(pos)) {
						blockPos = blockPos.offset(hit.getSide().getOpposite());
					}
						BlockState blockState = world.getBlockState(blockPos);
						Block block = blockState.getBlock();

						if (block instanceof SecretBlock) {
							if (world.getBlockEntity(blockPos) instanceof SecretBlockEntity) {
								SecretBlockEntity blockEntityAdjacent = (SecretBlockEntity) world.getBlockEntity(blockPos);
								for (Direction dir : Direction.values()) {
									if (glass) {
										SecretBlocks.updateSide(facing != dir ? blockEntityAdjacent.getState(hit.getSide()) : Blocks.GLASS.getDefaultState(), dir, pos, blockEntity);
									} else {
										SecretBlocks.updateSide(blockEntityAdjacent.getState(hit.getSide()), dir, pos, blockEntity);
									}
								}
							} else {
								for (Direction dir : Direction.values()) {
									if (glass) {
										SecretBlocks.updateSide(facing != dir ? Blocks.STONE.getDefaultState() : Blocks.GLASS.getDefaultState(), dir, pos, blockEntity);
									} else {
										SecretBlocks.updateSide(Blocks.STONE.getDefaultState(), dir, pos, blockEntity);
									}
								}
							}
						} else if (block != Blocks.AIR && blockState.isFullCube(world, blockPos)) {
							for (Direction dir : Direction.values()) {
								if (glass) {
									SecretBlocks.updateSide(facing != dir ? blockState : Blocks.GLASS.getDefaultState(), dir, pos, blockEntity);
								} else {
									SecretBlocks.updateSide(blockState, dir, pos, blockEntity);
								}
							}
						} else {
							for (Direction dir : Direction.values()) {
								if (glass) {
									SecretBlocks.updateSide(facing != dir ? Blocks.STONE.getDefaultState() : Blocks.GLASS.getDefaultState(), dir, pos, blockEntity);
								} else {
									SecretBlocks.updateSide(Blocks.STONE.getDefaultState(), dir, pos, blockEntity);
								}
							}
						}
					}

					blockEntity.refresh();
				}
			});
		});
	}


	public static void applyPlacementFallback(World world, BlockPos pos) {
		if (world.isClient) {
			return;
		}
		if (!(world.getBlockEntity(pos) instanceof SecretBlockEntity entity)) {
			return;
		}

		BlockState state = findBestCamouflageState(world, pos);
		entity.setState(state);
		entity.refresh();
	}

	private static BlockState findBestCamouflageState(World world, BlockPos pos) {
		for (Direction dir : Direction.values()) {
			BlockPos adjacentPos = pos.offset(dir);
			BlockState adjacentState = world.getBlockState(adjacentPos);

			if (adjacentState.getBlock() instanceof SecretBlock) {
				if (world.getBlockEntity(adjacentPos) instanceof SecretBlockEntity adjacentEntity) {
					BlockState copiedState = adjacentEntity.getState(dir.getOpposite());
					if (copiedState != null && copiedState.getBlock() != Blocks.AIR) {
						return copiedState;
					}
				}
				continue;
			}

			if (adjacentState.getBlock() != Blocks.AIR && adjacentState.isFullCube(world, adjacentPos)) {
				return adjacentState;
			}
		}

		return Blocks.STONE.getDefaultState();
	}

	public static void updateSide(BlockState state, Direction dir, BlockPos pos, SecretBlockEntity entity) {
		if (!entity.getWorld().isClient) {
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			NbtCompound tag = new NbtCompound();
			tag.put("state", NbtHelper.fromBlockState(state));
			passedData.writeNbt(tag);
			passedData.writeEnumConstant(dir);
			passedData.writeBlockPos(pos);
			Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.world((ServerWorld) entity.getWorld());
			watchingPlayers.forEach(player -> ServerPlayNetworking.send(player, SecretBlocks.id("update_side"), passedData));
		}
		entity.setState(dir, state);
	}

	public static void updateDirection(Direction faceDir, Direction dir, BlockPos pos, SecretBlockEntity entity) {
		if (!entity.getWorld().isClient) {
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeEnumConstant(faceDir);
			passedData.writeEnumConstant(dir);
			passedData.writeBlockPos(pos);
			Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.world((ServerWorld) entity.getWorld());
			watchingPlayers.forEach(player -> ServerPlayNetworking.send(player, SecretBlocks.id("update_direction"), passedData));
		}
		entity.setDirection(dir, faceDir);
	}

	public static void updateRotation(int rotation, Direction dir, BlockPos pos, SecretBlockEntity entity) {
		if (!entity.getWorld().isClient) {
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeInt(rotation);
			passedData.writeEnumConstant(dir);
			passedData.writeBlockPos(pos);
			Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.world((ServerWorld) entity.getWorld());
			watchingPlayers.forEach(player -> ServerPlayNetworking.send(player, SecretBlocks.id("update_rotation"), passedData));
		}
		entity.setRotation(dir, rotation);
	}
}
