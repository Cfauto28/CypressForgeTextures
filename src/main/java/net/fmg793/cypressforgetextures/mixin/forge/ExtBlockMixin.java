package net.fmg793.cypressforgetextures.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ext.block.ExtBlock;
import net.fmg793.cypressforgetextures.forge.ITextureProvider;
import net.minecraft.block.material.Material;

@Mixin(ExtBlock.class)
public class ExtBlockMixin implements ITextureProvider {

	@Unique
    private String currentTexture;

	@Unique
	private boolean isDefaultTexture;

	@Inject(method = "<init>(ILnet/minecraft/block/material/Material;)V", at = @At("TAIL"))
    private void initHead(int id, Material material, CallbackInfo ci) {
		this.currentTexture = "/terrain.png";
		this.isDefaultTexture = this.getTextureFile() != null && this.getTextureFile().equalsIgnoreCase("/terrain.png");
    }

	@Override
	public String currentTexture() {
		return currentTexture;
	}

	@Override
	public boolean isDefaultTexture() {
		return isDefaultTexture;
	}

	@Override
	public String getTextureFile() {
		return this.currentTexture;
	}

	@Override
	public ExtBlock setTextureFile(String texture) {
		this.currentTexture = texture;
		this.isDefaultTexture = false;
		return (ExtBlock) (Object) this;
	}
}
