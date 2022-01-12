/*     */ package me.mohalk.banzem.features.gui;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.gui.components.Component;
/*     */ import me.mohalk.banzem.features.gui.components.items.Item;
/*     */ import me.mohalk.banzem.features.gui.components.items.buttons.Button;
/*     */ import me.mohalk.banzem.features.gui.components.items.buttons.ModuleButton;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import net.minecraft.client.gui.GuiScreen;
/*     */ import org.lwjgl.input.Mouse;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PhobosGui
/*     */   extends GuiScreen
/*     */ {
/*     */   private static PhobosGui phobosGui;
/*  20 */   private static PhobosGui INSTANCE = new PhobosGui();
/*     */ 
/*     */   
/*  23 */   private final ArrayList<Component> components = new ArrayList<>();
/*     */   
/*     */   public PhobosGui() {
/*  26 */     setInstance();
/*  27 */     load();
/*     */   }
/*     */   
/*     */   public static PhobosGui getInstance() {
/*  31 */     if (INSTANCE == null) {
/*  32 */       INSTANCE = new PhobosGui();
/*     */     }
/*  34 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   public static PhobosGui getClickGui() {
/*  38 */     return getInstance();
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  42 */     INSTANCE = this;
/*     */   }
/*     */   
/*     */   private void load() {
/*  46 */     int x = -84;
/*  47 */     for (Module.Category category : Banzem.moduleManager.getCategories()) {
/*  48 */       x += 90; this.components.add(new Component(category.getName(), x, 4, true)
/*     */           {
/*     */             public void setupItems()
/*     */             {
/*  52 */               Banzem.moduleManager.getModulesByCategory(category).forEach(module -> {
/*     */                     if (!module.hidden) {
/*     */                       addButton((Button)new ModuleButton(module));
/*     */                     }
/*     */                   });
/*     */             }
/*     */           });
/*     */     } 
/*  60 */     this.components.forEach(components -> components.getItems().sort(()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateModule(Module module) {
/*  65 */     for (Component component : this.components) {
/*  66 */       for (Item item : component.getItems()) {
/*  67 */         if (!(item instanceof ModuleButton))
/*  68 */           continue;  ModuleButton button = (ModuleButton)item;
/*  69 */         Module mod = button.getModule();
/*  70 */         if (module == null || !module.equals(mod))
/*  71 */           continue;  button.initSettings();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
/*  78 */     checkMouseWheel();
/*  79 */     func_146276_q_();
/*  80 */     this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
/*     */   }
/*     */   
/*     */   public void func_73864_a(int mouseX, int mouseY, int clickedButton) {
/*  84 */     this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
/*     */   }
/*     */   
/*     */   public void func_146286_b(int mouseX, int mouseY, int releaseButton) {
/*  88 */     this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
/*     */   }
/*     */   
/*     */   public boolean func_73868_f() {
/*  92 */     return false;
/*     */   }
/*     */   
/*     */   public final ArrayList<Component> getComponents() {
/*  96 */     return this.components;
/*     */   }
/*     */   
/*     */   public void checkMouseWheel() {
/* 100 */     int dWheel = Mouse.getDWheel();
/* 101 */     if (dWheel < 0) {
/* 102 */       this.components.forEach(component -> component.setY(component.getY() - 10));
/* 103 */     } else if (dWheel > 0) {
/* 104 */       this.components.forEach(component -> component.setY(component.getY() + 10));
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getTextOffset() {
/* 109 */     return -6;
/*     */   }
/*     */   
/*     */   public Component getComponentByName(String name) {
/* 113 */     for (Component component : this.components) {
/* 114 */       if (!component.getName().equalsIgnoreCase(name))
/* 115 */         continue;  return component;
/*     */     } 
/* 117 */     return null;
/*     */   }
/*     */   
/*     */   public void func_73869_a(char typedChar, int keyCode) throws IOException {
/* 121 */     super.func_73869_a(typedChar, keyCode);
/* 122 */     this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\PhobosGui.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */