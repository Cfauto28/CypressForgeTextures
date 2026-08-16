package net.fmg793.cypressforgetextures.mixin.forge;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import ext.client.gui.screen.cheat.ScreenDebugMenu;
import ext.client.visuals.VisualsClass;
import ext.client.visuals.VisualsUnknownClass1;
import ext.network.ImageHolder;
import net.fmg793.cypressforgetextures.cpw.mods.fml.client.FMLClientHandler;
import net.fmg793.cypressforgetextures.forge.ForgeHooksClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.platform.MemoryTracker;
import net.minecraft.client.render.texture.DynamicTexture;
import net.minecraft.client.render.texture.TextureManager;

@Mixin(value = TextureManager.class, priority = 990)
public abstract class TextureManagerMixin {

	@Shadow
	public static boolean MIPMAP;

	@Shadow
	private HashMap textureIds;

	@Shadow
	private IntBuffer idBuffer;

	@Shadow
	private ByteBuffer imageBuffer;

	@Shadow
	private List dynamicTextures;

	@Shadow
	public GameOptions options;

	@Shadow
	private boolean blur;

	@Shadow
	public VisualsUnknownClass1 field_1_1559;

	@Shadow
	public ImageHolder imageHolder;

	@Shadow
	abstract BufferedImage method_1_1196(InputStream inputStream) throws IOException;

	@Shadow
	abstract BufferedImage rescale(BufferedImage image);

	@Shadow
	abstract int crispBlend(int color1, int color2);

	@Overwrite
	public int load(String path) {
		VisualsClass visualsClass2 = this.field_1_1559.visualClass;
		Integer integer3 = (Integer)this.textureIds.get(path);
		if(integer3 != null) {
			return integer3.intValue();
		} else {
			try {
				ForgeHooksClient.onTextureLoadPre(path);
				this.idBuffer.clear();
				MemoryTracker.genTextures(this.idBuffer);
				int i4 = this.idBuffer.get(0);
				if(path.startsWith("##")) {
					this.load(this.rescale(this.method_1_1196(visualsClass2.getResourceAsStream(path.substring(2)))), i4);
				} else if(path.startsWith("%%")) {
					this.blur = true;
					this.load(this.method_1_1196(visualsClass2.getResourceAsStream(path.substring(2))), i4);
					this.blur = false;
				} else {
					this.load(this.method_1_1196(visualsClass2.getResourceAsStream(path)), i4);
				}

				this.textureIds.put(path, i4);
				ForgeHooksClient.onTextureLoad(path, i4);
				return i4;
			} catch (IOException iOException5) {
				throw new RuntimeException("!!");
			} catch (IllegalArgumentException illegalArgumentException6) {
				System.err.println("Failed to load resource: " + path);
				throw new RuntimeException(illegalArgumentException6);
			}
		}
	}

	@Overwrite
	public void load(BufferedImage image, int id) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		if(MIPMAP) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, ScreenDebugMenu.conf_disableBilinearFiltering ? GL11.GL_LINEAR : GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, ScreenDebugMenu.conf_disableBilinearFiltering ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		}

		if(this.blur) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}

		int i3 = image.getWidth();
		int i4 = image.getHeight();
		FMLClientHandler.instance().setTextureDimensions(id, i3, i4, this.dynamicTextures);
		int[] i5 = new int[i3 * i4];
		byte[] b6 = new byte[i3 * i4 * 4];
		image.getRGB(0, 0, i3, i4, i5, 0, i3);

		int i7;
		int i8;
		int i9;
		int i10;
		int i11;
		int i12;
		int i13;
		int i14;
		for(i7 = 0; i7 < i5.length; ++i7) {
			i8 = i5[i7] >> 24 & 255;
			i9 = i5[i7] >> 16 & 255;
			i10 = i5[i7] >> 8 & 255;
			i11 = i5[i7] & 255;
			if(this.options != null && this.options.anaglyph) {
				i12 = (i9 * 30 + i10 * 59 + i11 * 11) / 100;
				i13 = (i9 * 30 + i10 * 70) / 100;
				i14 = (i9 * 30 + i11 * 70) / 100;
				i9 = i12;
				i10 = i13;
				i11 = i14;
			}

			b6[i7 * 4 + 0] = (byte)i9;
			b6[i7 * 4 + 1] = (byte)i10;
			b6[i7 * 4 + 2] = (byte)i11;
			b6[i7 * 4 + 3] = (byte)i8;
		}

		this.imageBuffer.clear();
		this.imageBuffer.put(b6);
		this.imageBuffer.position(0).limit(b6.length);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, i3, i4, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageBuffer);
		if(MIPMAP) {
			for(i7 = 1; i7 <= 4; ++i7) {
				i8 = i3 >> i7 - 1;
				i9 = i3 >> i7;
				i10 = i4 >> i7;

				for(i11 = 0; i11 < i9; ++i11) {
					for(i12 = 0; i12 < i10; ++i12) {
						i13 = this.imageBuffer.getInt((i11 * 2 + 0 + (i12 * 2 + 0) * i8) * 4);
						i14 = this.imageBuffer.getInt((i11 * 2 + 1 + (i12 * 2 + 0) * i8) * 4);
						int i15 = this.imageBuffer.getInt((i11 * 2 + 1 + (i12 * 2 + 1) * i8) * 4);
						int i16 = this.imageBuffer.getInt((i11 * 2 + 0 + (i12 * 2 + 1) * i8) * 4);
						int i17 = this.crispBlend(this.crispBlend(i13, i14), this.crispBlend(i15, i16));
						this.imageBuffer.putInt((i11 + i12 * i9) * 4, i17);
					}
				}

				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i7, GL11.GL_RGBA, i9, i10, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageBuffer);
			}
		}

	}

	@Overwrite
	public void addDynamicTexture(DynamicTexture texture) {
		FMLClientHandler.instance().onPreRegisterEffect(texture);
		this.dynamicTextures.add(texture);
		texture.tick();
	}

	@Unique
	private int TextureHeight(DynamicTexture var3) {
		int TextureHeight = 0;
		if(var3.atlas == 0) {
			TextureHeight = 32;
		} else if(var3.atlas == 1) {
			TextureHeight = 16;
		}

		return TextureHeight;
	}

	@Overwrite
	public void tick() {
		if(Minecraft.extendedTextureAtlas) {
			int var1 = -1;

			for(int var2 = 0; var2 < this.dynamicTextures.size(); ++var2) {
				DynamicTexture var3 = (DynamicTexture)this.dynamicTextures.get(var2);
				var3.anaglyph = this.options.anaglyph;
				if(FMLClientHandler.instance().onUpdateTextureEffect(var3)) {
					Dimension dim = FMLClientHandler.instance().getTextureDimensions(var3);
					int tWidth = dim.width / 16;
					int tHeight = dim.height / TextureHeight(var3);
					int tLen = tWidth * tHeight * 4;
					if(var3.pixels.length == tLen) {
						this.imageBuffer.clear();
						this.imageBuffer.put(var3.pixels);
						this.imageBuffer.position(0).limit(var3.pixels.length);
					} else {
						FMLClientHandler.instance().scaleTextureFXData(var3.pixels, this.imageBuffer, tWidth, tLen);
					}

					if(var3.sprite != var1) {
						var3.bind((TextureManager) (Object) this);
						var1 = var3.sprite;
					}

					for(int var4 = 0; var4 < var3.replicate; ++var4) {
						int xOffset = var3.sprite % 16 * tWidth + var4 * tWidth;

						for(int var5 = 0; var5 < var3.replicate; ++var5) {
							int yOffset = var3.sprite / 16 * tHeight + var5 * tHeight;
							GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xOffset, yOffset, tWidth, tHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageBuffer);
						}
					}
				}
			}
		}
	}
}
