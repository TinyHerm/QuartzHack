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
/*    */ public class UnlimitedSlider extends Button {
/*    */   public Setting setting;
/*    */   
/*    */   public UnlimitedSlider(Setting setting) {
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
/* 30 */       RenderUtil.drawGradientRect((int)this.x, (int)this.y, this.width + 7.4F, this.height, color, color1);
/*    */     } else {
/* 32 */       RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, !isHovering(mouseX, mouseY) ? Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
/*    */     } 
/* 34 */     Banzem.textManager.drawStringWithShadow(" - " + this.setting.getName() + " §7" + this.setting.getValue() + "§r +", this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/*    */   }
/*    */ 
/*    */   
/*    */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/* 39 */     super.mouseClicked(mouseX, mouseY, mouseButton);
/* 40 */     if (isHovering(mouseX, mouseY)) {
/* 41 */       Util.mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187682_dG, 1.0F));
/* 42 */       if (isRight(mouseX)) {
/* 43 */         if (this.setting.getValue() instanceof Double) {
/* 44 */           this.setting.setValue(Double.valueOf(((Double)this.setting.getValue()).doubleValue() + 1.0D));
/* 45 */         } else if (this.setting.getValue() instanceof Float) {
/* 46 */           this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() + 1.0F));
/* 47 */         } else if (this.setting.getValue() instanceof Integer) {
/* 48 */           this.setting.setValue(Integer.valueOf(((Integer)this.setting.getValue()).intValue() + 1));
/*    */         } 
/* 50 */       } else if (this.setting.getValue() instanceof Double) {
/* 51 */         this.setting.setValue(Double.valueOf(((Double)this.setting.getValue()).doubleValue() - 1.0D));
/* 52 */       } else if (this.setting.getValue() instanceof Float) {
/* 53 */         this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() - 1.0F));
/* 54 */       } else if (this.setting.getValue() instanceof Integer) {
/* 55 */         this.setting.setValue(Integer.valueOf(((Integer)this.setting.getValue()).intValue() - 1));
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void update() {
/* 62 */     setHidden(!this.setting.isVisible());
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeight() {
/* 67 */     return 14;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void toggle() {}
/*    */ 
/*    */   
/*    */   public boolean getState() {
/* 76 */     return true;
/*    */   }
/*    */   
/*    */   public boolean isRight(int x) {
/* 80 */     return (x > this.x + (this.width + 7.4F) / 2.0F);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\buttons\UnlimitedSlider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */