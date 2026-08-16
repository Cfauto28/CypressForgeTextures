package net.fmg793.cypressforgetextures.mixin.forge;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ext.block.ExtBlock;
import net.fmg793.cypressforgetextures.forge.ForgeHooksClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin extends EntityRenderer {

	@Shadow
	private BlockRenderer blockRenderer;

	@Shadow
	private Random random;

	/**
	 * @author FMG793
	 * @reason
	 */
	@Overwrite
	public void render(ItemEntity itemEntity1, double d2, double d4, double d6, float f8, float f9) {
		this.random.setSeed(187L);
		ItemStack itemStack10 = itemEntity1.item;
		GL11.glPushMatrix();
		float f11 = MathHelper.sin(((float)itemEntity1.age + f9) / 10.0F + itemEntity1.bobOffset) * 0.1F + 0.1F;
		float f12 = (((float)itemEntity1.age + f9) / 20.0F + itemEntity1.bobOffset) * 57.295776F;
		byte b13 = 1;
		if(itemEntity1.item.size > 1) {
			b13 = 2;
		}

		if(itemEntity1.item.size > 5) {
			b13 = 3;
		}

		if(itemEntity1.item.size > 20) {
			b13 = 4;
		}

		GL11.glTranslatef((float)d2, (float)d4 + f11, (float)d6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		if(!ForgeHooksClient.renderEntityItem(itemEntity1, itemStack10, f11, f12, this.random, this.dispatcher.textureManager, this.blockRenderer)) {
			float f14;
			float f15;
			float f16;
			if(itemStack10.getItem() instanceof BlockItem && BlockRenderer.isItem3d(ExtBlock.BY_ID[itemStack10.id].getRenderType())) {
				GL11.glRotatef(f12, 0.0F, 1.0F, 0.0F);
				this.bindTexture(ExtBlock.BY_ID[itemStack10.id].getTextureFile());
				float f27 = 0.25F;
				if(!ExtBlock.BY_ID[itemStack10.id].isCube() && itemStack10.id != ExtBlock.STONE_SLAB.id) {
					f27 = 0.5F;
				}

				GL11.glScalef(f27, f27, f27);

				for(int i28 = 0; i28 < b13; ++i28) {
					GL11.glPushMatrix();
					if(i28 > 0) {
						f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f27;
						f15 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f27;
						f16 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f27;
						GL11.glTranslatef(f14, f15, f16);
					}

					this.blockRenderer.method_1_1996(ExtBlock.BY_ID[itemStack10.id]);
					GL11.glPopMatrix();
				}
			} else {
				GL11.glScalef(0.5F, 0.5F, 0.5F);
				int i17 = itemStack10.getSprite();
				this.bindTexture(itemStack10.getItem().getTextureFile());

				Tesselator tesselator18 = Tesselator.INSTANCE;
				f14 = (float)(i17 % 16 * 16 + 0) / 256.0F;
				f15 = (float)(i17 % 16 * 16 + 16) / 256.0F;
				f16 = (float)(i17 / 16 * 16 + 0) / (itemStack10.getItem() instanceof BlockItem ? 512.0F : 256.0F);
				float f19 = (float)(i17 / 16 * 16 + 16) / (itemStack10.getItem() instanceof BlockItem ? 512.0F : 256.0F);
				float f20 = 1.0F;
				float f21 = 0.5F;
				float f22 = 0.25F;

				for(int i23 = 0; i23 < b13; ++i23) {
					GL11.glPushMatrix();
					if(i23 > 0) {
						float f24 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						float f25 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						float f26 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						GL11.glTranslatef(f24, f25, f26);
					}

					GL11.glRotatef(180.0F - this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
					tesselator18.method_1_1141();
					tesselator18.normal(0.0F, 1.0F, 0.0F);
					tesselator18.vertex((double)(0.0F - f21), (double)(0.0F - f22), 0.0D, (double)f14, (double)f19);
					tesselator18.vertex((double)(f20 - f21), (double)(0.0F - f22), 0.0D, (double)f15, (double)f19);
					tesselator18.vertex((double)(f20 - f21), (double)(1.0F - f22), 0.0D, (double)f15, (double)f16);
					tesselator18.vertex((double)(0.0F - f21), (double)(1.0F - f22), 0.0D, (double)f14, (double)f16);
					tesselator18.end();
					GL11.glPopMatrix();
				}
			}
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	@Overwrite
	public void renderGuiItem(TextRenderer textRenderer, TextureManager textureManager, ItemStack item, int x, int y) {
		if(item != null) {
			if(Item.BY_ID[item.id] instanceof BlockItem && BlockRenderer.isItem3d(ExtBlock.BY_ID[item.id].getRenderType())) {
				int i6 = item.id;
				textureManager.bind(textureManager.load(ExtBlock.BY_ID[item.id].getTextureFile()));
				ExtBlock extBlock7 = ExtBlock.BY_ID[i6];
				GL11.glPushMatrix();
				GL11.glTranslatef((float)(x - 2), (float)(y + 3), 0.0F);
				GL11.glScalef(10.0F, 10.0F, 10.0F);
				GL11.glTranslatef(1.0F, 0.5F, 8.0F);
				GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glScalef(1.0F, 1.0F, 1.0F);
				this.blockRenderer.method_1_1996(extBlock7);
				GL11.glPopMatrix();
			} else if(item.getSprite() >= 0) {
				GL11.glDisable(GL11.GL_LIGHTING);
				textureManager.bind(textureManager.load(Item.BY_ID[item.id].getTextureFile()));

				this.method_1_1744(x, y, item.getSprite() % 16 * 16, item.getSprite() / 16 * 16, 16, 16, item.getItem() instanceof BlockItem);
				GL11.glEnable(GL11.GL_LIGHTING);
			}

			GL11.glEnable(GL11.GL_CULL_FACE);
		}

	}

	@Shadow
	public abstract void method_1_1744(int i1, int i2, int i3, int i4, int i5, int i6, boolean z7);

	@Overwrite
	public void render(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta) {
		this.render((ItemEntity)entity, dx, dy, dz, yaw, tickDelta);
	}
}
