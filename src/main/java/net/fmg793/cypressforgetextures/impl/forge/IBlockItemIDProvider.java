package net.fmg793.cypressforgetextures.impl.forge;

public interface IBlockItemIDProvider {
	public default int getBlockID() {
		throw new AbstractMethodError();
	}
}
