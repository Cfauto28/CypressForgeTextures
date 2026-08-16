package net.fmg793.cypressforgetextures.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fmg793.cypressforgetextures.RegisterRenderInformation;
import net.fmg793.cypressforgetextures.forge.MinecraftForgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "method_1_518(Lnet/minecraft/world/World;Ljava/lang/String;Z)V", at = @At("TAIL"))
	public void regTextures(World world, String string, boolean bl, CallbackInfo ci) {
		for (int i = 0; i < RegisterRenderInformation.pathList.size(); i++) {
            if (!RegisterRenderInformation.pathList.isEmpty()) {
            	MinecraftForgeClient.preloadTexture(RegisterRenderInformation.pathList.get(i));
            }
        }
    }
}
