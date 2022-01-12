/*     */ package me.mohalk.banzem.features.gui.components.items.buttons;
/*     */ 
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.gui.PhobosGui;
/*     */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*     */ import me.mohalk.banzem.features.modules.client.HUD;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.ColorUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.audio.ISound;
/*     */ import net.minecraft.client.audio.PositionedSoundRecord;
/*     */ import net.minecraft.init.SoundEvents;
/*     */ import net.minecraft.util.ChatAllowedCharacters;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ public class StringButton
/*     */   extends Button {
/*     */   public boolean isListening;
/*     */   private final Setting setting;
/*  24 */   private CurrentString currentString = new CurrentString("");
/*     */   
/*     */   public StringButton(Setting setting) {
/*  27 */     super(setting.getName());
/*  28 */     this.setting = setting;
/*  29 */     this.width = 15;
/*     */   }
/*     */   
/*     */   public static String removeLastChar(String str) {
/*  33 */     String output = "";
/*  34 */     if (str != null && str.length() > 0) {
/*  35 */       output = str.substring(0, str.length() - 1);
/*     */     }
/*  37 */     return output;
/*     */   }
/*     */ 
/*     */   
/*     */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
/*  42 */     if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue()) {
/*  43 */       int color = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/*  44 */       int color1 = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
/*  45 */       RenderUtil.drawGradientRect(this.x, this.y, this.width + 7.4F, this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue() : color) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515), getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue() : color1) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
/*     */     } else {
/*  47 */       RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Banzem.colorManager.getColorWithAlpha(((Integer)((ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
/*     */     } 
/*  49 */     if (this.isListening) {
/*  50 */       Banzem.textManager.drawStringWithShadow(this.currentString.getString() + Banzem.textManager.getIdleSign(), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/*     */     } else {
/*  52 */       Banzem.textManager.drawStringWithShadow((this.setting.shouldRenderName() ? (this.setting.getName() + " §7") : "") + this.setting.getValue(), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/*  58 */     super.mouseClicked(mouseX, mouseY, mouseButton);
/*  59 */     if (isHovering(mouseX, mouseY)) {
/*  60 */       Util.mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187682_dG, 1.0F));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onKeyTyped(char typedChar, int keyCode) {
/*  66 */     if (this.isListening) {
/*  67 */       if (keyCode == 1) {
/*     */         return;
/*     */       }
/*  70 */       if (keyCode == 28) {
/*  71 */         enterString();
/*  72 */       } else if (keyCode == 14) {
/*  73 */         setString(removeLastChar(this.currentString.getString()));
/*  74 */       } else if (keyCode == 47 && (Keyboard.isKeyDown(157) || Keyboard.isKeyDown(29))) {
/*     */         try {
/*  76 */           setString(this.currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
/*  77 */         } catch (Exception e) {
/*  78 */           e.printStackTrace();
/*     */         } 
/*  80 */       } else if (ChatAllowedCharacters.func_71566_a(typedChar)) {
/*  81 */         setString(this.currentString.getString() + typedChar);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void update() {
/*  88 */     setHidden(!this.setting.isVisible());
/*     */   }
/*     */   
/*     */   private void enterString() {
/*  92 */     if (this.currentString.getString().isEmpty()) {
/*  93 */       this.setting.setValue(this.setting.getDefaultValue());
/*     */     } else {
/*  95 */       this.setting.setValue(this.currentString.getString());
/*     */     } 
/*  97 */     setString("");
/*  98 */     onMouseClick();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 103 */     return 14;
/*     */   }
/*     */ 
/*     */   
/*     */   public void toggle() {
/* 108 */     this.isListening = !this.isListening;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getState() {
/* 113 */     return !this.isListening;
/*     */   }
/*     */   
/*     */   public void setString(String newString) {
/* 117 */     this.currentString = new CurrentString(newString);
/*     */   }
/*     */   
/*     */   public static class CurrentString {
/*     */     private final String string;
/*     */     
/*     */     public CurrentString(String string) {
/* 124 */       this.string = string;
/*     */     }
/*     */     
/*     */     public String getString() {
/* 128 */       return this.string;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\buttons\StringButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */