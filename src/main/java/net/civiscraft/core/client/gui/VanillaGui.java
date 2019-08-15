package net.civiscraft.core.client.gui;

import net.civiscraft.core.CCCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class VanillaGui extends Gui
{
	protected final Minecraft mc;
	protected static final ResourceLocation HOTBAR_TEX_PATH = new ResourceLocation(CCCore.MODID,
			"textures/gui/vanilla/hotbar.png");
	protected final RenderItem itemRenderer;

	public VanillaGui(Minecraft mcIn)
	{
		this.mc = mcIn;
		this.itemRenderer = mcIn.getRenderItem();
	}

	public void renderHealthBar(ScaledResolution sr, float partialTicks)
	{

	}

	public void renderHotbar(ScaledResolution sr, float partialTicks)
	{
		if(this.mc.getRenderViewEntity() instanceof EntityPlayer)
		{
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(HOTBAR_TEX_PATH);
			EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
			ItemStack itemstack = entityplayer.getHeldItemOffhand();
			EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
			int i = sr.getScaledWidth() / 2;
			float f = this.zLevel;
			this.zLevel = -90.0F;

			if(!itemstack.isEmpty())
			{
				int l1 = sr.getScaledHeight() - 16 - 3;

				if(enumhandside == EnumHandSide.LEFT)
				{
					drawModalRectWithCustomSizedTexture(i - 110, sr.getScaledHeight() - 23, 192, 0, 29, 23, 273, 24);
					drawModalRectWithCustomSizedTexture(i - 81, sr.getScaledHeight() - 23, 1, 0, 191, 23, 273, 24);
					drawModalRectWithCustomSizedTexture(i - 81 + entityplayer.inventory.currentItem * 21,
							sr.getScaledHeight() - 23 - 1, 250, 0, 23, 24, 273, 24);

					this.zLevel = f;
					GlStateManager.enableRescaleNormal();
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
							GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
							GlStateManager.DestFactor.ZERO);
					RenderHelper.enableGUIStandardItemLighting();

					for (int l = 0; l < 9; ++l)
					{
						int i1 = i - 78 + l * 21;
						int j1 = sr.getScaledHeight() - 16 - 3;
						this.renderHotbarItem(i1, j1, partialTicks, entityplayer,
								entityplayer.inventory.mainInventory.get(l));
					}

					this.renderHotbarItem(i - 91 - 15, l1, partialTicks, entityplayer, itemstack);
				}

				else
				{
					drawModalRectWithCustomSizedTexture(i - 110, sr.getScaledHeight() - 23, 0, 0, 191, 23, 273, 24);
					drawModalRectWithCustomSizedTexture(i + 81, sr.getScaledHeight() - 23, 221, 0, 29, 23, 273, 24);
					drawModalRectWithCustomSizedTexture(i - 109 + entityplayer.inventory.currentItem * 21,
							sr.getScaledHeight() - 23 - 1, 250, 0, 23, 24, 273, 24);

					this.zLevel = f;
					GlStateManager.enableRescaleNormal();
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
							GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
							GlStateManager.DestFactor.ZERO);
					RenderHelper.enableGUIStandardItemLighting();

					for (int l = 0; l < 9; ++l)
					{
						int i1 = i - 106 + l * 21;
						int j1 = sr.getScaledHeight() - 16 - 3;
						this.renderHotbarItem(i1, j1, partialTicks, entityplayer,
								entityplayer.inventory.mainInventory.get(l));
					}

					this.renderHotbarItem(i + 80 + 10, l1, partialTicks, entityplayer, itemstack);
				}
			}

			else
			{
				drawModalRectWithCustomSizedTexture(i - 96, sr.getScaledHeight() - 23, 0, 0, 192, 23, 273, 24);
				drawModalRectWithCustomSizedTexture(i - 95 + entityplayer.inventory.currentItem * 21,
						sr.getScaledHeight() - 23 - 1, 250, 0, 23, 24, 273, 24);

				this.zLevel = f;
				GlStateManager.enableRescaleNormal();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ZERO);
				RenderHelper.enableGUIStandardItemLighting();

				for (int l = 0; l < 9; ++l)
				{
					int i1 = i - 92 + l * 21;
					int j1 = sr.getScaledHeight() - 16 - 3;
					this.renderHotbarItem(i1, j1, partialTicks, entityplayer,
							entityplayer.inventory.mainInventory.get(l));
				}
			}

			if(this.mc.gameSettings.attackIndicator == 2)
			{
				float f1 = this.mc.player.getCooledAttackStrength(0.0F);

				if(f1 < 1.0F)
				{
					int i2 = sr.getScaledHeight() - 20;
					int j2 = i + 91 + 6;

					if(enumhandside == EnumHandSide.RIGHT)
					{
						j2 = i - 91 - 22;
					}

					this.mc.getTextureManager().bindTexture(Gui.ICONS);
					int k1 = (int) (f1 * 19.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
					this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
				}
			}

			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
		}
	}

	protected void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player,
			ItemStack stack)
	{
		if(!stack.isEmpty())
		{
			float f = (float) stack.getAnimationsToGo() - p_184044_3_;

			if(f > 0.0F)
			{
				GlStateManager.pushMatrix();
				float f1 = 1.0F + f / 5.0F;
				GlStateManager.translate((float) (p_184044_1_ + 8), (float) (p_184044_2_ + 12), 0.0F);
				GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translate((float) (-(p_184044_1_ + 8)), (float) (-(p_184044_2_ + 12)), 0.0F);
			}

			this.itemRenderer.renderItemAndEffectIntoGUI(player, stack, p_184044_1_, p_184044_2_);

			if(f > 0.0F)
			{
				GlStateManager.popMatrix();
			}

			this.itemRenderer.renderItemOverlays(this.mc.fontRenderer, stack, p_184044_1_, p_184044_2_);
		}
	}
}
