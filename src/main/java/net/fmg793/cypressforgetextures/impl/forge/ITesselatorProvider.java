package net.fmg793.cypressforgetextures.impl.forge;

public interface ITesselatorProvider {
	public default int textureID(int i) {
		throw new AbstractMethodError();
	}

	public default boolean renderingWorldRenderer(boolean b) {
		throw new AbstractMethodError();
	}
}
