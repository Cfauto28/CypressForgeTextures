package net.fmg793.cypressforgetextures.mixin.forge;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ext.block.ExtBlock;
import net.fmg793.cypressforgetextures.forge.IItemRenderer;
import net.fmg793.cypressforgetextures.forge.MinecraftForgeClient;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin extends MobRenderer {

	@Shadow
	private HumanoidModel player;

	public PlayerRendererMixin(Model model, float shadowSize) {
		super(new HumanoidModel(0.0F), 0.5F);
	}

	@Overwrite
	public void renderMore(PlayerEntity playerEntity1, float f2) {
		ItemStack itemStack3 = playerEntity1.inventory.getSelectedItem();
		if(itemStack3 != null) {
			GL11.glPushMatrix();
			this.player.rightArm.method_1_1108(0.0625F, true);
			GL11.glTranslatef(this.player.field_1_3184 ? 0.0F : -0.0625F, 0.4375F, 0.0625F);
			float f4;
			IItemRenderer iItemRenderer26 = MinecraftForgeClient.getItemRenderer(itemStack3, IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = iItemRenderer26 != null && iItemRenderer26.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, itemStack3, IItemRenderer.ItemRendererHelper.BLOCK_3D);
			if(itemStack3.getItem() instanceof BlockItem && (is3D || BlockRenderer.isItem3d(ExtBlock.BY_ID[itemStack3.id].getRenderType()))) {
				f4 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f4 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
			} else if(Item.BY_ID[itemStack3.id].isHandheld()) {
				f4 = 0.625F;
				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				f4 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f4, f4, f4);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.dispatcher.itemInHandRenderer.render(itemStack3);
			GL11.glPopMatrix();
		}

	}
}
