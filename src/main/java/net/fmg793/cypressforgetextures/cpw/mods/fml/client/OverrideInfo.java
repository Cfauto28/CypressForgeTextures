package net.fmg793.cypressforgetextures.cpw.mods.fml.client;

import net.minecraft.client.render.texture.DynamicTexture;

public class OverrideInfo {
	public String texture;
	public String override;
	public int index;
	public int imageIndex;
	public DynamicTexture textureFX;
	public boolean added;

	public boolean equals(Object obj) {
		try {
			OverrideInfo e = (OverrideInfo)obj;
			return this.index == e.index && this.imageIndex == e.imageIndex;
		} catch (Exception exception3) {
			return false;
		}
	}

	public int hashCode() {
		return this.index + this.imageIndex;
	}
}
