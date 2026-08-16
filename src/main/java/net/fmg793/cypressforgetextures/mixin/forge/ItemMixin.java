package net.fmg793.cypressforgetextures.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ext.block.ExtBlock;
import net.fmg793.cypressforgetextures.forge.ITextureProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

@Mixin(Item.class)
public class ItemMixin implements ITextureProvider {

	@Unique
    private String currentTexture = "/gui/items.png";

	@Unique
	private boolean isDefaultTexture = true;

	@Override
	public String currentTexture() {
		return currentTexture;
	}

	@Override
	public boolean isDefaultTexture() {
		return isDefaultTexture;
	}

	@Override
	public String getTextureFile() {
		return (Item) (Object) this instanceof BlockItem ? ExtBlock.BY_ID[((BlockItem) ((Item) (Object) this)).getBlockID()].getTextureFile() : this.currentTexture;
	}

	@Override
	public Item setTextureFile(String texture) {
		this.currentTexture = texture;
		this.isDefaultTexture = false;
		return (Item) (Object) this;
	}
}
