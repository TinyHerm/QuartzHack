/*     */ package me.mohalk.banzem.features.gui.components.items.buttons;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.gui.PhobosGui;
/*     */ import me.mohalk.banzem.features.gui.components.items.Item;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.audio.ISound;
/*     */ import net.minecraft.client.audio.PositionedSoundRecord;
/*     */ import net.minecraft.init.SoundEvents;
/*     */ 
/*     */ public class ModuleButton
/*     */   extends Button
/*     */ {
/*     */   private final Module module;
/*  20 */   private List<Item> items = new ArrayList<>();
/*     */   private boolean subOpen;
/*     */   
/*     */   public ModuleButton(Module module) {
/*  24 */     super(module.getName());
/*  25 */     this.module = module;
/*  26 */     initSettings();
/*     */   }
/*     */   
/*     */   public void initSettings() {
/*  30 */     ArrayList<Item> newItems = new ArrayList<>();
/*  31 */     if (!this.module.getSettings().isEmpty()) {
/*  32 */       for (Setting setting : this.module.getSettings()) {
/*  33 */         if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
/*  34 */           newItems.add(new BooleanButton(setting));
/*     */         }
/*  36 */         if (setting.getValue() instanceof me.mohalk.banzem.features.setting.Bind && !this.module.getName().equalsIgnoreCase("Hud")) {
/*  37 */           newItems.add(new BindButton(setting));
/*     */         }
/*  39 */         if (setting.getValue() instanceof String || setting.getValue() instanceof Character) {
/*  40 */           newItems.add(new StringButton(setting));
/*     */         }
/*  42 */         if (setting.isNumberSetting()) {
/*  43 */           if (setting.hasRestriction()) {
/*  44 */             newItems.add(new Slider(setting));
/*     */             continue;
/*     */           } 
/*  47 */           newItems.add(new UnlimitedSlider(setting));
/*     */         } 
/*  49 */         if (!setting.isEnumSetting())
/*  50 */           continue;  newItems.add(new EnumButton(setting));
/*     */       } 
/*     */     }
/*  53 */     this.items = newItems;
/*     */   }
/*     */ 
/*     */   
/*     */   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
/*  58 */     super.drawScreen(mouseX, mouseY, partialTicks);
/*  59 */     if (!this.items.isEmpty()) {
/*  60 */       ClickGui gui = (ClickGui)Banzem.moduleManager.getModuleByClass(ClickGui.class);
/*  61 */       Banzem.textManager.drawStringWithShadow(((Boolean)gui.openCloseChange.getValue()).booleanValue() ? (this.subOpen ? (String)gui.close.getValue() : (String)gui.open.getValue()) : (String)gui.moduleButton.getValue(), this.x - 1.5F + this.width - 7.4F, this.y - 2.0F - PhobosGui.getClickGui().getTextOffset(), -1);
/*  62 */       if (this.subOpen) {
/*  63 */         float height = 1.0F;
/*  64 */         for (Item item : this.items) {
/*  65 */           if (!item.isHidden()) {
/*  66 */             item.setLocation(this.x + 1.0F, this.y + (height += 15.0F));
/*  67 */             item.setHeight(15);
/*  68 */             item.setWidth(this.width - 9);
/*  69 */             item.drawScreen(mouseX, mouseY, partialTicks);
/*     */           } 
/*  71 */           item.update();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
/*  79 */     super.mouseClicked(mouseX, mouseY, mouseButton);
/*  80 */     if (!this.items.isEmpty()) {
/*  81 */       if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
/*  82 */         this.subOpen = !this.subOpen;
/*  83 */         Util.mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187682_dG, 1.0F));
/*     */       } 
/*  85 */       if (this.subOpen) {
/*  86 */         for (Item item : this.items) {
/*  87 */           if (item.isHidden())
/*  88 */             continue;  item.mouseClicked(mouseX, mouseY, mouseButton);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onKeyTyped(char typedChar, int keyCode) {
/*  96 */     super.onKeyTyped(typedChar, keyCode);
/*  97 */     if (!this.items.isEmpty() && this.subOpen) {
/*  98 */       for (Item item : this.items) {
/*  99 */         if (item.isHidden())
/* 100 */           continue;  item.onKeyTyped(typedChar, keyCode);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 107 */     if (this.subOpen) {
/* 108 */       int height = 14;
/* 109 */       for (Item item : this.items) {
/* 110 */         if (item.isHidden())
/* 111 */           continue;  height += item.getHeight() + 1;
/*     */       } 
/* 113 */       return height + 2;
/*     */     } 
/* 115 */     return 14;
/*     */   }
/*     */   
/*     */   public Module getModule() {
/* 119 */     return this.module;
/*     */   }
/*     */ 
/*     */   
/*     */   public void toggle() {
/* 124 */     this.module.toggle();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getState() {
/* 129 */     return this.module.isEnabled();
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\components\items\buttons\ModuleButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */