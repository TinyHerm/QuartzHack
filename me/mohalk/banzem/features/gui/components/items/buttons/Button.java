/*    */ package me.mohalk.banzem.features.gui.components.items.buttons;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.gui.PhobosGui;
/*    */ import me.mohalk.banzem.features.gui.components.Component;
/*    */ import me.mohalk.banzem.features.gui.components.items.Item;
/*    */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*    */ import me.mohalk.banzem.util.RenderUtil;
/*    */ import net.minecraft.client.audio.ISound;
/*    */ import net.minecraft.client.audio.PositionedSoundRecord;
/*    */ import net.minecraft.init.SoundEvents;
/*    */ 
/*    */ public class Button extends Item {
/*    */   private boolean state;
/*    */   
/*    */   public Button(String name) {
/* 17 */     super(name);
/* 18 */     this.height = 15;
/*    */   }
/*    */ 
/*    */   
/*    */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
/* 23 */     RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
/* 24 */     Banzem.textManager.drawStringWithShadow(getName(), this.x + 2.3F, this.y - 2.0F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/* 25 */     Banzem.textManager.drawStringWithShadow("+", this.x + this.width - Banzem.textManager.getStringWidth("+"), this.y - 2.0F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/*    */   }
/*    */ 
/*    */   
/*    */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/* 30 */     if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
/* 31 */       onMouseClick();
/*    */     }
/*    */   }
/*    */   
/*    */   public void onMouseClick() {
/* 36 */     this.state = !this.state;
/* 37 */     toggle();
/* 38 */     mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
/*    */   }
/*    */ 
/*    */   
/*    */   public void toggle() {}
/*    */   
/*    */   public boolean getState() {
/* 45 */     return this.state;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeight() {
/* 50 */     return 14;
/*    */   }
/*    */   
/*    */   public boolean isHovering(int mouseX, int mouseY) {
/* 54 */     for (Component component : PhobosGui.getClickGui().getComponents()) {
/* 55 */       if (!component.drag)
/* 56 */         continue;  return false;
/*    */     } 
/* 58 */     return (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + this.height);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\buttons\Button.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */