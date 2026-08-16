package net.fmg793.cypressforgetextures.forge;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ext.block.ExtBlock;
import ext.client.InputHandler;

public class ForgeHooksClient {
	private static Method textureID = null;
	private static boolean textureIDChecked = false;
	public static boolean enable4096 = false;
	public static LinkedList highlightHandlers = new LinkedList();
	public static LinkedList renderWorldLastHandlers = new LinkedList();
	public static LinkedList textureLoadHandlers = new LinkedList();
	public static HashMap tessellators = new HashMap();
	public static HashMap textures = new HashMap();
	public static boolean inWorld = false;
	public static TreeSet renderTextures = new TreeSet();
	public static Tesselator defaultTessellator = null;
	public static HashMap renderHandlers = new HashMap();
	public static IRenderContextHandler unbindContext = null;
	static int renderPass = -1;
	public static LinkedList soundHandlers = new LinkedList();
	public static LinkedList soundHandlers2 = new LinkedList();

	public static void onTextureLoad(String textureName, int textureID) {
		Iterator i$ = textureLoadHandlers.iterator();

		while(i$.hasNext()) {
			ITextureLoadHandler handler = (ITextureLoadHandler)i$.next();
			handler.onTextureLoad(textureName, textureID);
		}

	}

	public static boolean canRenderInPass(ExtBlock block, int pass) {
		return block instanceof IMultipassRender ? ((IMultipassRender)block).canRenderInPass(pass) : pass == block.getRenderLayer();
	}

	protected static void registerRenderContextHandler(String texture, int subID, IRenderContextHandler handler) {
		Integer texID = (Integer)textures.get(texture);
		if(texID == null) {
			texID = InputHandler.minecraft.textureManager.load(texture);
			textures.put(texture, texID);
		}

		renderHandlers.put(new ForgeHooksClient.TesKey(texID.intValue(), subID), handler);
	}

	protected static void bindTessellator(int texture, int subID) {
		ForgeHooksClient.TesKey key = new ForgeHooksClient.TesKey(texture, subID);
		Tesselator tess = (Tesselator)tessellators.get(key);
		if(tess == null) {
			tess = new Tesselator(2097152);
			if(!textureIDChecked && textureID == null) {
				textureIDChecked = true;

				try {
					textureID = Tesselator.class.getMethod("textureID");
				} catch (NoSuchMethodException noSuchFieldException5) {
				}
			}

			if(textureID != null) {
				tess.textureID(texture);// = texture;
			}

			tessellators.put(key, tess);
		}

		if(inWorld && !renderTextures.contains(key)) {
			renderTextures.add(key);
			tess.method_1_1141();
			tess.offset(defaultTessellator.offsetX, defaultTessellator.offsetY, defaultTessellator.offsetZ);
		}

		Tesselator.INSTANCE = tess;
	}

