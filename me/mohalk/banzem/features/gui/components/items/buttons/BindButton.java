/*    */ package me.mohalk.banzem.features.gui.components.items.buttons;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.gui.PhobosGui;
/*    */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*    */ import me.mohalk.banzem.features.modules.client.HUD;
/*    */ import me.mohalk.banzem.features.setting.Bind;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.ColorUtil;
/*    */ import me.mohalk.banzem.util.MathUtil;
/*    */ import me.mohalk.banzem.util.RenderUtil;
/*    */ import net.minecraft.client.audio.ISound;
/*    */ import net.minecraft.client.audio.PositionedSoundRecord;
/*    */ import net.minecraft.init.SoundEvents;
/*    */ 
/*    */ public class BindButton extends Button {
/*    */   public boolean isListening;
/*    */   private final Setting setting;
/*    */   
/*    */   public BindButton(Setting setting) {
/* 21 */     super(setting.getName());
/* 22 */     this.setting = setting;
/* 23 */     this.width = 15;
/*    */   }
/*    */ 
/*    */   
/*    */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
/* 28 */     if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue()) {
/* 29 */       int color = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/* 30 */       int color1 = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/* 31 */       RenderUtil.drawGradientRect(this.x, this.y, this.width + 7.4F, this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue() : color) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515), getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue() : color1) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
/*    */     } else {
/* 33 */       RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue()).intValue()) : Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByName("ClickGui")).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
/*    */     } 
/* 35 */     if (this.isListening) {
/* 36 */       Banzem.textManager.drawStringWithShadow("Listening...", this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/*    */     } else {
/* 38 */       Banzem.textManager.drawStringWithShadow(this.setting.getName() + " §7" + this.setting.getValue().toString(), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void update() {
/* 44 */     setHidden(!this.setting.isVisible());
/*    */   }
/*    */ 
/*    */   
/*    */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/* 49 */     super.mouseClicked(mouseX, mouseY, mouseButton);
/* 50 */     if (isHovering(mouseX, mouseY)) {
/* 51 */       mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187682_dG, 1.0F));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onKeyTyped(char typedChar, int keyCode) {
/* 57 */     if (this.isListening) {
/* 58 */       Bind bind = new Bind(keyCode);
/* 59 */       if (bind.toString().equalsIgnoreCase("Escape")) {
/*    */         return;
/*    */       }
/* 62 */       if (bind.toString().equalsIgnoreCase("Delete")) {
/* 63 */         bind = new Bind(-1);
/*    */       }
/* 65 */       this.setting.setValue(bind);
/* 66 */       onMouseClick();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeight() {
/* 72 */     return 14;
/*    */   }
/*    */ 
/*    */   
/*    */   public void toggle() {
/* 77 */     this.isListening = !this.isListening;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getState() {
/* 82 */     return !this.isListening;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\buttons\BindButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */