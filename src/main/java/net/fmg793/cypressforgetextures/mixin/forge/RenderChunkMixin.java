package net.fmg793.cypressforgetextures.mixin.forge;

import java.util.HashSet;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ext.block.ExtBlock;
import ext.util.ExtLogger;
import net.fmg793.cypressforgetextures.forge.ForgeHooksClient;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.client.render.world.RenderChunk;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldRegion;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(RenderChunk.class)
public abstract class RenderChunkMixin {

	@Shadow
	public World world;

	@Shadow
	private int glList;

	@Shadow
	private static Tesselator TESSELATOR;

	@Shadow
	public static int updateCounter;

	@Shadow
	public int originX;

	@Shadow
	public int originY;

	@Shadow
	public int originZ;

	@Shadow
	public int sizeX;

	@Shadow
	public int sizeY;

	@Shadow
	public int sizeZ;

	@Shadow
	public int renderX;

	@Shadow
	public int renderY;

	@Shadow
	public int renderZ;

	@Shadow
	public int renderOffsetX;

	@Shadow
	public int renderOffsetY;

	@Shadow
	public int renderOffsetZ;

	@Shadow
	public boolean visible;

	@Shadow
	public boolean[] empty;

	@Shadow
	public int centerX;

	@Shadow
	public int centerY;

	@Shadow
	public int centerZ;

	@Shadow
	public float radius;

	@Shadow
	public boolean dirty;

	@Shadow
	public Box bounds;

	@Shadow
	public int id;

	@Shadow
	public boolean occlusionVisible;

	@Shadow
	public boolean occlusionQueryPending;

	@Shadow
	public int occlusionQuery;

	@Shadow
	public boolean hasSkyLight;

	@Shadow
	public List renderableBlockEntities;

	@Shadow
	private List blockEntitiesToBeRendered;

	@Shadow
	abstract void glTranslate();

	@Overwrite
	public void compile() {
		if(this.dirty) {
			++updateCounter;
			int i1 = this.originX;
			int i2 = this.originY;
			int i3 = this.originZ;
			int i4 = this.originX + this.sizeX;
			int i5 = this.originY + this.sizeY;
			int i6 = this.originZ + this.sizeZ;

			for(int i7 = 0; i7 < 2; ++i7) {
				this.empty[i7] = true;
			}

			WorldChunk.hasSkyLight = false;
			HashSet hashSet21 = new HashSet();
			hashSet21.addAll(this.renderableBlockEntities);
			this.renderableBlockEntities.clear();
			byte b8 = 1;
			WorldRegion worldRegion9 = new WorldRegion(this.world, i1 - b8, i2 - b8, i3 - b8, i4 + b8, i5 + b8, i6 + b8);
			BlockRenderer blockRenderer10 = new BlockRenderer(worldRegion9);

			for(int i11 = 0; i11 < 2; ++i11) {
				boolean z12 = false;
				boolean z13 = false;
				boolean z14 = false;

				for(int i15 = i2; i15 < i5; ++i15) {
					for(int i16 = i3; i16 < i6; ++i16) {
						for(int i17 = i1; i17 < i4; ++i17) {
							int i18 = worldRegion9.getBlock(i17, i15, i16);
							if(i18 > 0) {
								if(!z14) {
									z14 = true;
									GL11.glNewList(this.glList + i11, GL11.GL_COMPILE);
									GL11.glPushMatrix();
									this.glTranslate();
									float f19 = 1.000001F;
									GL11.glTranslatef((float)(-this.sizeZ) / 2.0F, (float)(-this.sizeY) / 2.0F, (float)(-this.sizeZ) / 2.0F);
									GL11.glScalef(f19, f19, f19);
									ForgeHooksClient.beforeRenderPass(i11);
									GL11.glTranslatef((float)this.sizeZ / 2.0F, (float)this.sizeY / 2.0F, (float)this.sizeZ / 2.0F);
									TESSELATOR.method_1_1154(false);
									TESSELATOR.offset((double)(-this.originX), (double)(-this.originY), (double)(-this.originZ));
								}

								if(i11 == 0 && ExtBlock.HAS_BLOCK_ENTITY[i18]) {
									BlockEntity blockEntity23 = worldRegion9.getBlockEntity(i17, i15, i16);
									if(BlockEntityRenderDispatcher.INSTANCE.hasRenderer(blockEntity23)) {
										this.renderableBlockEntities.add(blockEntity23);
									}
								}

								if(ExtBlock.BY_ID[i18] == null) {
									ExtLogger.info("NULL BLOCK ID " + i18);
								} else {
									ExtBlock extBlock24 = ExtBlock.BY_ID[i18];
									int i20 = extBlock24.getRenderLayer();
									if(i20 > i11) {
										z12 = true;
									}
									if(ForgeHooksClient.canRenderInPass(extBlock24, i11)) {
										ForgeHooksClient.beforeBlockRender(extBlock24, blockRenderer10);
										z13 |= blockRenderer10.method_1_2004(extBlock24, i17, i15, i16);
										ForgeHooksClient.afterBlockRender(extBlock24, blockRenderer10);
									}
								}
							}
						}
					}
				}

				if(z14) {
					ForgeHooksClient.afterRenderPass(i11);
					TESSELATOR.end();
					GL11.glPopMatrix();
					GL11.glEndList();
					TESSELATOR.offset(0.0D, 0.0D, 0.0D);
				} else {
					z13 = false;
				}

				if(z13) {
					this.empty[i11] = false;
				}

				if(!z12) {
					break;
				}
			}

			HashSet hashSet22 = new HashSet();
			hashSet22.addAll(this.renderableBlockEntities);
			hashSet22.removeAll(hashSet21);
			this.blockEntitiesToBeRendered.addAll(hashSet22);
			hashSet21.removeAll(this.renderableBlockEntities);
			this.blockEntitiesToBeRendered.removeAll(hashSet21);
			this.hasSkyLight = WorldChunk.hasSkyLight;
		}

	}
}