	protected static void bindTexture(String texture, int subID) {
		Integer texID = (Integer)textures.get(texture);
		if(texID == null) {
			texID = InputHandler.minecraft.textureManager.load(texture);
			textures.put(texture, texID);
		}

		if(!inWorld) {
			if(unbindContext != null) {
				unbindContext.afterRenderContext();
				unbindContext = null;
			}

			if(Tesselator.INSTANCE.tesselating) {
				int mode = Tesselator.INSTANCE.drawMode;
				Tesselator.INSTANCE.end();
				Tesselator.INSTANCE.method_1_1146(mode);
			}

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID.intValue());
			unbindContext = (IRenderContextHandler)renderHandlers.get(new ForgeHooksClient.TesKey(texID.intValue(), subID));
			if(unbindContext != null) {
				unbindContext.beforeRenderContext();
			}

		} else {
			bindTessellator(texID.intValue(), subID);
		}
	}

	protected static void unbindTexture() {
		if(inWorld) {
			Tesselator.INSTANCE = defaultTessellator;
		} else {
			if(Tesselator.INSTANCE.tesselating) {
				int mode = Tesselator.INSTANCE.drawMode;
				Tesselator.INSTANCE.end();
				if(unbindContext != null) {
					unbindContext.afterRenderContext();
					unbindContext = null;
				}

				Tesselator.INSTANCE.method_1_1146(mode);
			}

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, InputHandler.minecraft.textureManager.load("/terrain.png"));
		}
	}

	public static void beforeRenderPass(int pass) {
		renderPass = pass;
		defaultTessellator = Tesselator.INSTANCE;
		Tesselator.INSTANCE.renderingWorldRenderer(true);// = true;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, InputHandler.minecraft.textureManager.load("/terrain.png"));
		renderTextures.clear();
		inWorld = true;
	}

	public static void afterRenderPass(int pass) {
		renderPass = -1;
		inWorld = false;
		Iterator i$ = renderTextures.iterator();

		while(i$.hasNext()) {
			ForgeHooksClient.TesKey info = (ForgeHooksClient.TesKey)i$.next();
			IRenderContextHandler handler = (IRenderContextHandler)renderHandlers.get(info);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.tex);
			Tesselator tess = (Tesselator)tessellators.get(info);
			if(handler == null) {
				tess.end();
			} else {
				Tesselator.INSTANCE = tess;
				handler.beforeRenderContext();
				tess.end();
				handler.afterRenderContext();
			}
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, InputHandler.minecraft.textureManager.load("/terrain.png"));
		Tesselator.INSTANCE.renderingWorldRenderer(false);// = false;
		Tesselator.INSTANCE = defaultTessellator;
	}

	public static void beforeBlockRender(ExtBlock block, BlockRenderer render) {
		if(!block.isDefaultTexture() && render.forcedSprite == -1) {
			bindTexture(block.getTextureFile(), 0);
		}

	}

	public static void afterBlockRender(ExtBlock block, BlockRenderer render) {
		if(!block.isDefaultTexture() && render.forcedSprite == -1) {
			unbindTexture();
		}

	}

	public static void overrideTexture(Object obj) {
		if(obj instanceof ITextureProvider) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, InputHandler.minecraft.textureManager.load(((ITextureProvider)obj).getTextureFile()));
		}

	}

	public static String getTexture(String def, Object obj) {
		return obj instanceof ITextureProvider ? ((ITextureProvider)obj).getTextureFile() : def;
	}

	public static void renderEquippedItem(IItemRenderer customRenderer, BlockRenderer renderBlocks, MobEntity entity, ItemStack item) {
		if(customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, item, IItemRenderer.ItemRendererHelper.EQUIPPED_BLOCK)) {
			GL11.glPushMatrix();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			customRenderer.renderItem(IItemRenderer.ItemRenderType.EQUIPPED, item, new Object[]{renderBlocks, entity});
			GL11.glPopMatrix();
		} else {
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glTranslatef(0.0F, -0.3F, 0.0F);
			GL11.glScalef(1.5F, 1.5F, 1.5F);
			GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
			customRenderer.renderItem(IItemRenderer.ItemRenderType.EQUIPPED, item, new Object[]{renderBlocks, entity});
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}

	}

	public static boolean renderEntityItem(ItemEntity entity, ItemStack item, float bobing, float rotation, Random random, TextureManager engine, BlockRenderer renderBlocks) {
		IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, IItemRenderer.ItemRenderType.ENTITY);
		if(customRenderer == null) {
			return false;
		} else {
			if(customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.ENTITY_ROTATION)) {
				GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
			}

			if(!customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.ENTITY_BOBBING)) {
				GL11.glTranslatef(0.0F, -bobing, 0.0F);
			}

			boolean is3D = customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, item, IItemRenderer.ItemRendererHelper.BLOCK_3D);
			if(!(item.getItem() instanceof BlockItem) || !is3D && !BlockRenderer.isItem3d(ExtBlock.BY_ID[item.id].getRenderType())) {
				engine.bind(engine.load(item.getItem().getTextureFile()));
				GL11.glScalef(0.5F, 0.5F, 0.5F);
				customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item, new Object[]{renderBlocks, entity});
			} else {
				engine.bind(engine.load(item.getItem().getTextureFile()));
				int renderType = ExtBlock.BY_ID[item.id].getRenderType();
				float scale = renderType != 1 && renderType != 19 && renderType != 12 && renderType != 2 ? 0.25F : 0.5F;
				GL11.glScalef(scale, scale, scale);
				int size = entity.item.size;
				boolean z10000;
				if(size > 20) {
					z10000 = true;
				} else if(size > 5) {
					z10000 = true;
				} else {
					z10000 = size > 1 ? true : true;
				}

				for(int j = 0; j < size; ++j) {
					GL11.glPushMatrix();
					if(j > 0) {
						GL11.glTranslatef((random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.5F, (random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.5F, (random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.5F);
					}

					customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item, new Object[]{renderBlocks, entity});
					GL11.glPopMatrix();
				}
			}

			return true;
		}
	}

	public static void onTextureLoadPre(String texture) {
		if(Tesselator.INSTANCE.renderingWorldRenderer(false)) {
			String msg = String.format("Warning: Texture %s not preloaded, will cause render glitches!", new Object[]{texture});
			System.out.println(msg);
			if(Tesselator.class.getPackage() != null && Tesselator.class.getPackage().equals("net.minecraft.src")) {
				Minecraft mc = InputHandler.minecraft;
				if(mc.gui != null) {
					mc.gui.printChatMessage(msg);
				}
			}
		}

	}

	private static class TesKey implements Comparable {
		public Integer tex;
		public Integer sub;

		public TesKey(int textureID, int subID) {
			this.tex = textureID;
			this.sub = subID;
		}

		public int compareTo(ForgeHooksClient.TesKey key) {
			return this.sub == key.sub ? this.tex - key.tex : this.sub - key.sub;
		}

		public boolean equals(Object obj) {
			return this.compareTo((ForgeHooksClient.TesKey)obj) == 0;
		}

		public int hashCode() {
			int c1 = this.tex.hashCode();
			int c2 = this.sub.hashCode();
			return c1 + 31 * c2;
		}

		public int compareTo(Object x0) {
			return this.compareTo((ForgeHooksClient.TesKey)x0);
		}
	}
}
