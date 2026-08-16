package net.fmg793.cypressforgetextures.forge;

import ext.block.ExtBlock;
import ext.client.InputHandler;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MinecraftForgeClient {
	private static IItemRenderer[] customItemRenderers = new IItemRenderer[Item.BY_ID.length];

	public static void registerTextureLoadHandler(ITextureLoadHandler handler) {
		ForgeHooksClient.textureLoadHandlers.add(handler);
	}

	public static void bindTexture(String texture, int subid) {
		ForgeHooksClient.bindTexture(texture, subid);
	}

	public static void bindTexture(String texture) {
		ForgeHooksClient.bindTexture(texture, 0);
	}

	public static void unbindTexture() {
		ForgeHooksClient.unbindTexture();
	}

	public static void preloadTexture(String texture) {
		InputHandler.minecraft.textureManager.load(texture);
	}

	public static void renderBlock(BlockRenderer render, ExtBlock block, int X, int Y, int Z) {
		ForgeHooksClient.beforeBlockRender(block, render);
		render.method_1_2004(block, X, Y, Z);
		ForgeHooksClient.afterBlockRender(block, render);
	}

	public static int getRenderPass() {
		return ForgeHooksClient.renderPass;
	}

	public static void registerItemRenderer(int itemID, IItemRenderer renderer) {
		customItemRenderers[itemID] = renderer;
	}

	public static IItemRenderer getItemRenderer(ItemStack item, IItemRenderer.ItemRenderType type) {
		IItemRenderer renderer = customItemRenderers[item.id];
		return renderer != null && renderer.handleRenderType(item, type) ? customItemRenderers[item.id] : null;
	}
}
