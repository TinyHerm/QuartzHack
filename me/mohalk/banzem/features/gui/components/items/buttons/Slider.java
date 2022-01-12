/*    */ package me.mohalk.banzem.features.gui.components.items.buttons;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.gui.PhobosGui;
/*    */ import me.mohalk.banzem.features.gui.components.Component;
/*    */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*    */ import me.mohalk.banzem.features.modules.client.HUD;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.ColorUtil;
/*    */ import me.mohalk.banzem.util.MathUtil;
/*    */ import me.mohalk.banzem.util.RenderUtil;
/*    */ import org.lwjgl.input.Mouse;
/*    */ 
/*    */ public class Slider
/*    */   extends Button {
/*    */   public Setting setting;
/*    */   private final Number min;
/*    */   private final Number max;
/*    */   private final int difference;
/*    */   
/*    */   public Slider(Setting setting) {
/* 22 */     super(setting.getName());
/* 23 */     this.setting = setting;
/* 24 */     this.min = (Number)setting.getMin();
/* 25 */     this.max = (Number)setting.getMax();
/* 26 */     this.difference = this.max.intValue() - this.min.intValue();
/* 27 */     this.width = 15;
/*    */   }
/*    */ 
/*    */   
/*    */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
/* 32 */     dragSetting(mouseX, mouseY);
/* 33 */     RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, !isHovering(mouseX, mouseY) ? 290805077 : -2007673515);
/* 34 */     if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue()) {
/* 35 */       int color = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/* 36 */       int color1 = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/* 37 */       RenderUtil.drawGradientRect(this.x, this.y, (((Number)this.setting.getValue()).floatValue() <= this.min.floatValue()) ? 0.0F : ((this.width + 7.4F) * partialMultiplier()), this.height - 0.5F, !isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue() : color, !isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue() : color1);
/*    */     } else {
/* 39 */       RenderUtil.drawRect(this.x, this.y, (((Number)this.setting.getValue()).floatValue() <= this.min.floatValue()) ? this.x : (this.x + (this.width + 7.4F) * partialMultiplier()), this.y + this.height - 0.5F, !isHovering(mouseX, mouseY) ? Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
/*    */     } 
/* 41 */     Banzem.textManager.drawStringWithShadow(getName() + " §7" + ((this.setting.getValue() instanceof Float) ? (String)this.setting.getValue() : (String)Double.valueOf(((Number)this.setting.getValue()).doubleValue())), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), -1);
/*    */   }
/*    */ 
/*    */   
/*    */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/* 46 */     super.mouseClicked(mouseX, mouseY, mouseButton);
/* 47 */     if (isHovering(mouseX, mouseY)) {
/* 48 */       setSettingFromX(mouseX);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isHovering(int mouseX, int mouseY) {
/* 54 */     for (Component component : PhobosGui.getClickGui().getComponents()) {
/* 55 */       if (!component.drag)
/* 56 */         continue;  return false;
/*    */     } 
/* 58 */     return (mouseX >= getX() && mouseX <= getX() + getWidth() + 8.0F && mouseY >= getY() && mouseY <= getY() + this.height);
/*    */   }
/*    */ 
/*    */   
/*    */   public void update() {
/* 63 */     setHidden(!this.setting.isVisible());
/*    */   }
/*    */   
/*    */   private void dragSetting(int mouseX, int mouseY) {
/* 67 */     if (isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
/* 68 */       setSettingFromX(mouseX);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeight() {
/* 74 */     return 14;
/*    */   }
/*    */   
/*    */   private void setSettingFromX(int mouseX) {
/* 78 */     float percent = (mouseX - this.x) / (this.width + 7.4F);
/* 79 */     if (this.setting.getValue() instanceof Double) {
/* 80 */       double result = ((Double)this.setting.getMin()).doubleValue() + (this.difference * percent);
/* 81 */       this.setting.setValue(Double.valueOf(Math.round(10.0D * result) / 10.0D));
/* 82 */     } else if (this.setting.getValue() instanceof Float) {
/* 83 */       float result = ((Float)this.setting.getMin()).floatValue() + this.difference * percent;
/* 84 */       this.setting.setValue(Float.valueOf(Math.round(10.0F * result) / 10.0F));
/* 85 */     } else if (this.setting.getValue() instanceof Integer) {
/* 86 */       this.setting.setValue(Integer.valueOf(((Integer)this.setting.getMin()).intValue() + (int)(this.difference * percent)));
/*    */     } 
/*    */   }
/*    */   
/*    */   private float middle() {
/* 91 */     return this.max.floatValue() - this.min.floatValue();
/*    */   }
/*    */   
/*    */   private float part() {
/* 95 */     return ((Number)this.setting.getValue()).floatValue() - this.min.floatValue();
/*    */   }
/*    */   
/*    */   private float partialMultiplier() {
/* 99 */     return part() / middle();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\buttons\Slider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */