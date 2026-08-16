package net.fmg793.cypressforgetextures.forge;

public interface ITextureProvider {

	public default String currentTexture() {
		throw new AbstractMethodError();
	}

	public default boolean isDefaultTexture() {
		throw new AbstractMethodError();
	}

	public default String getTextureFile() {
		throw new AbstractMethodError();
	}

	public default <T> T setTextureFile(String tex) {
		throw new AbstractMethodError();
	}
}
