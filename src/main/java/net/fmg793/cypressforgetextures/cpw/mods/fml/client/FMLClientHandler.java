package net.fmg793.cypressforgetextures.cpw.mods.fml.client;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.DynamicTexture;

import org.lwjgl.opengl.GL11;

import ext.client.InputHandler;

public class FMLClientHandler {
	private static final FMLClientHandler INSTANCE = new FMLClientHandler();
	private Minecraft client;
	public HashSet animationSet = new HashSet();
	public List addedTextureFX = new ArrayList();
	private HashMap textureDims = new HashMap();
	private IdentityHashMap effectTextures = new IdentityHashMap();

	public static FMLClientHandler instance() {
		return INSTANCE;
	}

	public void addAnimation(DynamicTexture anim) {
		OverrideInfo info = new OverrideInfo();
		info.index = anim.sprite;
		info.imageIndex = anim.atlas;
		info.textureFX = anim;
		if(this.animationSet.contains(info)) {
			this.animationSet.remove(info);
		}

		this.animationSet.add(info);
	}

	public void setTextureDimensions(int id, int width, int height, List effects) {
		Dimension dim = new Dimension(width, height);
		this.textureDims.put(id, dim);
		Iterator i$ = effects.iterator();

		while(i$.hasNext()) {
			DynamicTexture tex = (DynamicTexture)i$.next();
			if(this.getEffectTexture(tex) == id && tex instanceof ITextureFX) {
				((ITextureFX)tex).onTextureDimensionsUpdate(width, height);
			}
		}

	}

	public Dimension getTextureDimensions(DynamicTexture effect) {
		return this.getTextureDimensions(this.getEffectTexture(effect));
	}

	public Dimension getTextureDimensions(int id) {
		return (Dimension)this.textureDims.get(id);
	}

	public int getEffectTexture(DynamicTexture effect) {
		Integer id = (Integer)this.effectTextures.get(effect);
		if(id != null) {
			return id.intValue();
		} else {
			int old = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			this.client = InputHandler.minecraft;
			effect.bind(this.client.textureManager);
			id = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, old);
			this.effectTextures.put(effect, id);
			return id.intValue();
		}
	}

	public boolean onUpdateTextureEffect(DynamicTexture effect) {
		ITextureFX ifx = effect instanceof ITextureFX ? (ITextureFX)effect : null;
		if(ifx != null && ifx.getErrored()) {
			return false;
		} else {
			String name = effect.getClass().getSimpleName();
			Profiler.startSection(name);

			try {
				effect.tick();
			} catch (Exception exception7) {
				if(ifx != null) {
					ifx.setErrored(true);
				}

				Profiler.endSection();
				return false;
			}

			Profiler.endSection();
			if(ifx != null) {
				Dimension dim = this.getTextureDimensions(effect);
				int target = (dim.width >> 4) * (dim.height >> 4) << 2;
				if(effect.pixels.length != target) {
					ifx.setErrored(true);
					return false;
				}
			}

			return true;
		}
	}

	public void scaleTextureFXData(byte[] data, ByteBuffer buf, int target, int length) {
		int sWidth = (int)Math.sqrt((double)(data.length / 4));
		int factor = target / sWidth;
		byte[] tmp = new byte[4];
		buf.clear();
		if(factor > 1) {
			for(int y = 0; y < sWidth; ++y) {
				int sRowOff = sWidth * y;
				int tRowOff = target * y * factor;

				for(int x = 0; x < sWidth; ++x) {
					int sPos = (x + sRowOff) * 4;
					tmp[0] = data[sPos + 0];
					tmp[1] = data[sPos + 1];
					tmp[2] = data[sPos + 2];
					tmp[3] = data[sPos + 3];
					int tPosTop = x * factor + tRowOff;

					for(int y2 = 0; y2 < factor; ++y2) {
						buf.position((tPosTop + y2 * target) * 4);

						for(int x2 = 0; x2 < factor; ++x2) {
							buf.put(tmp);
						}
					}
				}
			}
		}

		buf.position(0).limit(length);
	}

	public void onPreRegisterEffect(DynamicTexture effect) {
		Dimension dim = this.getTextureDimensions(effect);
		if(effect instanceof ITextureFX) {
			((ITextureFX)effect).onTextureDimensionsUpdate(dim.width, dim.height);
		}

	}
}
