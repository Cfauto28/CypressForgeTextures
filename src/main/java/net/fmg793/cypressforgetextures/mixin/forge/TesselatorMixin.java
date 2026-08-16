package net.fmg793.cypressforgetextures.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;

import net.fmg793.cypressforgetextures.impl.forge.ITesselatorProvider;
import net.minecraft.client.render.vertex.Tesselator;

@Mixin(Tesselator.class)
public class TesselatorMixin implements ITesselatorProvider {
	@Override
	public int textureID(int i) {
		return i;
	}

	@Override
	public boolean renderingWorldRenderer(boolean b) {
		return b;
	}
}
