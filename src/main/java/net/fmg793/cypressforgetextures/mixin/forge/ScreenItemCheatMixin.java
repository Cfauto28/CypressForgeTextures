package net.fmg793.cypressforgetextures.mixin.forge;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ext.block.ExtBlock;
import ext.client.InputHandler;
import ext.client.gui.screen.cheat.ButtonItemCheat;
import ext.client.gui.screen.cheat.ScreenItemCheat;
import ext.client.gui.widget.ButtonSelect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(ScreenItemCheat.class)
public abstract class ScreenItemCheatMixin extends Screen {

	@Shadow
	public Minecraft minecraft;

	@Shadow
	private BlockRenderer blockRendererBlurryTroll;

	@Shadow
	private boolean createButtons;

	@Shadow
	private int lastCols;

	@Shadow
	private int lastRows;

	@Shadow
	private int lastNOfPages;

	@Shadow
	private long rotateTimer;

	@Shadow
	private boolean selectingBlocks;

	@Shadow
	private boolean resetButtons;

	@Shadow
	private int currentPage;

	@Shadow
	public abstract void method_1_2267(int i1, int i2, int i3, int i4, int i5, int i6, float f7);

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void method_1_2265(ExtBlock block) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(ExtBlock.BY_ID[new ItemStack(block).id].getTextureFile()));
		int i2 = block.getSprite(0, 0);
		this.method_1_2267(0, 0, i2 % 16 * 16, i2 / 16 * 16, 16, 16, 512.0F);
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void RenderItem(Item item) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(Item.BY_ID[new ItemStack(item).id].getTextureFile()));
		int i2 = item.getSprite(new ItemStack(item));
		this.method_1_2267(0, 0, i2 % 16 * 16, i2 / 16 * 16, 16, 16, 256.0F);
	}

	@Shadow
	public abstract int nthExistingBlock(int i1);

	@Shadow
	public abstract int nOfExistingBlocks();

	@Shadow
	public abstract int nthExistingItem(int i1);

	@Shadow
	public abstract int nOfExistingItems();

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void buttonClicked(ButtonWidget button) {
		if(button.active) {
			if(button.id == 2) {
				this.resetButtons = true;
				this.selectingBlocks = true;
			} else if(button.id == 3) {
				this.resetButtons = true;
				this.selectingBlocks = false;
			} else if(button.id == 4) {
				this.currentPage = this.currentPage == 0 ? this.lastNOfPages - 1 : --this.currentPage;
				this.resetButtons = true;
			} else if(button.id == 5) {
				++this.currentPage;
				this.currentPage %= this.lastNOfPages;
				this.resetButtons = true;
			}

			ItemStack itemStack2 = null;
			if(button.id >= 65534) {
				itemStack2 = new ItemStack(Item.BY_ID[button.id - 65534], 1);
			} else if(button.id >= 32767) {
				itemStack2 = new ItemStack(ExtBlock.BY_ID[button.id - 32767], 64);
			}

			if(itemStack2 != null) {
				if(InputHandler.field_1_1691) {
					this.minecraft.player.inventory.addItem(itemStack2);
				} else {
					this.minecraft.player.dropItem(itemStack2, true);
				}
			}

		}
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void render(int mouseX, int mouseY, float tickDelta) {
		if(this.rotateTimer == -1L) {
			this.rotateTimer = System.currentTimeMillis();
		}

		float f4 = Math.min((float)(System.currentTimeMillis() - this.rotateTimer) / 100.0F, 1.0F);
		fillGradient(0, (int)((float)this.height * (1.0F - f4)), this.width, this.height, 1614823488, 1612718112);
		this.drawCenteredString(this.textRenderer, "Palette", this.width / 2, 15, 0xFFFFFF);
		this.drawCenteredString(this.textRenderer, "" + this.currentPage, 208, 35, 0xFFFFFF);
		int i5 = (this.width - 18) / 32;
		int i6 = (this.height - 50) / 23;
		if(i5 != 0 && i6 != 0) {
			int i7 = (this.selectingBlocks ? this.nOfExistingBlocks() : this.nOfExistingItems()) / (i5 * i6) + 1;
			if(this.lastCols != i5 || this.lastRows != i6 || this.resetButtons) {
				this.lastNOfPages = i7;
				this.lastCols = i5;

				for(this.lastRows = i6; i7 <= this.currentPage; --this.currentPage) {
				}

				this.buttons.clear();
				this.createButtons = true;
				this.resetButtons = false;
			}

			if(this.createButtons) {
				this.buttons.add(new ButtonSelect(2, 18, 30, "Blocks"));
				this.buttons.add(new ButtonSelect(3, 68, 30, "Items"));
				this.buttons.add(new ButtonSelect(4, 148, 30, "<<"));
				this.buttons.add(new ButtonSelect(5, 218, 30, ">>"));
				((ButtonWidget)this.buttons.get(this.selectingBlocks ? 0 : 1)).active = false;
				((ButtonWidget)this.buttons.get(3)).active = i7 != 1;
				((ButtonWidget)this.buttons.get(2)).active = ((ButtonWidget)this.buttons.get(3)).active;
			}

			int i8 = this.currentPage * i5 * i6;

			for(int i9 = 0; i9 != i5 * i6; ++i9) {
				float f10;
				int i12;
				if(this.selectingBlocks) {
					i12 = this.nthExistingBlock(i8 + i9);
					if(i12 == -1) {
						break;
					}

					ExtBlock extBlock11 = ExtBlock.BY_ID[i12];
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(ExtBlock.BY_ID[new ItemStack(extBlock11).id].getTextureFile()));
					boolean z13 = BlockRenderer.isItem3d(((ExtBlock)extBlock11).getRenderType()) || ((ExtBlock)extBlock11).getRenderType() == 1 || ((ExtBlock)extBlock11).getRenderType() == 2 || ((ExtBlock)extBlock11).getRenderType() == 14;
					f10 = 18.0F + 32.0F * (float)(i9 % i5);
					float f14 = 50.0F + 23.0F * (float)(i9 / i5);
					if(this.createButtons) {
						this.buttons.add(new ButtonItemCheat(32767 + i12, (int)f10, (int)f14, "", i12, this));
					}

					GL11.glPushMatrix();
					if(z13) {
						GL11.glTranslatef(f10 + 14.0F, f14 + 8.0F, 16.0F);
						GL11.glScalef(16.0F, 16.0F, 16.0F);
						GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
						GL11.glRotatef(30.0F, -1.0F, -1.0F, 0.0F);
						float f15 = (float)(System.currentTimeMillis() - this.rotateTimer) / 13000.0F * 360.0F;
						GL11.glRotatef(f15, 0.0F, -1.0F, 0.0F);
						this.blockRendererBlurryTroll.method_1_1996((ExtBlock)extBlock11);
					} else {
						GL11.glTranslatef(f10 + 5.0F, f14, 13.0F);
						this.method_1_2265((ExtBlock)extBlock11);
					}

					GL11.glPopMatrix();
				} else {
					i12 = this.nthExistingItem(i8 + i9);
					if(i12 == -1) {
						break;
					}

					Item item16 = Item.BY_ID[i12];
					float f17 = 18.0F + 32.0F * (float)(i9 % i5);
					f10 = 50.0F + 23.0F * (float)(i9 / i5);
					if(this.createButtons) {
						this.buttons.add(new ButtonItemCheat(65534 + i12, (int)f17, (int)f10, "", i12, this));
					}

					GL11.glPushMatrix();
					GL11.glTranslatef(f17 + 5.0F, f10, 13.0F);
					this.RenderItem((Item)item16);
					GL11.glPopMatrix();
				}
			}

			this.createButtons = false;
			super.render(mouseX, mouseY, tickDelta);
		}
	}
}
