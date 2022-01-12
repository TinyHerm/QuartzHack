/*     */ package me.mohalk.banzem.features.modules.render;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import me.mohalk.banzem.event.events.Render3DEvent;
/*     */ import me.mohalk.banzem.event.events.RenderEntityModelEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.Colors;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.client.renderer.RenderGlobal;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ESP
/*     */   extends Module
/*     */ {
/*  27 */   private static ESP INSTANCE = new ESP();
/*  28 */   private final Setting<Mode> mode = register(new Setting("Mode", Mode.OUTLINE));
/*  29 */   private final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
/*  30 */   private final Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true)));
/*  31 */   private final Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false)));
/*  32 */   private final Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false)));
/*  33 */   private final Setting<Boolean> items = register(new Setting("Items", Boolean.valueOf(false)));
/*  34 */   private final Setting<Boolean> xporbs = register(new Setting("XpOrbs", Boolean.valueOf(false)));
/*  35 */   private final Setting<Boolean> xpbottles = register(new Setting("XpBottles", Boolean.valueOf(false)));
/*  36 */   private final Setting<Boolean> pearl = register(new Setting("Pearls", Boolean.valueOf(false)));
/*  37 */   private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  38 */   private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  39 */   private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  40 */   private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255)));
/*  41 */   private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
/*  42 */   private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(2.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
/*  43 */   private final Setting<Boolean> colorFriends = register(new Setting("Friends", Boolean.valueOf(true)));
/*  44 */   private final Setting<Boolean> self = register(new Setting("Self", Boolean.valueOf(true)));
/*  45 */   private final Setting<Boolean> onTop = register(new Setting("onTop", Boolean.valueOf(true)));
/*  46 */   private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
/*     */   
/*     */   public ESP() {
/*  49 */     super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
/*  50 */     setInstance();
/*     */   }
/*     */   
/*     */   public static ESP getInstance() {
/*  54 */     if (INSTANCE == null) {
/*  55 */       INSTANCE = new ESP();
/*     */     }
/*  57 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  61 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onRender3D(Render3DEvent event) {
/*  69 */     if (((Boolean)this.items.getValue()).booleanValue()) {
/*  70 */       int i = 0;
/*  71 */       for (Entity entity : mc.field_71441_e.field_72996_f) {
/*  72 */         if (!(entity instanceof net.minecraft.entity.item.EntityItem) || mc.field_71439_g.func_70068_e(entity) >= 2500.0D)
/*  73 */           continue;  Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.func_184121_ak());
/*  74 */         AxisAlignedBB bb = new AxisAlignedBB((entity.func_174813_aQ()).field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, (entity.func_174813_aQ()).field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
/*  75 */         GlStateManager.func_179094_E();
/*  76 */         GlStateManager.func_179147_l();
/*  77 */         GlStateManager.func_179097_i();
/*  78 */         GlStateManager.func_179120_a(770, 771, 0, 1);
/*  79 */         GlStateManager.func_179090_x();
/*  80 */         GlStateManager.func_179132_a(false);
/*  81 */         GL11.glEnable(2848);
/*  82 */         GL11.glHint(3154, 4354);
/*  83 */         GL11.glLineWidth(1.0F);
/*  84 */         RenderGlobal.func_189696_b(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor().getAlpha() : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
/*  85 */         GL11.glDisable(2848);
/*  86 */         GlStateManager.func_179132_a(true);
/*  87 */         GlStateManager.func_179126_j();
/*  88 */         GlStateManager.func_179098_w();
/*  89 */         GlStateManager.func_179084_k();
/*  90 */         GlStateManager.func_179121_F();
/*  91 */         RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
/*  92 */         if (++i < 50);
/*     */       } 
/*     */     } 
/*     */     
/*  96 */     if (((Boolean)this.xporbs.getValue()).booleanValue()) {
/*  97 */       int i = 0;
/*  98 */       for (Entity entity : mc.field_71441_e.field_72996_f) {
/*  99 */         if (!(entity instanceof net.minecraft.entity.item.EntityXPOrb) || mc.field_71439_g.func_70068_e(entity) >= 2500.0D)
/* 100 */           continue;  Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.func_184121_ak());
/* 101 */         AxisAlignedBB bb = new AxisAlignedBB((entity.func_174813_aQ()).field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, (entity.func_174813_aQ()).field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
/* 102 */         GlStateManager.func_179094_E();
/* 103 */         GlStateManager.func_179147_l();
/* 104 */         GlStateManager.func_179097_i();
/* 105 */         GlStateManager.func_179120_a(770, 771, 0, 1);
/* 106 */         GlStateManager.func_179090_x();
/* 107 */         GlStateManager.func_179132_a(false);
/* 108 */         GL11.glEnable(2848);
/* 109 */         GL11.glHint(3154, 4354);
/* 110 */         GL11.glLineWidth(1.0F);
/* 111 */         RenderGlobal.func_189696_b(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F) : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
/* 112 */         GL11.glDisable(2848);
/* 113 */         GlStateManager.func_179132_a(true);
/* 114 */         GlStateManager.func_179126_j();
/* 115 */         GlStateManager.func_179098_w();
/* 116 */         GlStateManager.func_179084_k();
/* 117 */         GlStateManager.func_179121_F();
/* 118 */         RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
/* 119 */         if (++i < 50);
/*     */       } 
/*     */     } 
/*     */     
/* 123 */     if (((Boolean)this.pearl.getValue()).booleanValue()) {
/* 124 */       int i = 0;
/* 125 */       for (Entity entity : mc.field_71441_e.field_72996_f) {
/* 126 */         if (!(entity instanceof net.minecraft.entity.item.EntityEnderPearl) || mc.field_71439_g.func_70068_e(entity) >= 2500.0D)
/* 127 */           continue;  Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.func_184121_ak());
/* 128 */         AxisAlignedBB bb = new AxisAlignedBB((entity.func_174813_aQ()).field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, (entity.func_174813_aQ()).field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
/* 129 */         GlStateManager.func_179094_E();
/* 130 */         GlStateManager.func_179147_l();
/* 131 */         GlStateManager.func_179097_i();
/* 132 */         GlStateManager.func_179120_a(770, 771, 0, 1);
/* 133 */         GlStateManager.func_179090_x();
/* 134 */         GlStateManager.func_179132_a(false);
/* 135 */         GL11.glEnable(2848);
/* 136 */         GL11.glHint(3154, 4354);
/* 137 */         GL11.glLineWidth(1.0F);
/* 138 */         RenderGlobal.func_189696_b(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F) : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
/* 139 */         GL11.glDisable(2848);
/* 140 */         GlStateManager.func_179132_a(true);
/* 141 */         GlStateManager.func_179126_j();
/* 142 */         GlStateManager.func_179098_w();
/* 143 */         GlStateManager.func_179084_k();
/* 144 */         GlStateManager.func_179121_F();
/* 145 */         RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
/* 146 */         if (++i < 50);
/*     */       } 
/*     */     } 
/*     */     
/* 150 */     if (((Boolean)this.xpbottles.getValue()).booleanValue()) {
/* 151 */       int i = 0;
/* 152 */       for (Entity entity : mc.field_71441_e.field_72996_f) {
/* 153 */         if (!(entity instanceof net.minecraft.entity.item.EntityExpBottle) || mc.field_71439_g.func_70068_e(entity) >= 2500.0D)
/* 154 */           continue;  Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.func_184121_ak());
/* 155 */         AxisAlignedBB bb = new AxisAlignedBB((entity.func_174813_aQ()).field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, (entity.func_174813_aQ()).field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, (entity.func_174813_aQ()).field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, (entity.func_174813_aQ()).field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
/* 156 */         GlStateManager.func_179094_E();
/* 157 */         GlStateManager.func_179147_l();
/* 158 */         GlStateManager.func_179097_i();
/* 159 */         GlStateManager.func_179120_a(770, 771, 0, 1);
/* 160 */         GlStateManager.func_179090_x();
/* 161 */         GlStateManager.func_179132_a(false);
/* 162 */         GL11.glEnable(2848);
/* 163 */         GL11.glHint(3154, 4354);
/* 164 */         GL11.glLineWidth(1.0F);
/* 165 */         RenderGlobal.func_189696_b(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F) : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
/* 166 */         GL11.glDisable(2848);
/* 167 */         GlStateManager.func_179132_a(true);
/* 168 */         GlStateManager.func_179126_j();
/* 169 */         GlStateManager.func_179098_w();
/* 170 */         GlStateManager.func_179084_k();
/* 171 */         GlStateManager.func_179121_F();
/* 172 */         RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
/* 173 */         if (++i < 50);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRenderModel(RenderEntityModelEvent event) {
/* 180 */     if (event.getStage() != 0 || event.entity == null || (event.entity.func_82150_aj() && !((Boolean)this.invisibles.getValue()).booleanValue()) || (!((Boolean)this.self.getValue()).booleanValue() && event.entity.equals(mc.field_71439_g)) || (!((Boolean)this.players.getValue()).booleanValue() && event.entity instanceof net.minecraft.entity.player.EntityPlayer) || (!((Boolean)this.animals.getValue()).booleanValue() && EntityUtil.isPassive(event.entity)) || (!((Boolean)this.mobs.getValue()).booleanValue() && !EntityUtil.isPassive(event.entity) && !(event.entity instanceof net.minecraft.entity.player.EntityPlayer))) {
/*     */       return;
/*     */     }
/* 183 */     Color color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : EntityUtil.getColor(event.entity, ((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue(), ((Boolean)this.colorFriends.getValue()).booleanValue());
/* 184 */     boolean fancyGraphics = mc.field_71474_y.field_74347_j;
/* 185 */     mc.field_71474_y.field_74347_j = false;
/* 186 */     float gamma = mc.field_71474_y.field_74333_Y;
/* 187 */     mc.field_71474_y.field_74333_Y = 10000.0F;
/* 188 */     if (((Boolean)this.onTop.getValue()).booleanValue() && (!Chams.getInstance().isEnabled() || !((Boolean)(Chams.getInstance()).colored.getValue()).booleanValue())) {
/* 189 */       event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
/*     */     }
/* 191 */     if (this.mode.getValue() == Mode.OUTLINE) {
/* 192 */       RenderUtil.renderOne(((Float)this.lineWidth.getValue()).floatValue());
/* 193 */       event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
/* 194 */       GlStateManager.func_187441_d(((Float)this.lineWidth.getValue()).floatValue());
/* 195 */       RenderUtil.renderTwo();
/* 196 */       event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
/* 197 */       GlStateManager.func_187441_d(((Float)this.lineWidth.getValue()).floatValue());
/* 198 */       RenderUtil.renderThree();
/* 199 */       RenderUtil.renderFour(color);
/* 200 */       event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
/* 201 */       GlStateManager.func_187441_d(((Float)this.lineWidth.getValue()).floatValue());
/* 202 */       RenderUtil.renderFive();
/*     */     } else {
/* 204 */       GL11.glPushMatrix();
/* 205 */       GL11.glPushAttrib(1048575);
/* 206 */       if (this.mode.getValue() == Mode.WIREFRAME) {
/* 207 */         GL11.glPolygonMode(1032, 6913);
/*     */       } else {
/* 209 */         GL11.glPolygonMode(1028, 6913);
/*     */       } 
/* 211 */       GL11.glDisable(3553);
/* 212 */       GL11.glDisable(2896);
/* 213 */       GL11.glDisable(2929);
/* 214 */       GL11.glEnable(2848);
/* 215 */       GL11.glEnable(3042);
/* 216 */       GlStateManager.func_179112_b(770, 771);
/* 217 */       GlStateManager.func_179131_c(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
/* 218 */       GlStateManager.func_187441_d(((Float)this.lineWidth.getValue()).floatValue());
/* 219 */       event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
/* 220 */       GL11.glPopAttrib();
/* 221 */       GL11.glPopMatrix();
/*     */     } 
/* 223 */     if (!((Boolean)this.onTop.getValue()).booleanValue() && (!Chams.getInstance().isEnabled() || !((Boolean)(Chams.getInstance()).colored.getValue()).booleanValue())) {
/* 224 */       event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
/*     */     }
/*     */     try {
/* 227 */       mc.field_71474_y.field_74347_j = fancyGraphics;
/* 228 */       mc.field_71474_y.field_74333_Y = gamma;
/* 229 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 232 */     event.setCanceled(true);
/*     */   }
/*     */   
/*     */   public enum Mode {
/* 236 */     WIREFRAME,
/* 237 */     OUTLINE;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\render\ESP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */