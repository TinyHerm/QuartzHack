/*     */ package me.mohalk.banzem.features.gui.components;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.ArrayList;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.gui.PhobosGui;
/*     */ import me.mohalk.banzem.features.gui.components.items.Item;
/*     */ import me.mohalk.banzem.features.gui.components.items.buttons.Button;
/*     */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*     */ import me.mohalk.banzem.features.modules.client.Colors;
/*     */ import me.mohalk.banzem.features.modules.client.HUD;
/*     */ import me.mohalk.banzem.util.ColorUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.audio.ISound;
/*     */ import net.minecraft.client.audio.PositionedSoundRecord;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.init.SoundEvents;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ public class Component
/*     */   extends Feature {
/*  25 */   private final ArrayList<Item> items = new ArrayList<>();
/*     */   public boolean drag;
/*     */   private int x;
/*     */   private int y;
/*     */   private int x2;
/*     */   private int y2;
/*     */   private int width;
/*     */   private int height;
/*     */   private boolean open;
/*     */   private boolean hidden = false;
/*     */   
/*     */   public Component(String name, int x, int y, boolean open) {
/*  37 */     super(name);
/*  38 */     this.x = x;
/*  39 */     this.y = y;
/*  40 */     this.width = 88;
/*  41 */     this.height = 18;
/*  42 */     this.open = open;
/*  43 */     setupItems();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setupItems() {}
/*     */   
/*     */   private void drag(int mouseX, int mouseY) {
/*  50 */     if (!this.drag) {
/*     */       return;
/*     */     }
/*  53 */     this.x = this.x2 + mouseX;
/*  54 */     this.y = this.y2 + mouseY;
/*     */   }
/*     */   
/*     */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
/*  58 */     drag(mouseX, mouseY);
/*  59 */     float totalItemHeight = this.open ? (getTotalItemHeight() - 2.0F) : 0.0F;
/*  60 */     int color = -7829368;
/*  61 */     if (((Boolean)(ClickGui.getInstance()).devSettings.getValue()).booleanValue()) {
/*  62 */       int i = color = ((Boolean)(ClickGui.getInstance()).colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toARGB(((Integer)(ClickGui.getInstance()).topRed.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topGreen.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topBlue.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topAlpha.getValue()).intValue());
/*     */     }
/*  64 */     if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue() && ((Boolean)(ClickGui.getInstance()).colorSync.getValue()).booleanValue() && ((Boolean)Colors.INSTANCE.rainbow.getValue()).booleanValue()) {
/*  65 */       RenderUtil.drawGradientRect(this.x, this.y - 1.5F, this.width, (this.height - 4), ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp(this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp(this.y + this.height - 4, 0, this.renderer.scaledHeight)))).intValue());
/*     */     } else {
/*  67 */       RenderUtil.drawRect(this.x, this.y - 1.5F, (this.x + this.width), (this.y + this.height - 6), color);
/*     */     } 
/*  69 */     if (this.open) {
/*  70 */       RenderUtil.drawRect(this.x, this.y + 12.5F, (this.x + this.width), (this.y + this.height) + totalItemHeight, 1996488704);
/*  71 */       if (((Boolean)(ClickGui.getInstance()).outline.getValue()).booleanValue()) {
/*  72 */         if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue()) {
/*  73 */           GlStateManager.func_179090_x();
/*  74 */           GlStateManager.func_179147_l();
/*  75 */           GlStateManager.func_179118_c();
/*  76 */           GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
/*  77 */           GlStateManager.func_179103_j(7425);
/*  78 */           GL11.glBegin(1);
/*  79 */           Color currentColor = new Color(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp(this.y, 0, this.renderer.scaledHeight)))).intValue());
/*  80 */           GL11.glColor4f(currentColor.getRed() / 255.0F, currentColor.getGreen() / 255.0F, currentColor.getBlue() / 255.0F, currentColor.getAlpha() / 255.0F);
/*  81 */           GL11.glVertex3f((this.x + this.width), this.y - 1.5F, 0.0F);
/*  82 */           GL11.glVertex3f(this.x, this.y - 1.5F, 0.0F);
/*  83 */           GL11.glVertex3f(this.x, this.y - 1.5F, 0.0F);
/*  84 */           float currentHeight = getHeight() - 1.5F;
/*  85 */           for (Item item : getItems()) {
/*  86 */             currentColor = new Color(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)(this.y + (currentHeight += item.getHeight() + 1.5F)), 0, this.renderer.scaledHeight)))).intValue());
/*  87 */             GL11.glColor4f(currentColor.getRed() / 255.0F, currentColor.getGreen() / 255.0F, currentColor.getBlue() / 255.0F, currentColor.getAlpha() / 255.0F);
/*  88 */             GL11.glVertex3f(this.x, this.y + currentHeight, 0.0F);
/*  89 */             GL11.glVertex3f(this.x, this.y + currentHeight, 0.0F);
/*     */           } 
/*  91 */           currentColor = new Color(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)((this.y + this.height) + totalItemHeight), 0, this.renderer.scaledHeight)))).intValue());
/*  92 */           GL11.glColor4f(currentColor.getRed() / 255.0F, currentColor.getGreen() / 255.0F, currentColor.getBlue() / 255.0F, currentColor.getAlpha() / 255.0F);
/*  93 */           GL11.glVertex3f((this.x + this.width), (this.y + this.height) + totalItemHeight, 0.0F);
/*  94 */           GL11.glVertex3f((this.x + this.width), (this.y + this.height) + totalItemHeight, 0.0F);
/*  95 */           for (Item item : getItems()) {
/*  96 */             currentColor = new Color(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)(this.y + (currentHeight -= item.getHeight() + 1.5F)), 0, this.renderer.scaledHeight)))).intValue());
/*  97 */             GL11.glColor4f(currentColor.getRed() / 255.0F, currentColor.getGreen() / 255.0F, currentColor.getBlue() / 255.0F, currentColor.getAlpha() / 255.0F);
/*  98 */             GL11.glVertex3f((this.x + this.width), this.y + currentHeight, 0.0F);
/*  99 */             GL11.glVertex3f((this.x + this.width), this.y + currentHeight, 0.0F);
/*     */           } 
/* 101 */           GL11.glVertex3f((this.x + this.width), this.y, 0.0F);
/* 102 */           GL11.glEnd();
/* 103 */           GlStateManager.func_179103_j(7424);
/* 104 */           GlStateManager.func_179084_k();
/* 105 */           GlStateManager.func_179141_d();
/* 106 */           GlStateManager.func_179098_w();
/*     */         } else {
/* 108 */           GlStateManager.func_179090_x();
/* 109 */           GlStateManager.func_179147_l();
/* 110 */           GlStateManager.func_179118_c();
/* 111 */           GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
/* 112 */           GlStateManager.func_179103_j(7425);
/* 113 */           GL11.glBegin(2);
/* 114 */           Color outlineColor = ((Boolean)(ClickGui.getInstance()).colorSync.getValue()).booleanValue() ? new Color(Colors.INSTANCE.getCurrentColorHex()) : new Color(Banzem.colorManager.getColorAsIntFullAlpha());
/* 115 */           GL11.glColor4f(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), outlineColor.getAlpha());
/* 116 */           GL11.glVertex3f(this.x, this.y - 1.5F, 0.0F);
/* 117 */           GL11.glVertex3f((this.x + this.width), this.y - 1.5F, 0.0F);
/* 118 */           GL11.glVertex3f((this.x + this.width), (this.y + this.height) + totalItemHeight, 0.0F);
/* 119 */           GL11.glVertex3f(this.x, (this.y + this.height) + totalItemHeight, 0.0F);
/* 120 */           GL11.glEnd();
/* 121 */           GlStateManager.func_179103_j(7424);
/* 122 */           GlStateManager.func_179084_k();
/* 123 */           GlStateManager.func_179141_d();
/* 124 */           GlStateManager.func_179098_w();
/*     */         } 
/*     */       }
/*     */     } 
/* 128 */     Banzem.textManager.drawStringWithShadow(getName(), this.x + 3.0F, this.y - 4.0F - PhobosGui.getClickGui().getTextOffset(), -1);
/* 129 */     if (this.open) {
/* 130 */       float y = (getY() + getHeight()) - 3.0F;
/* 131 */       for (Item item : getItems()) {
/* 132 */         if (item.isHidden())
/* 133 */           continue;  item.setLocation(this.x + 2.0F, y);
/* 134 */         item.setWidth(getWidth() - 4);
/* 135 */         item.drawScreen(mouseX, mouseY, partialTicks);
/* 136 */         y += item.getHeight() + 1.5F;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/* 142 */     if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
/* 143 */       this.x2 = this.x - mouseX;
/* 144 */       this.y2 = this.y - mouseY;
/* 145 */       PhobosGui.getClickGui().getComponents().forEach(component -> {
/*     */             if (component.drag) {
/*     */               component.drag = false;
/*     */             }
/*     */           });
/* 150 */       this.drag = true;
/*     */       return;
/*     */     } 
/* 153 */     if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
/* 154 */       this.open = !this.open;
/* 155 */       Util.mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187682_dG, 1.0F));
/*     */       return;
/*     */     } 
/* 158 */     if (!this.open) {
/*     */       return;
/*     */     }
/* 161 */     getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
/*     */   }
/*     */   
/*     */   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
/* 165 */     if (releaseButton == 0) {
/* 166 */       this.drag = false;
/*     */     }
/* 168 */     if (!this.open) {
/*     */       return;
/*     */     }
/* 171 */     getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
/*     */   }
/*     */   
/*     */   public void onKeyTyped(char typedChar, int keyCode) {
/* 175 */     if (!this.open) {
/*     */       return;
/*     */     }
/* 178 */     getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
/*     */   }
/*     */   
/*     */   public void addButton(Button button) {
/* 182 */     this.items.add(button);
/*     */   }
/*     */   
/*     */   public int getX() {
/* 186 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(int x) {
/* 190 */     this.x = x;
/*     */   }
/*     */   
/*     */   public int getY() {
/* 194 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(int y) {
/* 198 */     this.y = y;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 202 */     return this.width;
/*     */   }
/*     */   
/*     */   public void setWidth(int width) {
/* 206 */     this.width = width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 210 */     return this.height;
/*     */   }
/*     */   
/*     */   public void setHeight(int height) {
/* 214 */     this.height = height;
/*     */   }
/*     */   
/*     */   public boolean isHidden() {
/* 218 */     return this.hidden;
/*     */   }
/*     */   
/*     */   public void setHidden(boolean hidden) {
/* 222 */     this.hidden = hidden;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 226 */     return this.open;
/*     */   }
/*     */   
/*     */   public final ArrayList<Item> getItems() {
/* 230 */     return this.items;
/*     */   }
/*     */   
/*     */   private boolean isHovering(int mouseX, int mouseY) {
/* 234 */     return (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (this.open ? 2 : 0));
/*     */   }
/*     */   
/*     */   private float getTotalItemHeight() {
/* 238 */     float height = 0.0F;
/* 239 */     for (Item item : getItems()) {
/* 240 */       height += item.getHeight() + 1.5F;
/*     */     }
/* 242 */     return height;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\Component.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */