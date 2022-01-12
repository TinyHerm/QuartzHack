/*     */ package me.mohalk.banzem.features.modules.client;
/*     */ import com.mojang.realmsclient.gui.ChatFormatting;
/*     */ import java.awt.Color;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.ClientEvent;
/*     */ import me.mohalk.banzem.event.events.Render2DEvent;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.misc.ToolTips;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.manager.TextManager;
/*     */ import me.mohalk.banzem.util.ColorUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.gui.ScaledResolution;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.init.MobEffects;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.potion.Potion;
/*     */ import net.minecraft.potion.PotionEffect;
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class HUD extends Module {
/*  33 */   private static final ItemStack totem = new ItemStack(Items.field_190929_cY);
/*  34 */   private static final ResourceLocation codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
/*  35 */   private static final ResourceLocation csgoHitmarker = new ResourceLocation("earthhack", "csgo_hitmarker");
/*  36 */   private static HUD INSTANCE = new HUD();
/*  37 */   private final Setting<Boolean> renderingUp = register(new Setting("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
/*  38 */   private final Setting<WaterMark> watermark = register(new Setting("Logo", WaterMark.NONE, "WaterMark"));
/*  39 */   private final Setting<Boolean> modeVer = register(new Setting("Version", Boolean.valueOf(false), v -> (this.watermark.getValue() != WaterMark.NONE)));
/*  40 */   private final Setting<Boolean> arrayList = register(new Setting("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
/*  41 */   private final Setting<Boolean> moduleColors = register(new Setting("ModuleColors", Boolean.valueOf(false), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
/*  42 */   private final Setting<Boolean> alphabeticalSorting = register(new Setting("AlphabeticalSorting", Boolean.valueOf(false), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
/*  43 */   private final Setting<Boolean> serverBrand = register(new Setting("ServerBrand", Boolean.valueOf(false), "Brand of the server you are on."));
/*  44 */   private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(false), "Your response time to the server."));
/*  45 */   private final Setting<Boolean> tps = register(new Setting("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
/*  46 */   private final Setting<Boolean> fps = register(new Setting("FPS", Boolean.valueOf(false), "Your frames per second."));
/*  47 */   private final Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(false), "Your current coordinates"));
/*  48 */   private final Setting<Boolean> direction = register(new Setting("Direction", Boolean.valueOf(false), "The Direction you are facing."));
/*  49 */   private final Setting<Boolean> speed = register(new Setting("Speed", Boolean.valueOf(false), "Your Speed"));
/*  50 */   private final Setting<Boolean> potions = register(new Setting("Potions", Boolean.valueOf(false), "Active potion effects"));
/*  51 */   private final Setting<Boolean> altPotionsColors = register(new Setting("AltPotionColors", Boolean.valueOf(false), v -> ((Boolean)this.potions.getValue()).booleanValue()));
/*  52 */   private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(false), "ArmorHUD"));
/*  53 */   private final Setting<Boolean> durability = register(new Setting("Durability", Boolean.valueOf(false), "Durability"));
/*  54 */   private final Setting<Boolean> percent = register(new Setting("Percent", Boolean.valueOf(true), v -> ((Boolean)this.armor.getValue()).booleanValue()));
/*  55 */   private final Setting<Boolean> totems = register(new Setting("Totems", Boolean.valueOf(false), "TotemHUD"));
/*  56 */   private final Setting<Greeter> greeter = register(new Setting("Greeter", Greeter.NONE, "Greets you."));
/*  57 */   private final Setting<String> spoofGreeter = register(new Setting("GreeterName", "CharlesDana", v -> (this.greeter.getValue() == Greeter.CUSTOM)));
/*  58 */   private final Setting<LagNotify> lag = register(new Setting("Lag", LagNotify.GRAY, "Lag Notifier"));
/*  59 */   private final Setting<Boolean> hitMarkers = register(new Setting("HitMarkers", Boolean.valueOf(false)));
/*  60 */   private final Setting<Boolean> grayNess = register(new Setting("FutureColour", Boolean.valueOf(false)));
/*  61 */   private final Timer timer = new Timer();
/*  62 */   private final Timer moduleTimer = new Timer();
/*  63 */   public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false), "Universal colors for hud."));
/*  64 */   public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), "Rainbow hud."));
/*  65 */   public Setting<Integer> factor = register(new Setting("Factor", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(20), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
/*  66 */   public Setting<Boolean> rolling = register(new Setting("Rolling", Boolean.valueOf(false), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
/*  67 */   public Setting<Integer> rainbowSpeed = register(new Setting("RSpeed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
/*  68 */   public Setting<Integer> rainbowSaturation = register(new Setting("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
/*  69 */   public Setting<Integer> rainbowBrightness = register(new Setting("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
/*  70 */   public Setting<Boolean> potionIcons = register(new Setting("PotionIcons", Boolean.valueOf(true), "Draws Potion Icons."));
/*  71 */   public Setting<Boolean> shadow = register(new Setting("Shadow", Boolean.valueOf(false), "Draws the text with a shadow."));
/*  72 */   public Setting<Integer> animationHorizontalTime = register(new Setting("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
/*  73 */   public Setting<Integer> animationVerticalTime = register(new Setting("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
/*  74 */   public Setting<Boolean> textRadar = register(new Setting("TextRadar", Boolean.valueOf(false), "A TextRadar"));
/*  75 */   public Setting<Boolean> time = register(new Setting("Time", Boolean.valueOf(false), "The time"));
/*  76 */   public Setting<Integer> hudRed = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
/*  77 */   public Setting<Integer> hudGreen = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
/*  78 */   public Setting<Integer> hudBlue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
/*  79 */   public Setting<Boolean> potions1 = register(new Setting("LevelPotions", Boolean.valueOf(false), v -> ((Boolean)this.potions.getValue()).booleanValue()));
/*  80 */   public Setting<Boolean> MS = register(new Setting("ms", Boolean.valueOf(false), v -> ((Boolean)this.ping.getValue()).booleanValue()));
/*  81 */   public Map<Module, Float> moduleProgressMap = new HashMap<>();
/*  82 */   public Map<Integer, Integer> colorMap = new HashMap<>();
/*  83 */   private Map<String, Integer> players = new HashMap<>();
/*  84 */   private final Map<Potion, Color> potionColorMap = new HashMap<>();
/*     */   
/*     */   private int color;
/*     */   private boolean shouldIncrement;
/*     */   private int hitMarkerTimer;
/*     */   
/*     */   public HUD() {
/*  91 */     super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
/*  92 */     setInstance();
/*  93 */     this.potionColorMap.put(MobEffects.field_76424_c, new Color(124, 175, 198));
/*  94 */     this.potionColorMap.put(MobEffects.field_76421_d, new Color(90, 108, 129));
/*  95 */     this.potionColorMap.put(MobEffects.field_76422_e, new Color(217, 192, 67));
/*  96 */     this.potionColorMap.put(MobEffects.field_76419_f, new Color(74, 66, 23));
/*  97 */     this.potionColorMap.put(MobEffects.field_76420_g, new Color(147, 36, 35));
/*  98 */     this.potionColorMap.put(MobEffects.field_76432_h, new Color(67, 10, 9));
/*  99 */     this.potionColorMap.put(MobEffects.field_76433_i, new Color(67, 10, 9));
/* 100 */     this.potionColorMap.put(MobEffects.field_76430_j, new Color(34, 255, 76));
/* 101 */     this.potionColorMap.put(MobEffects.field_76431_k, new Color(85, 29, 74));
/* 102 */     this.potionColorMap.put(MobEffects.field_76428_l, new Color(205, 92, 171));
/* 103 */     this.potionColorMap.put(MobEffects.field_76429_m, new Color(153, 69, 58));
/* 104 */     this.potionColorMap.put(MobEffects.field_76426_n, new Color(228, 154, 58));
/* 105 */     this.potionColorMap.put(MobEffects.field_76427_o, new Color(46, 82, 153));
/* 106 */     this.potionColorMap.put(MobEffects.field_76441_p, new Color(127, 131, 146));
/* 107 */     this.potionColorMap.put(MobEffects.field_76440_q, new Color(31, 31, 35));
/* 108 */     this.potionColorMap.put(MobEffects.field_76439_r, new Color(31, 31, 161));
/* 109 */     this.potionColorMap.put(MobEffects.field_76438_s, new Color(88, 118, 83));
/* 110 */     this.potionColorMap.put(MobEffects.field_76437_t, new Color(72, 77, 72));
/* 111 */     this.potionColorMap.put(MobEffects.field_76436_u, new Color(78, 147, 49));
/* 112 */     this.potionColorMap.put(MobEffects.field_82731_v, new Color(53, 42, 39));
/* 113 */     this.potionColorMap.put(MobEffects.field_180152_w, new Color(248, 125, 35));
/* 114 */     this.potionColorMap.put(MobEffects.field_76444_x, new Color(37, 82, 165));
/* 115 */     this.potionColorMap.put(MobEffects.field_76443_y, new Color(248, 36, 35));
/* 116 */     this.potionColorMap.put(MobEffects.field_188423_x, new Color(148, 160, 97));
/* 117 */     this.potionColorMap.put(MobEffects.field_188424_y, new Color(206, 255, 255));
/* 118 */     this.potionColorMap.put(MobEffects.field_188425_z, new Color(51, 153, 0));
/* 119 */     this.potionColorMap.put(MobEffects.field_189112_A, new Color(192, 164, 77));
/*     */   }
/*     */   
/*     */   public static HUD getInstance() {
/* 123 */     if (INSTANCE == null) {
/* 124 */       INSTANCE = new HUD();
/*     */     }
/* 126 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/* 130 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/* 135 */     for (Module module : Banzem.moduleManager.sortedModules) {
/* 136 */       if (module.isDisabled() && module.arrayListOffset == 0.0F) {
/* 137 */         module.sliding = true;
/*     */       }
/*     */     } 
/* 140 */     if (this.timer.passedMs(((Integer)(Managers.getInstance()).textRadarUpdates.getValue()).intValue())) {
/* 141 */       this.players = getTextRadarPlayers();
/* 142 */       this.timer.reset();
/*     */     } 
/* 144 */     if (this.shouldIncrement) {
/* 145 */       this.hitMarkerTimer++;
/*     */     }
/* 147 */     if (this.hitMarkerTimer == 10) {
/* 148 */       this.hitMarkerTimer = 0;
/* 149 */       this.shouldIncrement = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onModuleToggle(ClientEvent event) {
/* 155 */     if (event.getFeature() instanceof Module) {
/* 156 */       if (event.getStage() == 0) {
/* 157 */         for (float i = 0.0F; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
/* 158 */           if (this.moduleTimer.passedMs(1L)) {
/* 159 */             this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf(this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i));
/*     */           }
/* 161 */           this.timer.reset();
/*     */         } 
/* 163 */       } else if (event.getStage() == 1) {
/* 164 */         for (float i = 0.0F; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
/* 165 */           if (this.moduleTimer.passedMs(1L)) {
/* 166 */             this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf(this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i));
/*     */           }
/* 168 */           this.timer.reset();
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void onRender2D(Render2DEvent event) {
/*     */     int color;
/* 176 */     if (Feature.fullNullCheck()) {
/*     */       return;
/*     */     }
/* 179 */     int colorSpeed = 101 - ((Integer)this.rainbowSpeed.getValue()).intValue();
/* 180 */     float hue = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.hue : ((float)(System.currentTimeMillis() % (360 * colorSpeed)) / 360.0F * colorSpeed);
/* 181 */     int width = this.renderer.scaledWidth;
/* 182 */     int height = this.renderer.scaledHeight;
/* 183 */     float tempHue = hue;
/* 184 */     for (int i = 0; i <= height; i++) {
/* 185 */       if (((Boolean)this.colorSync.getValue()).booleanValue()) {
/* 186 */         this.colorMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, ((Integer)Colors.INSTANCE.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)Colors.INSTANCE.rainbowBrightness.getValue()).intValue() / 255.0F)));
/*     */       } else {
/* 188 */         this.colorMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F)));
/*     */       } 
/* 190 */       tempHue += 1.0F / height * ((Integer)this.factor.getValue()).intValue();
/*     */     } 
/* 192 */     GlStateManager.func_179094_E();
/* 193 */     if (((Boolean)this.rainbow.getValue()).booleanValue() && !((Boolean)this.rolling.getValue()).booleanValue()) {
/* 194 */       this.color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : Color.HSBtoRGB(hue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F);
/* 195 */     } else if (!((Boolean)this.rainbow.getValue()).booleanValue()) {
/* 196 */       this.color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(((Integer)this.hudRed.getValue()).intValue(), ((Integer)this.hudGreen.getValue()).intValue(), ((Integer)this.hudBlue.getValue()).intValue());
/*     */     } 
/* 198 */     String grayString = ((Boolean)this.grayNess.getValue()).booleanValue() ? String.valueOf(ChatFormatting.GRAY) : "";
/* 199 */     switch ((WaterMark)this.watermark.getValue()) {
/*     */       case TIME:
/* 201 */         this.renderer.drawString("QuartzHack.cc " + (((Boolean)this.modeVer.getValue()).booleanValue() ? "0.3.0-b22" : ""), 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
/*     */         break;
/*     */     } 
/*     */     
/* 205 */     if (((Boolean)this.textRadar.getValue()).booleanValue()) {
/* 206 */       drawTextRadar((ToolTips.getInstance().isOff() || !((Boolean)(ToolTips.getInstance()).shulkerSpy.getValue()).booleanValue() || !((Boolean)(ToolTips.getInstance()).render.getValue()).booleanValue()) ? 0 : ToolTips.getInstance().getTextRadarY());
/*     */     }
/* 208 */     int j = ((Boolean)this.renderingUp.getValue()).booleanValue() ? 0 : ((mc.field_71462_r instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0);
/* 209 */     if (((Boolean)this.arrayList.getValue()).booleanValue()) {
/* 210 */       if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
/* 211 */         for (int m = 0; m < (((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Banzem.moduleManager.alphabeticallySortedModules.size() : Banzem.moduleManager.sortedModules.size()); m++) {
/* 212 */           Module module = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Banzem.moduleManager.alphabeticallySortedModules.get(m) : Banzem.moduleManager.sortedModules.get(m);
/* 213 */           String text = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
/* 214 */           Color moduleColor = (Color)Banzem.moduleManager.moduleColorMap.get(module);
/* 215 */           this.renderer.drawString(text, (width - 2 - this.renderer.getStringWidth(text)) + ((((Integer)this.animationHorizontalTime.getValue()).intValue() == 1) ? 0.0F : module.arrayListOffset), (2 + j * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(MathUtil.clamp(2 + j * 10, 0, height)))).intValue() : ((((Boolean)this.moduleColors.getValue()).booleanValue() && moduleColor != null) ? moduleColor.getRGB() : this.color), true);
/* 216 */           j++;
/*     */         } 
/*     */       } else {
/* 219 */         for (int m = 0; m < (((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Banzem.moduleManager.alphabeticallySortedModules.size() : Banzem.moduleManager.sortedModules.size()); m++) {
/* 220 */           Module module = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Banzem.moduleManager.alphabeticallySortedModules.get(Banzem.moduleManager.alphabeticallySortedModules.size() - 1 - m) : Banzem.moduleManager.sortedModules.get(m);
/* 221 */           String text = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
/* 222 */           Color moduleColor = (Color)Banzem.moduleManager.moduleColorMap.get(module);
/* 223 */           TextManager renderer = this.renderer;
/* 224 */           String text5 = text;
/* 225 */           float x = (width - 2 - this.renderer.getStringWidth(text)) + ((((Integer)this.animationHorizontalTime.getValue()).intValue() == 1) ? 0.0F : module.arrayListOffset);
/* 226 */           int n = height;
/* 227 */           j += 10;
/* 228 */           renderer.drawString(text5, x, (n - j), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(MathUtil.clamp(height - j, 0, height)))).intValue() : ((((Boolean)this.moduleColors.getValue()).booleanValue() && moduleColor != null) ? moduleColor.getRGB() : this.color), true);
/*     */         } 
/*     */       } 
/*     */     }
/* 232 */     int k = ((Boolean)this.renderingUp.getValue()).booleanValue() ? ((mc.field_71462_r instanceof net.minecraft.client.gui.GuiChat) ? 0 : 0) : 0;
/* 233 */     if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
/* 234 */       if (((Boolean)this.serverBrand.getValue()).booleanValue()) {
/* 235 */         String text2 = grayString + "Server brand " + ChatFormatting.WHITE + Banzem.serverManager.getServerBrand();
/* 236 */         TextManager renderer2 = this.renderer;
/* 237 */         String text6 = text2;
/* 238 */         float x2 = (width - this.renderer.getStringWidth(text2) + 2);
/* 239 */         int n2 = height - 2;
/* 240 */         k += 10;
/* 241 */         renderer2.drawString(text6, x2, (n2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */       } 
/* 243 */       if (((Boolean)this.potions.getValue()).booleanValue()) {
/* 244 */         for (PotionEffect effect : Banzem.potionManager.getOwnPotions()) {
/* 245 */           String text3 = ((Boolean)this.altPotionsColors.getValue()).booleanValue() ? Banzem.potionManager.getPotionString(effect) : Banzem.potionManager.getColoredPotionString(effect);
/* 246 */           TextManager renderer3 = this.renderer;
/* 247 */           String text7 = text3;
/* 248 */           float x3 = (width - this.renderer.getStringWidth(text3) + 2);
/* 249 */           int n3 = height - 2;
/* 250 */           k += 10;
/* 251 */           renderer3.drawString(text7, x3, (n3 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : (((Boolean)this.altPotionsColors.getValue()).booleanValue() ? ((Color)this.potionColorMap.get(effect.func_188419_a())).getRGB() : this.color), true);
/*     */         } 
/*     */       }
/* 254 */       if (((Boolean)this.speed.getValue()).booleanValue()) {
/* 255 */         String text2 = grayString + "Speed " + ChatFormatting.WHITE + Banzem.speedManager.getSpeedKpH() + " km/h";
/* 256 */         TextManager renderer4 = this.renderer;
/* 257 */         String text8 = text2;
/* 258 */         float x4 = (width - this.renderer.getStringWidth(text2) + 2);
/* 259 */         int n4 = height - 2;
/* 260 */         k += 10;
/* 261 */         renderer4.drawString(text8, x4, (n4 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */       } 
/* 263 */       if (((Boolean)this.time.getValue()).booleanValue()) {
/* 264 */         String text2 = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
/* 265 */         TextManager renderer5 = this.renderer;
/* 266 */         String text9 = text2;
/* 267 */         float x5 = (width - this.renderer.getStringWidth(text2) + 2);
/* 268 */         int n5 = height - 2;
/* 269 */         k += 10;
/* 270 */         renderer5.drawString(text9, x5, (n5 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */       } 
/* 272 */       if (((Boolean)this.durability.getValue()).booleanValue()) {
/* 273 */         int itemDamage = mc.field_71439_g.func_184614_ca().func_77958_k() - mc.field_71439_g.func_184614_ca().func_77952_i();
/* 274 */         if (itemDamage > 0) {
/* 275 */           String str1 = grayString + "Durability " + ChatFormatting.RESET + itemDamage;
/* 276 */           TextManager renderer6 = this.renderer;
/* 277 */           String text10 = str1;
/* 278 */           float x6 = (width - this.renderer.getStringWidth(str1) + 2);
/* 279 */           int n6 = height - 2;
/* 280 */           k += 10;
/* 281 */           renderer6.drawString(text10, x6, (n6 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */         } 
/*     */       } 
/* 284 */       if (((Boolean)this.tps.getValue()).booleanValue()) {
/* 285 */         String text2 = grayString + "TPS " + ChatFormatting.WHITE + Banzem.serverManager.getTPS();
/* 286 */         TextManager renderer7 = this.renderer;
/* 287 */         String text11 = text2;
/* 288 */         float x7 = (width - this.renderer.getStringWidth(text2) + 2);
/* 289 */         int n7 = height - 2;
/* 290 */         k += 10;
/* 291 */         renderer7.drawString(text11, x7, (n7 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */       } 
/* 293 */       String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.field_71470_ab;
/* 294 */       String text = grayString + "Ping " + ChatFormatting.WHITE + (ServerModule.getInstance().isConnected() ? ServerModule.getInstance().getServerPing() : Banzem.serverManager.getPing()) + (((Boolean)this.MS.getValue()).booleanValue() ? "ms" : "");
/* 295 */       if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
/* 296 */         if (((Boolean)this.ping.getValue()).booleanValue()) {
/* 297 */           TextManager renderer8 = this.renderer;
/* 298 */           String text12 = text;
/* 299 */           float x8 = (width - this.renderer.getStringWidth(text) + 2);
/* 300 */           int n8 = height - 2;
/* 301 */           k += 10;
/* 302 */           renderer8.drawString(text12, x8, (n8 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */         } 
/* 304 */         if (((Boolean)this.fps.getValue()).booleanValue()) {
/* 305 */           TextManager renderer9 = this.renderer;
/* 306 */           String text13 = fpsText;
/* 307 */           float x9 = (width - this.renderer.getStringWidth(fpsText) + 2);
/* 308 */           int n9 = height - 2;
/* 309 */           k += 10;
/* 310 */           renderer9.drawString(text13, x9, (n9 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */         } 
/*     */       } else {
/* 313 */         if (((Boolean)this.fps.getValue()).booleanValue()) {
/* 314 */           TextManager renderer10 = this.renderer;
/* 315 */           String text14 = fpsText;
/* 316 */           float x10 = (width - this.renderer.getStringWidth(fpsText) + 2);
/* 317 */           int n10 = height - 2;
/* 318 */           k += 10;
/* 319 */           renderer10.drawString(text14, x10, (n10 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */         } 
/* 321 */         if (((Boolean)this.ping.getValue()).booleanValue()) {
/* 322 */           TextManager renderer11 = this.renderer;
/* 323 */           String text15 = text;
/* 324 */           float x11 = (width - this.renderer.getStringWidth(text) + 2);
/* 325 */           int n11 = height - 2;
/* 326 */           k += 10;
/* 327 */           renderer11.drawString(text15, x11, (n11 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 331 */       if (((Boolean)this.serverBrand.getValue()).booleanValue()) {
/* 332 */         String text2 = grayString + "Server brand " + ChatFormatting.WHITE + Banzem.serverManager.getServerBrand();
/* 333 */         this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */       } 
/* 335 */       if (((Boolean)this.potions.getValue()).booleanValue()) {
/* 336 */         for (PotionEffect effect : Banzem.potionManager.getOwnPotions()) {
/* 337 */           String text3 = ((Boolean)this.altPotionsColors.getValue()).booleanValue() ? Banzem.potionManager.getPotionString(effect) : Banzem.potionManager.getColoredPotionString(effect);
/* 338 */           this.renderer.drawString(text3, (width - this.renderer.getStringWidth(text3) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : (((Boolean)this.altPotionsColors.getValue()).booleanValue() ? ((Color)this.potionColorMap.get(effect.func_188419_a())).getRGB() : this.color), true);
/*     */         } 
/*     */       }
/* 341 */       if (((Boolean)this.speed.getValue()).booleanValue()) {
/* 342 */         String text2 = grayString + "Speed " + ChatFormatting.WHITE + Banzem.speedManager.getSpeedKpH() + " km/h";
/* 343 */         this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */       } 
/* 345 */       if (((Boolean)this.time.getValue()).booleanValue()) {
/* 346 */         String text2 = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
/* 347 */         this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */       } 
/* 349 */       if (((Boolean)this.durability.getValue()).booleanValue()) {
/* 350 */         int itemDamage = mc.field_71439_g.func_184614_ca().func_77958_k() - mc.field_71439_g.func_184614_ca().func_77952_i();
/* 351 */         if (itemDamage > 0) {
/* 352 */           String str = grayString + "Durability " + ChatFormatting.GREEN + itemDamage;
/* 353 */           this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */         } 
/*     */       } 
/* 356 */       if (((Boolean)this.tps.getValue()).booleanValue()) {
/* 357 */         String text2 = grayString + "TPS " + ChatFormatting.WHITE + Banzem.serverManager.getTPS();
/* 358 */         this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */       } 
/* 360 */       String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.field_71470_ab;
/* 361 */       String text = grayString + "Ping " + ChatFormatting.WHITE + Banzem.serverManager.getPing();
/* 362 */       if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
/* 363 */         if (((Boolean)this.ping.getValue()).booleanValue()) {
/* 364 */           this.renderer.drawString(text, (width - this.renderer.getStringWidth(text) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */         }
/* 366 */         if (((Boolean)this.fps.getValue()).booleanValue()) {
/* 367 */           this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */         }
/*     */       } else {
/* 370 */         if (((Boolean)this.fps.getValue()).booleanValue()) {
/* 371 */           this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */         }
/* 373 */         if (((Boolean)this.ping.getValue()).booleanValue()) {
/* 374 */           this.renderer.drawString(text, (width - this.renderer.getStringWidth(text) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
/*     */         }
/*     */       } 
/*     */     } 
/* 378 */     boolean inHell = mc.field_71441_e.func_180494_b(mc.field_71439_g.func_180425_c()).func_185359_l().equals("Hell");
/* 379 */     int posX = (int)mc.field_71439_g.field_70165_t;
/* 380 */     int posY = (int)mc.field_71439_g.field_70163_u;
/* 381 */     int posZ = (int)mc.field_71439_g.field_70161_v;
/* 382 */     float nether = inHell ? 8.0F : 0.125F;
/* 383 */     int hposX = (int)(mc.field_71439_g.field_70165_t * nether);
/* 384 */     int hposZ = (int)(mc.field_71439_g.field_70161_v * nether);
/* 385 */     if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
/* 386 */       Banzem.notificationManager.handleNotifications(height - k + 16);
/*     */     } else {
/* 388 */       Banzem.notificationManager.handleNotifications(height - j + 16);
/*     */     } 
/* 390 */     k = (mc.field_71462_r instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0;
/* 391 */     String coordinates = String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + hposX + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + ", " + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + hposZ + "]";
/* 392 */     String text4 = (((Boolean)this.direction.getValue()).booleanValue() ? (Banzem.rotationManager.getDirection4D(false) + " ") : "") + (((Boolean)this.coords.getValue()).booleanValue() ? coordinates : "") + "";
/* 393 */     TextManager renderer12 = this.renderer;
/* 394 */     String text16 = text4;
/* 395 */     float x12 = 2.0F;
/* 396 */     int n12 = height;
/* 397 */     k += 10;
/* 398 */     float y = (n12 - k);
/*     */     
/* 400 */     if (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) {
/* 401 */       Map<Integer, Integer> colorMap = this.colorMap;
/* 402 */       int n13 = height;
/* 403 */       k += 10;
/* 404 */       color = ((Integer)colorMap.get(Integer.valueOf(n13 - k))).intValue();
/*     */     } else {
/* 406 */       color = this.color;
/*     */     } 
/* 408 */     renderer12.drawString(text16, 2.0F, y, color, true);
/* 409 */     if (((Boolean)this.armor.getValue()).booleanValue()) {
/* 410 */       renderArmorHUD(((Boolean)this.percent.getValue()).booleanValue());
/*     */     }
/* 412 */     if (((Boolean)this.totems.getValue()).booleanValue()) {
/* 413 */       renderTotemHUD();
/*     */     }
/* 415 */     if (this.greeter.getValue() != Greeter.NONE) {
/* 416 */       renderGreeter();
/*     */     }
/* 418 */     if (this.lag.getValue() != LagNotify.NONE) {
/* 419 */       renderLag();
/*     */     }
/* 421 */     if (((Boolean)this.hitMarkers.getValue()).booleanValue() && this.hitMarkerTimer > 0) {
/* 422 */       drawHitMarkers();
/*     */     }
/* 424 */     GlStateManager.func_179121_F();
/*     */   }
/*     */   
/*     */   public Map<String, Integer> getTextRadarPlayers() {
/* 428 */     return EntityUtil.getTextRadarPlayers();
/*     */   }
/*     */   
/*     */   public void renderGreeter() {
/* 432 */     int width = this.renderer.scaledWidth;
/* 433 */     String text = "";
/* 434 */     switch ((Greeter)this.greeter.getValue()) {
/*     */       case TIME:
/* 436 */         text = text + MathUtil.getTimeOfDay() + mc.field_71439_g.getDisplayNameString();
/*     */         break;
/*     */       
/*     */       case CHRISTMAS:
/* 440 */         text = text + "Merry Christmas " + mc.field_71439_g.getDisplayNameString() + " :^)";
/*     */         break;
/*     */       
/*     */       case LONG:
/* 444 */         text = text + "Welcome to QuartzHack.cc " + mc.field_71439_g.getDisplayNameString() + " :^)";
/*     */         break;
/*     */       
/*     */       case CUSTOM:
/* 448 */         text = text + (String)this.spoofGreeter.getValue();
/*     */         break;
/*     */       
/*     */       default:
/* 452 */         text = text + "Welcome " + mc.field_71439_g.getDisplayNameString();
/*     */         break;
/*     */     } 
/*     */     
/* 456 */     this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
/*     */   }
/*     */   
/*     */   public void renderLag() {
/* 460 */     int width = this.renderer.scaledWidth;
/* 461 */     if (Banzem.serverManager.isServerNotResponding()) {
/* 462 */       String text = ((this.lag.getValue() == LagNotify.GRAY) ? (String)ChatFormatting.GRAY : (String)ChatFormatting.RED) + "Server not responding: " + MathUtil.round((float)Banzem.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
/* 463 */       this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(20))).intValue() : this.color, true);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void renderArrayList() {}
/*     */   
/*     */   public void renderTotemHUD() {
/* 471 */     int width = this.renderer.scaledWidth;
/* 472 */     int height = this.renderer.scaledHeight;
/* 473 */     int totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> (itemStack.func_77973_b() == Items.field_190929_cY)).mapToInt(ItemStack::func_190916_E).sum();
/* 474 */     if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
/* 475 */       totems += mc.field_71439_g.func_184592_cb().func_190916_E();
/*     */     }
/* 477 */     if (totems > 0) {
/* 478 */       GlStateManager.func_179098_w();
/* 479 */       int i = width / 2;
/* 480 */       int iteration = 0;
/* 481 */       int y = height - 55 - ((mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f()) ? 10 : 0);
/* 482 */       int x = i - 189 + 180 + 2;
/* 483 */       GlStateManager.func_179126_j();
/* 484 */       RenderUtil.itemRender.field_77023_b = 200.0F;
/* 485 */       RenderUtil.itemRender.func_180450_b(totem, x, y);
/* 486 */       RenderUtil.itemRender.func_180453_a(mc.field_71466_p, totem, x, y, "");
/* 487 */       RenderUtil.itemRender.field_77023_b = 0.0F;
/* 488 */       GlStateManager.func_179098_w();
/* 489 */       GlStateManager.func_179140_f();
/* 490 */       GlStateManager.func_179097_i();
/* 491 */       this.renderer.drawStringWithShadow(totems + "", (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (y + 9), 16777215);
/* 492 */       GlStateManager.func_179126_j();
/* 493 */       GlStateManager.func_179140_f();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void renderArmorHUD(boolean percent) {
/* 498 */     int width = this.renderer.scaledWidth;
/* 499 */     int height = this.renderer.scaledHeight;
/* 500 */     GlStateManager.func_179098_w();
/* 501 */     int i = width / 2;
/* 502 */     int iteration = 0;
/* 503 */     int y = height - 55 - ((mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f()) ? 10 : 0);
/* 504 */     for (ItemStack is : mc.field_71439_g.field_71071_by.field_70460_b) {
/* 505 */       iteration++;
/* 506 */       if (is.func_190926_b()) {
/*     */         continue;
/*     */       }
/* 509 */       int x = i - 90 + (9 - iteration) * 20 + 2;
/* 510 */       GlStateManager.func_179126_j();
/* 511 */       RenderUtil.itemRender.field_77023_b = 200.0F;
/* 512 */       RenderUtil.itemRender.func_180450_b(is, x, y);
/* 513 */       RenderUtil.itemRender.func_180453_a(mc.field_71466_p, is, x, y, "");
/* 514 */       RenderUtil.itemRender.field_77023_b = 0.0F;
/* 515 */       GlStateManager.func_179098_w();
/* 516 */       GlStateManager.func_179140_f();
/* 517 */       GlStateManager.func_179097_i();
/* 518 */       String s = (is.func_190916_E() > 1) ? (is.func_190916_E() + "") : "";
/* 519 */       this.renderer.drawStringWithShadow(s, (x + 19 - 2 - this.renderer.getStringWidth(s)), (y + 9), 16777215);
/* 520 */       if (!percent) {
/*     */         continue;
/*     */       }
/* 523 */       int dmg = 0;
/* 524 */       int itemDurability = is.func_77958_k() - is.func_77952_i();
/* 525 */       float green = (is.func_77958_k() - is.func_77952_i()) / is.func_77958_k();
/* 526 */       float red = 1.0F - green;
/* 527 */       if (percent) {
/* 528 */         dmg = 100 - (int)(red * 100.0F);
/*     */       } else {
/* 530 */         dmg = itemDurability;
/*     */       } 
/* 532 */       this.renderer.drawStringWithShadow(dmg + "", (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (y - 11), ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0));
/*     */     } 
/* 534 */     GlStateManager.func_179126_j();
/* 535 */     GlStateManager.func_179140_f();
/*     */   }
/*     */   
/*     */   public void drawHitMarkers() {
/* 539 */     ScaledResolution resolution = new ScaledResolution(mc);
/* 540 */     RenderUtil.drawLine(resolution.func_78326_a() / 2.0F - 4.0F, resolution.func_78328_b() / 2.0F - 4.0F, resolution.func_78326_a() / 2.0F - 8.0F, resolution.func_78328_b() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
/* 541 */     RenderUtil.drawLine(resolution.func_78326_a() / 2.0F + 4.0F, resolution.func_78328_b() / 2.0F - 4.0F, resolution.func_78326_a() / 2.0F + 8.0F, resolution.func_78328_b() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
/* 542 */     RenderUtil.drawLine(resolution.func_78326_a() / 2.0F - 4.0F, resolution.func_78328_b() / 2.0F + 4.0F, resolution.func_78326_a() / 2.0F - 8.0F, resolution.func_78328_b() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
/* 543 */     RenderUtil.drawLine(resolution.func_78326_a() / 2.0F + 4.0F, resolution.func_78328_b() / 2.0F + 4.0F, resolution.func_78326_a() / 2.0F + 8.0F, resolution.func_78328_b() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
/*     */   }
/*     */   
/*     */   public void drawTextRadar(int yOffset) {
/* 547 */     if (!this.players.isEmpty()) {
/* 548 */       int y = this.renderer.getFontHeight() + 7 + yOffset;
/* 549 */       for (Map.Entry<String, Integer> player : this.players.entrySet()) {
/* 550 */         String text = (String)player.getKey() + " ";
/* 551 */         int textheight = this.renderer.getFontHeight() + 1;
/* 552 */         this.renderer.drawString(text, 2.0F, y, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(y))).intValue() : this.color, true);
/* 553 */         y += textheight;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public enum Greeter {
/* 559 */     NONE,
/* 560 */     NAME,
/* 561 */     TIME,
/* 562 */     CHRISTMAS,
/* 563 */     LONG,
/* 564 */     CUSTOM;
/*     */   }
/*     */   
/*     */   public enum LagNotify {
/* 568 */     NONE,
/* 569 */     RED,
/* 570 */     GRAY;
/*     */   }
/*     */   
/*     */   public enum WaterMark {
/* 574 */     NONE,
/* 575 */     BANZEM;
/*     */   }
/*     */   
/*     */   public enum Sound {
/* 579 */     NONE,
/* 580 */     COD,
/* 581 */     CSGO;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\HUD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */