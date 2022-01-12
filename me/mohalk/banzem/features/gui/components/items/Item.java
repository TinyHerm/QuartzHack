/*    */ package me.mohalk.banzem.features.gui.components.items;
/*    */ 
/*    */ import me.mohalk.banzem.features.Feature;
/*    */ 
/*    */ public class Item
/*    */   extends Feature {
/*    */   protected float x;
/*    */   protected float y;
/*    */   protected int width;
/*    */   protected int height;
/*    */   private boolean hidden;
/*    */   
/*    */   public Item(String name) {
/* 14 */     super(name);
/*    */   }
/*    */   
/*    */   public void setLocation(float x, float y) {
/* 18 */     this.x = x;
/* 19 */     this.y = y;
/*    */   }
/*    */ 
/*    */   
/*    */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
/*    */ 
/*    */   
/*    */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}
/*    */ 
/*    */   
/*    */   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {}
/*    */ 
/*    */   
/*    */   public void update() {}
/*    */ 
/*    */   
/*    */   public void onKeyTyped(char typedChar, int keyCode) {}
/*    */   
/*    */   public float getX() {
/* 38 */     return this.x;
/*    */   }
/*    */   
/*    */   public float getY() {
/* 42 */     return this.y;
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 46 */     return this.width;
/*    */   }
/*    */   
/*    */   public void setWidth(int width) {
/* 50 */     this.width = width;
/*    */   }
/*    */   
/*    */   public int getHeight() {
/* 54 */     return this.height;
/*    */   }
/*    */   
/*    */   public void setHeight(int height) {
/* 58 */     this.height = height;
/*    */   }
/*    */   
/*    */   public boolean isHidden() {
/* 62 */     return this.hidden;
/*    */   }
/*    */   
/*    */   public boolean setHidden(boolean hidden) {
/* 66 */     this.hidden = hidden;
/* 67 */     return this.hidden;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\Item.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */