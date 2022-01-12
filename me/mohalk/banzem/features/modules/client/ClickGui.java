/*     */ package me.mohalk.banzem.features.modules.client;
/*     */ 
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.ClientEvent;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.gui.PhobosGui;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.gui.GuiScreen;
/*     */ import net.minecraft.client.settings.GameSettings;
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class ClickGui extends Module {
/*  16 */   private static ClickGui INSTANCE = new ClickGui();
/*  17 */   public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
/*  18 */   public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
/*  19 */   public Setting<Boolean> rainbowRolling = register(new Setting("RollingRainbow", Boolean.valueOf(false), v -> (((Boolean)this.colorSync.getValue()).booleanValue() && ((Boolean)Colors.INSTANCE.rainbow.getValue()).booleanValue())));
/*  20 */   public Setting<String> prefix = register((new Setting("Prefix", ".")).setRenderName(true));
/*  21 */   public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  22 */   public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(255)));
/*  23 */   public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  24 */   public Setting<Integer> hoverAlpha = register(new Setting("Alpha", Integer.valueOf(64), Integer.valueOf(0), Integer.valueOf(255)));
/*  25 */   public Setting<Integer> alpha = register(new Setting("HoverAlpha", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255)));
/*  26 */   public Setting<Boolean> customFov = register(new Setting("CustomFov", Boolean.valueOf(false)));
/*  27 */   public Setting<Float> fov = register(new Setting("Fov", Float.valueOf(150.0F), Float.valueOf(-180.0F), Float.valueOf(180.0F), v -> ((Boolean)this.customFov.getValue()).booleanValue()));
/*  28 */   public Setting<Boolean> openCloseChange = register(new Setting("Open/Close", Boolean.valueOf(false)));
/*  29 */   public Setting<String> open = register((new Setting("Open:", "", v -> ((Boolean)this.openCloseChange.getValue()).booleanValue())).setRenderName(true));
/*  30 */   public Setting<String> close = register((new Setting("Close:", "", v -> ((Boolean)this.openCloseChange.getValue()).booleanValue())).setRenderName(true));
/*  31 */   public Setting<String> moduleButton = register((new Setting("Buttons:", "", v -> !((Boolean)this.openCloseChange.getValue()).booleanValue())).setRenderName(true));
/*  32 */   public Setting<Boolean> devSettings = register(new Setting("DevSettings", Boolean.valueOf(true)));
/*  33 */   public Setting<Integer> topRed = register(new Setting("TopRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
/*  34 */   public Setting<Integer> topGreen = register(new Setting("TopGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
/*  35 */   public Setting<Integer> topBlue = register(new Setting("TopBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
/*  36 */   public Setting<Integer> topAlpha = register(new Setting("TopAlpha", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
/*  37 */   public Setting<Boolean> blurEffect = register(new Setting("Blur", Boolean.valueOf(true)));
/*     */   
/*     */   public ClickGui() {
/*  40 */     super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
/*  41 */     setInstance();
/*     */   }
/*     */   
/*     */   public static ClickGui getInstance() {
/*  45 */     if (INSTANCE == null) {
/*  46 */       INSTANCE = new ClickGui();
/*     */     }
/*  48 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  52 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  57 */     if (((Boolean)this.customFov.getValue()).booleanValue()) {
/*  58 */       mc.field_71474_y.func_74304_a(GameSettings.Options.FOV, ((Float)this.fov.getValue()).floatValue());
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onSettingChange(ClientEvent event) {
/*  64 */     if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
/*  65 */       if (event.getSetting().equals(this.prefix)) {
/*  66 */         Banzem.commandManager.setPrefix((String)this.prefix.getPlannedValue());
/*  67 */         Command.sendMessage("Prefix set to §a" + Banzem.commandManager.getPrefix());
/*     */       } 
/*  69 */       Banzem.colorManager.setColor(((Integer)this.red.getPlannedValue()).intValue(), ((Integer)this.green.getPlannedValue()).intValue(), ((Integer)this.blue.getPlannedValue()).intValue(), ((Integer)this.hoverAlpha.getPlannedValue()).intValue());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  76 */     mc.func_147108_a((GuiScreen)new PhobosGui());
/*  77 */     if (((Boolean)this.blurEffect.getValue()).booleanValue()) {
/*  78 */       mc.field_71460_t.func_175069_a(new ResourceLocation("shaders/post/blur.json"));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLoad() {
/*  84 */     if (((Boolean)this.colorSync.getValue()).booleanValue()) {
/*  85 */       Banzem.colorManager.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), ((Integer)this.hoverAlpha.getValue()).intValue());
/*     */     } else {
/*  87 */       Banzem.colorManager.setColor(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.hoverAlpha.getValue()).intValue());
/*     */     } 
/*  89 */     Banzem.commandManager.setPrefix((String)this.prefix.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onTick() {
/*  95 */     if (!(mc.field_71462_r instanceof PhobosGui)) {
/*  96 */       disable();
/*  97 */       if (mc.field_71460_t.func_147706_e() != null) {
/*  98 */         mc.field_71460_t.func_147706_e().func_148021_a();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void onDisable() {
/* 104 */     if (mc.field_71462_r instanceof PhobosGui)
/* 105 */       Util.mc.func_147108_a(null); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\ClickGui.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */