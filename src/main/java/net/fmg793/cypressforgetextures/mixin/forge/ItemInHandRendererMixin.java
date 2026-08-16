package net.fmg793.cypressforgetextures.mixin.forge;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ext.block.ExtBlock;
import ext.client.InputHandler;
import net.fmg793.cypressforgetextures.forge.ForgeHooksClient;
import net.fmg793.cypressforgetextures.forge.IItemRenderer;
import net.fmg793.cypressforgetextures.forge.MinecraftForgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.ItemInHandRenderer;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	@Shadow
	private Minecraft minecraft;

	@Shadow
	private BlockRenderer blockRenderer;

	/**
	 * @author FMG793
	 * @reason
	 */
	@Overwrite
	public void render(ItemStack item) {
		GL11.glPushMatrix();
		IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, IItemRenderer.ItemRenderType.EQUIPPED);
		if(customRenderer != null) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(item.getItem().getTextureFile()));
			ForgeHooksClient.renderEquippedItem(customRenderer, this.blockRenderer, InputHandler.minecraft.player, item);
		} else if(item.getItem() instanceof BlockItem && BlockRenderer.isItem3d(ExtBlock.BY_ID[item.id].getRenderType())) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(item.getItem().getTextureFile()));
			this.blockRenderer.method_1_1996(ExtBlock.BY_ID[item.id]);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(item.getItem().getTextureFile()));
			Tesselator tesselator2 = Tesselator.INSTANCE;
			float f3 = (float)(item.getSprite() % 16 * 16 + 0) / 256.0F;
			float f4 = (float)(item.getSprite() % 16 * 16 + 16) / 256.0F;
			float f5 = (float)(item.getSprite() / 16 * 16 + 0) / (item.getItem() instanceof BlockItem ? 512.0F : 256.0F);
			float f6 = (float)(item.getSprite() / 16 * 16 + 16) / (item.getItem() instanceof BlockItem ? 512.0F : 256.0F);
			float f7 = 1.0F;
			float f8 = 0.0F;
			float f9 = 0.3F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glTranslatef(-f8, -f9, 0.0F);
			float f10 = 1.5F;
			GL11.glScalef(f10, f10, f10);
			GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
			float f11 = 0.0625F;
			tesselator2.method_1_1154(true);
			tesselator2.normal(0.0F, 0.0F, 1.0F);
			tesselator2.vertex(0.0D, 0.0D, 0.0D, (double)f4, (double)f6);
			tesselator2.vertex((double)f7, 0.0D, 0.0D, (double)f3, (double)f6);
			tesselator2.vertex((double)f7, 1.0D, 0.0D, (double)f3, (double)f5);
			tesselator2.vertex(0.0D, 1.0D, 0.0D, (double)f4, (double)f5);
			tesselator2.end();
			tesselator2.method_1_1154(true);
			tesselator2.normal(0.0F, 0.0F, -1.0F);
			tesselator2.vertex(0.0D, 1.0D, (double)(0.0F - f11), (double)f4, (double)f5);
			tesselator2.vertex((double)f7, 1.0D, (double)(0.0F - f11), (double)f3, (double)f5);
			tesselator2.vertex((double)f7, 0.0D, (double)(0.0F - f11), (double)f3, (double)f6);
			tesselator2.vertex(0.0D, 0.0D, (double)(0.0F - f11), (double)f4, (double)f6);
			tesselator2.end();
			tesselator2.method_1_1154(true);
			tesselator2.normal(-1.0F, 0.0F, 0.0F);

			int i12;
			float f13;
			float f14;
			float f15;
			for(i12 = 0; i12 < 16; ++i12) {
				f13 = (float)i12 / 16.0F;
				f14 = f4 + (f3 - f4) * f13 - 0.001953125F;
				f15 = f7 * f13;
				tesselator2.vertex((double)f15, 0.0D, (double)(0.0F - f11), (double)f14, (double)f6);
				tesselator2.vertex((double)f15, 0.0D, 0.0D, (double)f14, (double)f6);
				tesselator2.vertex((double)f15, 1.0D, 0.0D, (double)f14, (double)f5);
				tesselator2.vertex((double)f15, 1.0D, (double)(0.0F - f11), (double)f14, (double)f5);
			}

			tesselator2.end();
			tesselator2.method_1_1154(true);
			tesselator2.normal(1.0F, 0.0F, 0.0F);

			for(i12 = 0; i12 < 16; ++i12) {
				f13 = (float)i12 / 16.0F;
				f14 = f4 + (f3 - f4) * f13 - 0.001953125F;
				f15 = f7 * f13 + 0.0625F;
				tesselator2.vertex((double)f15, 1.0D, (double)(0.0F - f11), (double)f14, (double)f5);
				tesselator2.vertex((double)f15, 1.0D, 0.0D, (double)f14, (double)f5);
				tesselator2.vertex((double)f15, 0.0D, 0.0D, (double)f14, (double)f6);
				tesselator2.vertex((double)f15, 0.0D, (double)(0.0F - f11), (double)f14, (double)f6);
			}

			tesselator2.end();
			tesselator2.method_1_1154(true);
			tesselator2.normal(0.0F, 1.0F, 0.0F);

			for(i12 = 0; i12 < 16; ++i12) {
				f13 = (float)i12 / 16.0F;
				f14 = f6 + (f5 - f6) * f13 - 0.001953125F;
				f15 = f7 * f13 + 0.0625F;
				tesselator2.vertex(0.0D, (double)f15, 0.0D, (double)f4, (double)f14);
				tesselator2.vertex((double)f7, (double)f15, 0.0D, (double)f3, (double)f14);
				tesselator2.vertex((double)f7, (double)f15, (double)(0.0F - f11), (double)f3, (double)f14);
				tesselator2.vertex(0.0D, (double)f15, (double)(0.0F - f11), (double)f4, (double)f14);
			}

			tesselator2.end();
			tesselator2.method_1_1154(true);
			tesselator2.normal(0.0F, -1.0F, 0.0F);

			for(i12 = 0; i12 < 16; ++i12) {
				f13 = (float)i12 / 16.0F;
				f14 = f6 + (f5 - f6) * f13 - 0.001953125F;
				f15 = f7 * f13;
				tesselator2.vertex((double)f7, (double)f15, 0.0D, (double)f3, (double)f14);
				tesselator2.vertex(0.0D, (double)f15, 0.0D, (double)f4, (double)f14);
				tesselator2.vertex(0.0D, (double)f15, (double)(0.0F - f11), (double)f4, (double)f14);
				tesselator2.vertex((double)f7, (double)f15, (double)(0.0F - f11), (double)f3, (double)f14);
			}

			tesselator2.end();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}

		GL11.glPopMatrix();
	}
}
