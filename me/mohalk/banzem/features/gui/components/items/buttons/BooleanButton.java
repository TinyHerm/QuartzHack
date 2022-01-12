/*    */ package me.mohalk.banzem.features.gui.components.items.buttons;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.gui.PhobosGui;
/*    */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*    */ import me.mohalk.banzem.features.modules.client.HUD;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.ColorUtil;
/*    */ import me.mohalk.banzem.util.MathUtil;
/*    */ import me.mohalk.banzem.util.RenderUtil;
/*    */ import me.mohalk.banzem.util.Util;
/*    */ import net.minecraft.client.audio.ISound;
/*    */ import net.minecraft.client.audio.PositionedSoundRecord;
/*    */ import net.minecraft.init.SoundEvents;
/*    */ 
/*    */ public class BooleanButton extends Button {
/*    */   private final Setting setting;
/*    */   
/*    */   public BooleanButton(Setting setting) {
/* 20 */     super(setting.getName());
/* 21 */     this.setting = setting;
/* 22 */     this.width = 15;
/*    */   }
/*    */ 
/*    */   
/*    */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
/* 27 */     if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue()) {
/* 28 */       int color = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/* 29 */       int color1 = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/* 30 */       RenderUtil.drawGradientRect(this.x, this.y, this.width + 7.4F, this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue() : color) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515), getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue() : color1) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
/*    */     } else {
/* 32 */       RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
/*    */     } 
/* 34 */     Banzem.textManager.drawStringWithShadow(getName(), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/*    */   }
/*    */ 
/*    */   
/*    */   public void update() {
/* 39 */     setHidden(!this.setting.isVisible());
/*    */   }
/*    */ 
/*    */   
/*    */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/* 44 */     super.mouseClicked(mouseX, mouseY, mouseButton);
/* 45 */     if (isHovering(mouseX, mouseY)) {
/* 46 */       Util.mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187682_dG, 1.0F));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeight() {
/* 52 */     return 14;
/*    */   }
/*    */ 
/*    */   
/*    */   public void toggle() {
/* 57 */     this.setting.setValue(Boolean.valueOf(!((Boolean)this.setting.getValue()).booleanValue()));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getState() {
/* 62 */     return ((Boolean)this.setting.getValue()).booleanValue();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\buttons\BooleanButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */