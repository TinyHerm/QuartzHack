/*     */ package me.mohalk.banzem.features.modules.render;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Random;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.Render3DEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.RotationUtil;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ 
/*     */ 
/*     */ public class HoleESP
/*     */   extends Module
/*     */ {
/*  17 */   private static HoleESP INSTANCE = new HoleESP();
/*  18 */   private final Setting<Integer> holes = register(new Setting("Holes", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(500)));
/*  19 */   private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
/*  20 */   private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  21 */   private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
/*  22 */   private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  23 */   public Setting<Boolean> ownHole = register(new Setting("OwnHole", Boolean.valueOf(false)));
/*  24 */   public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true)));
/*  25 */   private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.box.getValue()).booleanValue()));
/*  26 */   public Setting<Boolean> gradientBox = register(new Setting("GradientBox", Boolean.FALSE, v -> ((Boolean)this.box.getValue()).booleanValue()));
/*  27 */   public Setting<Boolean> pulseAlpha = register(new Setting("PulseAlpha", Boolean.FALSE, v -> ((Boolean)this.gradientBox.getValue()).booleanValue()));
/*  28 */   private final Setting<Integer> minPulseAlpha = register(new Setting("MinPulse", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.pulseAlpha.getValue()).booleanValue()));
/*  29 */   private final Setting<Integer> maxPulseAlpha = register(new Setting("MaxPulse", Integer.valueOf(40), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.pulseAlpha.getValue()).booleanValue()));
/*  30 */   private final Setting<Integer> pulseSpeed = register(new Setting("PulseSpeed", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(50), v -> ((Boolean)this.pulseAlpha.getValue()).booleanValue()));
/*  31 */   public Setting<Boolean> invertGradientBox = register(new Setting("InvertGradientBox", Boolean.FALSE, v -> ((Boolean)this.gradientBox.getValue()).booleanValue()));
/*  32 */   public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
/*  33 */   private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  34 */   public Setting<Boolean> gradientOutline = register(new Setting("GradientOutline", Boolean.FALSE, v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  35 */   public Setting<Boolean> invertGradientOutline = register(new Setting("InvertGradientOutline", Boolean.FALSE, v -> ((Boolean)this.gradientOutline.getValue()).booleanValue()));
/*  36 */   public Setting<Double> height = register(new Setting("Height", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
/*  37 */   public Setting<Boolean> safeColor = register(new Setting("SafeColor", Boolean.valueOf(false)));
/*  38 */   private final Setting<Integer> safeRed = register(new Setting("SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
/*  39 */   private final Setting<Integer> safeGreen = register(new Setting("SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
/*  40 */   private final Setting<Integer> safeBlue = register(new Setting("SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
/*  41 */   private final Setting<Integer> safeAlpha = register(new Setting("SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
/*  42 */   public Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.FALSE, v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  43 */   private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  44 */   private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  45 */   private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  46 */   private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
/*  47 */   private final Setting<Integer> safecRed = register(new Setting("OL-SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
/*  48 */   private final Setting<Integer> safecGreen = register(new Setting("OL-SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
/*  49 */   private final Setting<Integer> safecBlue = register(new Setting("OL-SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
/*  50 */   private final Setting<Integer> safecAlpha = register(new Setting("OL-SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
/*     */   
/*     */   private boolean pulsing;
/*     */   private boolean shouldDecrease;
/*     */   private int pulseDelay;
/*     */   private int currentPulseAlpha;
/*     */   private int currentAlpha;
/*     */   
/*     */   public HoleESP() {
/*  59 */     super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
/*  60 */     setInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   public static HoleESP getInstance() {
/*  65 */     if (INSTANCE == null) {
/*  66 */       INSTANCE = new HoleESP();
/*     */     }
/*  68 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   private void setInstance() {
/*  73 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onRender3D(Render3DEvent event) {
/*  79 */     int drawnHoles = 0;
/*  80 */     if (!this.pulsing && ((Boolean)this.pulseAlpha.getValue()).booleanValue()) {
/*  81 */       Random rand = new Random();
/*  82 */       this.currentPulseAlpha = rand.nextInt(((Integer)this.maxPulseAlpha.getValue()).intValue() - ((Integer)this.minPulseAlpha.getValue()).intValue() + 1) + ((Integer)this.minPulseAlpha.getValue()).intValue();
/*  83 */       this.pulsing = true;
/*  84 */       this.shouldDecrease = false;
/*     */     } 
/*  86 */     if (this.pulseDelay == 0) {
/*  87 */       if (this.pulsing && ((Boolean)this.pulseAlpha.getValue()).booleanValue() && !this.shouldDecrease) {
/*  88 */         this.currentAlpha++;
/*  89 */         if (this.currentAlpha >= this.currentPulseAlpha) {
/*  90 */           this.shouldDecrease = true;
/*     */         }
/*     */       } 
/*  93 */       if (this.pulsing && ((Boolean)this.pulseAlpha.getValue()).booleanValue() && this.shouldDecrease) {
/*  94 */         this.currentAlpha--;
/*     */       }
/*  96 */       if (this.currentAlpha <= 0) {
/*  97 */         this.pulsing = false;
/*  98 */         this.shouldDecrease = false;
/*     */       } 
/* 100 */       this.pulseDelay++;
/*     */     } else {
/* 102 */       this.pulseDelay++;
/* 103 */       if (this.pulseDelay == 51 - ((Integer)this.pulseSpeed.getValue()).intValue()) {
/* 104 */         this.pulseDelay = 0;
/*     */       }
/*     */     } 
/* 107 */     if (!((Boolean)this.pulseAlpha.getValue()).booleanValue() || !this.pulsing) {
/* 108 */       this.currentAlpha = 0;
/*     */     }
/* 110 */     if (fullNullCheck())
/* 111 */       return;  for (BlockPos pos : Banzem.holeManager.getSortedHoles()) {
/* 112 */       if (drawnHoles >= ((Integer)this.holes.getValue()).intValue())
/* 113 */         break;  if ((pos.equals(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)) && !((Boolean)this.ownHole.getValue()).booleanValue()) || RotationUtil.isInFov(pos))
/*     */         continue; 
/* 115 */       if (((Boolean)this.safeColor.getValue()).booleanValue() && Banzem.holeManager.isSafe(pos)) {
/* 116 */         RenderUtil.drawBoxESP(pos, new Color(((Integer)this.safeRed.getValue()).intValue(), ((Integer)this.safeGreen.getValue()).intValue(), ((Integer)this.safeBlue.getValue()).intValue(), ((Integer)this.safeAlpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.safecRed.getValue()).intValue(), ((Integer)this.safecGreen.getValue()).intValue(), ((Integer)this.safecBlue.getValue()).intValue(), ((Integer)this.safecAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true, ((Double)this.height.getValue()).doubleValue(), ((Boolean)this.gradientBox.getValue()).booleanValue(), ((Boolean)this.gradientOutline.getValue()).booleanValue(), ((Boolean)this.invertGradientBox.getValue()).booleanValue(), ((Boolean)this.invertGradientOutline.getValue()).booleanValue(), this.currentAlpha);
/*     */       } else {
/* 118 */         RenderUtil.drawBoxESP(pos, new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true, ((Double)this.height.getValue()).doubleValue(), ((Boolean)this.gradientBox.getValue()).booleanValue(), ((Boolean)this.gradientOutline.getValue()).booleanValue(), ((Boolean)this.invertGradientBox.getValue()).booleanValue(), ((Boolean)this.invertGradientOutline.getValue()).booleanValue(), this.currentAlpha);
/*     */       } 
/* 120 */       drawnHoles++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\render\HoleESP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */