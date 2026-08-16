package net.fmg793.cypressforgetextures.mixin.forge;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ext.block.ExtBlock;
import ext.world.WorldUnknownClass2;
import net.fmg793.cypressforgetextures.forge.ForgeHooksClient;
import net.minecraft.client.ParticleManager;
import net.minecraft.client.entity.particle.BlockParticle;
import net.minecraft.client.entity.particle.Particle;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

	@Shadow
	protected World world;

	@Shadow
	private List[] particles;

	@Shadow
	private TextureManager textureManager;

	@Shadow
	private Random random;

	@Unique
	private Hashtable effectList = new Hashtable();

	@Inject(method = "tick()V", at = @At("TAIL"))
	public void tickInject(CallbackInfo ci) {
		Iterator iterator6 = this.effectList.keySet().iterator();

		while(iterator6.hasNext()) {
			String string7 = (String)iterator6.next();
			ArrayList arrayList8 = (ArrayList)this.effectList.get(string7);

			for(int y = 0; y < arrayList8.size(); ++y) {
				Particle var3 = (Particle)arrayList8.get(y);
				var3.tick();
				if(var3.removed) {
					arrayList8.remove(y--);
				}
			}

			if(this.effectList.size() == 0) {
				this.effectList.remove(string7);
			}
		}
	}

	@Overwrite
	public void render(Entity camera, float tickDelta) {
		float f3 = MathHelper.cos(camera.yaw * (float)Math.PI / 180.0F);
		float f4 = MathHelper.sin(camera.yaw * (float)Math.PI / 180.0F);
		float f5 = -f4 * MathHelper.sin(camera.pitch * (float)Math.PI / 180.0F);
		float f6 = f3 * MathHelper.sin(camera.pitch * (float)Math.PI / 180.0F);
		float f7 = MathHelper.cos(camera.pitch * (float)Math.PI / 180.0F);
		Particle.lerpCameraX = camera.prevX + (camera.x - camera.prevX) * (double)tickDelta;
		Particle.lerpCameraY = camera.prevY + (camera.y - camera.prevY) * (double)tickDelta;
		Particle.lerpCameraZ = camera.prevZ + (camera.z - camera.prevZ) * (double)tickDelta;
		Particle entityFX12;

		for(int i8 = 0; i8 < 3; ++i8) {
			if(this.particles[i8].size() != 0) {
				int i9 = 0;
				if(i8 == 0) {
					i9 = this.textureManager.load("/particles.png");
				}

				if(i8 == 1) {
					i9 = this.textureManager.load(WorldUnknownClass2.method_1_1562(WorldUnknownClass2.field_1_2243));
				}

				if(i8 == 2) {
					i9 = this.textureManager.load("/gui/items.png");
				}

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, i9);
				Tesselator tesselator10 = Tesselator.INSTANCE;
				tesselator10.method_1_1141();

				for(int i11 = 0; i11 < this.particles[i8].size(); ++i11) {
					entityFX12 = (Particle)this.particles[i8].get(i11);
					entityFX12.render(tesselator10, tickDelta, f3, f7, f4, f5, f6);
				}

				tesselator10.end();
			}
		}

		Tesselator tessellator13 = Tesselator.INSTANCE;
		Iterator iterator15 = this.effectList.entrySet().iterator();

		while(iterator15.hasNext()) {
			Entry map$Entry14 = (Entry)iterator15.next();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.load((String)map$Entry14.getKey()));
			tessellator13.method_1_1141();
			Iterator iterator16 = ((ArrayList)map$Entry14.getValue()).iterator();

			while(iterator16.hasNext()) {
				entityFX12 = (Particle)iterator16.next();
				if(entityFX12.getAtlasType() != 3) {
					entityFX12.render(tessellator13, tickDelta, f3, f7, f4, f5, f6);
				}
			}

			tessellator13.end();
		}
	}

	@Inject(method = "setWorld(Lnet/minecraft/world/World;)V", at = @At("TAIL"))
	public void setWorld(World world, CallbackInfo ci) {
		Iterator iterator4 = this.effectList.values().iterator();

		while(iterator4.hasNext()) {
			ArrayList entry = (ArrayList)iterator4.next();
			entry.clear();
		}

		this.effectList.clear();

	}

	@Overwrite
	public void handleBlockBreaking(int x, int y, int z) {
		int i4 = this.world.getBlock(x, y, z);
		if(i4 != 0) {
			ExtBlock extBlock5 = ExtBlock.BY_ID[i4];
			byte b6 = 4;

			for(int i7 = 0; i7 < b6; ++i7) {
				for(int i8 = 0; i8 < b6; ++i8) {
					for(int i9 = 0; i9 < b6; ++i9) {
						double d10 = (double)x + ((double)i7 + 0.5D) / (double)b6;
						double d12 = (double)y + ((double)i8 + 0.5D) / (double)b6;
						double d14 = (double)z + ((double)i9 + 0.5D) / (double)b6;
						this.addEffect(new BlockParticle(this.world, d10, d12, d14, d10 - (double)x - 0.5D, d12 - (double)y - 0.5D, d14 - (double)z - 0.5D, extBlock5), extBlock5);
					}
				}
			}
		}

	}

	@Overwrite
	public void handleBlockMining(int x, int y, int z, int face) {
		int i5 = this.world.getBlock(x, y, z);
		if(i5 != 0) {
			ExtBlock extBlock6 = ExtBlock.BY_ID[i5];
			float f7 = 0.1F;
			double d8 = (double)x + this.random.nextDouble() * (extBlock6.maxX - extBlock6.minX - (double)(f7 * 2.0F)) + (double)f7 + extBlock6.minX;
			double d10 = (double)y + this.random.nextDouble() * (extBlock6.maxY - extBlock6.minY - (double)(f7 * 2.0F)) + (double)f7 + extBlock6.minY;
			double d12 = (double)z + this.random.nextDouble() * (extBlock6.maxZ - extBlock6.minZ - (double)(f7 * 2.0F)) + (double)f7 + extBlock6.minZ;
			if(face == 0) {
				d10 = (double)y + extBlock6.minY - (double)f7;
			}

			if(face == 1) {
				d10 = (double)y + extBlock6.maxY + (double)f7;
			}

			if(face == 2) {
				d12 = (double)z + extBlock6.minZ - (double)f7;
			}

			if(face == 3) {
				d12 = (double)z + extBlock6.maxZ + (double)f7;
			}

			if(face == 4) {
				d8 = (double)x + extBlock6.minX - (double)f7;
			}

			if(face == 5) {
				d8 = (double)x + extBlock6.maxX + (double)f7;
			}

			this.addEffect((new BlockParticle(this.world, d8, d10, d12, 0.0D, 0.0D, 0.0D, extBlock6)).multiplyVelocity(0.2F).multiplySize(0.6F), extBlock6);
		}

	}

	@Overwrite
	public String getDebugInfo() {
		int size = 0;
		List[] i$ = this.particles;
		int entry = i$.length;

		for(int i$1 = 0; i$1 < entry; ++i$1) {
			List x = i$[i$1];
			size += x.size();
		}

		ArrayList arrayList7;
		for(Iterator iterator6 = this.effectList.values().iterator(); iterator6.hasNext(); size += arrayList7.size()) {
			arrayList7 = (ArrayList)iterator6.next();
		}

		return Integer.toString(size);
	}

	@Unique
	public void addEffect(Particle effect, Object effectObject) {
		String texture = "/terrain.png";
		if(effect.getAtlasType() == 0) {
			texture = "/particles.png";
		} else if(effect.getAtlasType() == 2) {
			texture = "/gui/items.png";
		}

		texture = ForgeHooksClient.getTexture(texture, effectObject);
		ArrayList set = (ArrayList)this.effectList.get(texture);
		if(set == null) {
			set = new ArrayList();
			this.effectList.put(texture, set);
		}

		set.add(effect);
	}
}
