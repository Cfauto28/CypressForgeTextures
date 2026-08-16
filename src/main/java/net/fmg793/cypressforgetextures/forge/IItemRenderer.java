package net.fmg793.cypressforgetextures.forge;

import net.minecraft.item.ItemStack;

public interface IItemRenderer {
	boolean handleRenderType(ItemStack itemStack1, IItemRenderer.ItemRenderType iItemRenderer$ItemRenderType2);

	boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType iItemRenderer$ItemRenderType1, ItemStack itemStack2, IItemRenderer.ItemRendererHelper iItemRenderer$ItemRendererHelper3);

	void renderItem(IItemRenderer.ItemRenderType iItemRenderer$ItemRenderType1, ItemStack itemStack2, Object... object3);

	public static enum ItemRendererHelper {
		ENTITY_ROTATION,
		ENTITY_BOBBING,
		EQUIPPED_BLOCK,
		BLOCK_3D,
		INVENTORY_BLOCK;
	}

	public static enum ItemRenderType {
		ENTITY,
		EQUIPPED,
		INVENTORY,
		FIRST_PERSON_MAP;
	}
}
