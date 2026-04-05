package net.calibermc.secretblocks;

import io.netty.buffer.Unpooled;
import net.calibermc.secretblocks.blocks.entity.SecretBlockEntity;
import net.calibermc.secretblocks.registry.ModBlocks;
import net.calibermc.secretblocks.screen.ModScreenHandlers;
import net.calibermc.secretblocks.screen.SecretChestScreen;
import net.calibermc.secretblocks.render.SecretBlockModelResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.RegistryKeys;

@Environment(EnvType.CLIENT)
public class SecretBlocksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.SECRET_CHEST_SCREEN_HANDLER, SecretChestScreen::new);
		ModelLoadingPlugin.register(pluginContext -> pluginContext.resolveModel().register(new SecretBlockModelResolver()));
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.SOLID_BLOCK, ModBlocks.STAIR_BLOCK, ModBlocks.SLAB_BLOCK, ModBlocks.GHOST_BLOCK,
				ModBlocks.DOOR_BLOCK, ModBlocks.IRON_DOOR_BLOCK, ModBlocks.TRAPDOOR_BLOCK, ModBlocks.IRON_TRAPDOOR_BLOCK,
				ModBlocks.WOODEN_BUTTON, ModBlocks.STONE_BUTTON, ModBlocks.SECRET_LEVER, ModBlocks.SECRET_REDSTONE, ModBlocks.SECRET_CHEST);

		ClientPlayNetworking.registerGlobalReceiver(SecretBlocks.id("update_side"), (client, handler, buf, responseSender) -> {
			NbtCompound tag = buf.readNbt();
			Direction dir = buf.readEnumConstant(Direction.class);
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
				if (client.world == null) return;
				BlockState state = NbtHelper.toBlockState(client.world.getRegistryManager().getWrapperOrThrow(RegistryKeys.BLOCK), tag.getCompound("state"));
				SecretBlockEntity secretBlockEntity = ((SecretBlockEntity) client.world.getBlockEntity(pos));
				if (secretBlockEntity != null) {
					secretBlockEntity.setState(dir, state);
				}
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(SecretBlocks.id("update_direction"), (client, handler, buf, responseSender) -> {
			Direction faceDir = buf.readEnumConstant(Direction.class);
			Direction dir = buf.readEnumConstant(Direction.class);
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
				if (client.world == null) return;
				SecretBlockEntity secretBlockEntity = ((SecretBlockEntity) client.world.getBlockEntity(pos));
				if (secretBlockEntity != null) {
					secretBlockEntity.setDirection(dir, faceDir);
				}
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(SecretBlocks.id("update_rotation"), (client, handler, buf, responseSender) -> {
			int rotation = buf.readInt();
			Direction dir = buf.readEnumConstant(Direction.class);
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
				if (client.world == null) return;
				SecretBlockEntity secretBlockEntity = ((SecretBlockEntity) client.world.getBlockEntity(pos));
				if (secretBlockEntity != null) {
					secretBlockEntity.setRotation(dir, rotation);
				}
			});
		});
	}

	public static void sendHitSetter(BlockPos pos, BlockHitResult hit, boolean glass) {
		PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeBlockPos(pos);
		passedData.writeBlockHitResult(hit);
		passedData.writeBoolean(glass);
		ClientPlayNetworking.send(SecretBlocks.id("hit_setter"), passedData);
	}
}
