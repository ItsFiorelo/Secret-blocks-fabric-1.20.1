package net.calibermc.secretblocks.render;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class SecretBlockUnbakedModel implements UnbakedModel {

	public static Mesh mesh;
	private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.singletonList(DEFAULT_BLOCK_MODEL);
	}

	@Override
	public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
		// no-op
	}

	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		return new SecretBlockBakedModel();
	}
}
