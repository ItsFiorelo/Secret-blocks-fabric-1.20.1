package net.calibermc.secretblocks.render;

import net.calibermc.secretblocks.SecretBlocks;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SecretBlockModelResolver implements ModelResolver {
	public static final SecretBlockUnbakedModel CAMO_MODEL = new SecretBlockUnbakedModel();
	public static final Identifier CAMO_BLOCK = SecretBlocks.id("block/camo_block");

	@Override
	public @Nullable UnbakedModel resolveModel(Context context) {
		return CAMO_BLOCK.equals(context.id()) ? CAMO_MODEL : null;
	}
}
