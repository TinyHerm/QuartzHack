/*     */ package me.mohalk.banzem.features.modules.client;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.Render2DEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.combat.AutoCrystal;
/*     */ import me.mohalk.banzem.features.modules.combat.Killaura;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.ColorUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.client.entity.EntityPlayerSP;
/*     */ import net.minecraft.client.gui.ScaledResolution;
/*     */ import net.minecraft.client.gui.inventory.GuiInventory;
/*     */ import net.minecraft.client.renderer.DestroyBlockProgress;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.client.renderer.OpenGlHelper;
/*     */ import net.minecraft.client.renderer.RenderHelper;
/*     */ import net.minecraft.client.renderer.entity.RenderManager;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.MobEffects;
/*     */ import net.minecraft.inventory.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.util.NonNullList;
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ public class Components extends Module {
/*  44 */   private static final ResourceLocation box = new ResourceLocation("eralp232/transparent.png");
/*  45 */   public static ResourceLocation logo = new ResourceLocation("eralp232/quartzhack.png");
/*  46 */   public Setting<Boolean> inventory = register(new Setting("Inventory", Boolean.valueOf(false)));
/*  47 */   public Setting<Integer> invX = register(new Setting("InvX", Integer.valueOf(564), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
/*  48 */   public Setting<Integer> invY = register(new Setting("InvY", Integer.valueOf(467), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
/*  49 */   public Setting<Integer> fineinvX = register(new Setting("InvFineX", Integer.valueOf(0), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
/*  50 */   public Setting<Integer> fineinvY = register(new Setting("InvFineY", Integer.valueOf(0), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
/*  51 */   public Setting<Boolean> renderXCarry = register(new Setting("RenderXCarry", Boolean.valueOf(false), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
/*  52 */   public Setting<Integer> invH = register(new Setting("InvH", Integer.valueOf(3), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
/*  53 */   public Setting<Boolean> holeHud = register(new Setting("HoleHUD", Boolean.valueOf(false)));
/*  54 */   public Setting<Integer> holeX = register(new Setting("HoleX", Integer.valueOf(279), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.holeHud.getValue()).booleanValue()));
/*  55 */   public Setting<Integer> holeY = register(new Setting("HoleY", Integer.valueOf(485), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.holeHud.getValue()).booleanValue()));
/*  56 */   public Setting<Compass> compass = register(new Setting("Compass", Compass.NONE));
/*  57 */   public Setting<Integer> compassX = register(new Setting("CompX", Integer.valueOf(472), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.compass.getValue() != Compass.NONE)));
/*  58 */   public Setting<Integer> compassY = register(new Setting("CompY", Integer.valueOf(424), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.compass.getValue() != Compass.NONE)));
/*  59 */   public Setting<Integer> scale = register(new Setting("Scale", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(10), v -> (this.compass.getValue() != Compass.NONE)));
/*  60 */   public Setting<Boolean> playerViewer = register(new Setting("PlayerViewer", Boolean.valueOf(false)));
/*  61 */   public Setting<Integer> playerViewerX = register(new Setting("PlayerX", Integer.valueOf(752), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.playerViewer.getValue()).booleanValue()));
/*  62 */   public Setting<Integer> playerViewerY = register(new Setting("PlayerY", Integer.valueOf(497), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.playerViewer.getValue()).booleanValue()));
/*  63 */   public Setting<Float> playerScale = register(new Setting("PlayerScale", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(2.0F), v -> ((Boolean)this.playerViewer.getValue()).booleanValue()));
/*  64 */   public Setting<Boolean> imageLogo = register(new Setting("ImageLogo", Boolean.valueOf(false)));
/*  65 */   public Setting<Integer> imageX = register(new Setting("ImageX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
/*  66 */   public Setting<Integer> imageY = register(new Setting("ImageY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
/*  67 */   public Setting<Integer> imageWidth = register(new Setting("ImageWidth", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
/*  68 */   public Setting<Integer> imageHeight = register(new Setting("ImageHeight", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
/*  69 */   public Setting<Boolean> targetHud = register(new Setting("TargetHud", Boolean.valueOf(false)));
/*  70 */   public Setting<Boolean> targetHudBackground = register(new Setting("TargetHudBackground", Boolean.valueOf(true), v -> ((Boolean)this.targetHud.getValue()).booleanValue()));
/*  71 */   public Setting<Integer> targetHudX = register(new Setting("TargetHudX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.targetHud.getValue()).booleanValue()));
/*  72 */   public Setting<Integer> targetHudY = register(new Setting("TargetHudY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.targetHud.getValue()).booleanValue()));
/*  73 */   public Setting<TargetHudDesign> design = register(new Setting("Design", TargetHudDesign.NORMAL, v -> ((Boolean)this.targetHud.getValue()).booleanValue()));
/*  74 */   public Setting<Boolean> clock = register(new Setting("Clock", Boolean.valueOf(true)));
/*  75 */   public Setting<Boolean> clockFill = register(new Setting("ClockFill", Boolean.valueOf(true)));
/*  76 */   public Setting<Float> clockX = register(new Setting("ClockX", Float.valueOf(2.0F), Float.valueOf(0.0F), Float.valueOf(1000.0F), v -> ((Boolean)this.clock.getValue()).booleanValue()));
/*  77 */   public Setting<Float> clockY = register(new Setting("ClockY", Float.valueOf(2.0F), Float.valueOf(0.0F), Float.valueOf(1000.0F), v -> ((Boolean)this.clock.getValue()).booleanValue()));
/*  78 */   public Setting<Float> clockRadius = register(new Setting("ClockRadius", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(100.0F), v -> ((Boolean)this.clock.getValue()).booleanValue()));
/*  79 */   public Setting<Float> clockLineWidth = register(new Setting("ClockLineWidth", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(5.0F), v -> ((Boolean)this.clock.getValue()).booleanValue()));
/*  80 */   public Setting<Integer> clockSlices = register(new Setting("ClockSlices", Integer.valueOf(360), Integer.valueOf(1), Integer.valueOf(720), v -> ((Boolean)this.clock.getValue()).booleanValue()));
/*  81 */   public Setting<Integer> clockLoops = register(new Setting("ClockLoops", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(720), v -> ((Boolean)this.clock.getValue()).booleanValue()));
/*  82 */   private final Map<EntityPlayer, Map<Integer, ItemStack>> hotbarMap = new HashMap<>();
/*     */   
/*     */   public Components() {
/*  85 */     super("Components", "HudComponents", Module.Category.CLIENT, false, false, true);
/*     */   }
/*     */   
/*     */   public static EntityPlayer getClosestEnemy() {
/*  89 */     EntityPlayer closestPlayer = null;
/*  90 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*  91 */       if (player == mc.field_71439_g || Banzem.friendManager.isFriend(player))
/*  92 */         continue;  if (closestPlayer == null) {
/*  93 */         closestPlayer = player;
/*     */         continue;
/*     */       } 
/*  96 */       if (mc.field_71439_g.func_70068_e((Entity)player) >= mc.field_71439_g.func_70068_e((Entity)closestPlayer))
/*     */         continue; 
/*  98 */       closestPlayer = player;
/*     */     } 
/* 100 */     return closestPlayer;
/*     */   }
/*     */   
/*     */   private static double getPosOnCompass(Direction dir) {
/* 104 */     double yaw = Math.toRadians(MathHelper.func_76142_g(mc.field_71439_g.field_70177_z));
/* 105 */     int index = dir.ordinal();
/* 106 */     return yaw + index * 1.5707963267948966D;
/*     */   }
/*     */   
/*     */   private static void preboxrender() {
/* 110 */     GL11.glPushMatrix();
/* 111 */     GlStateManager.func_179094_E();
/* 112 */     GlStateManager.func_179118_c();
/* 113 */     GlStateManager.func_179086_m(256);
/* 114 */     GlStateManager.func_179147_l();
/* 115 */     GlStateManager.func_179131_c(255.0F, 255.0F, 255.0F, 255.0F);
/*     */   }
/*     */   
/*     */   private static void postboxrender() {
/* 119 */     GlStateManager.func_179084_k();
/* 120 */     GlStateManager.func_179097_i();
/* 121 */     GlStateManager.func_179140_f();
/* 122 */     GlStateManager.func_179126_j();
/* 123 */     GlStateManager.func_179141_d();
/* 124 */     GlStateManager.func_179121_F();
/* 125 */     GL11.glPopMatrix();
/*     */   }
/*     */   
/*     */   private static void preitemrender() {
/* 129 */     GL11.glPushMatrix();
/* 130 */     GL11.glDepthMask(true);
/* 131 */     GlStateManager.func_179086_m(256);
/* 132 */     GlStateManager.func_179097_i();
/* 133 */     GlStateManager.func_179126_j();
/* 134 */     RenderHelper.func_74519_b();
/* 135 */     GlStateManager.func_179152_a(1.0F, 1.0F, 0.01F);
/*     */   }
/*     */   
/*     */   private static void postitemrender() {
/* 139 */     GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F);
/* 140 */     RenderHelper.func_74518_a();
/* 141 */     GlStateManager.func_179141_d();
/* 142 */     GlStateManager.func_179084_k();
/* 143 */     GlStateManager.func_179140_f();
/* 144 */     GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
/* 145 */     GlStateManager.func_179097_i();
/* 146 */     GlStateManager.func_179126_j();
/* 147 */     GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
/* 148 */     GL11.glPopMatrix();
/*     */   }
/*     */   
/*     */   public static void drawCompleteImage(int posX, int posY, int width, int height) {
/* 152 */     GL11.glPushMatrix();
/* 153 */     GL11.glTranslatef(posX, posY, 0.0F);
/* 154 */     GL11.glBegin(7);
/* 155 */     GL11.glTexCoord2f(0.0F, 0.0F);
/* 156 */     GL11.glVertex3f(0.0F, 0.0F, 0.0F);
/* 157 */     GL11.glTexCoord2f(0.0F, 1.0F);
/* 158 */     GL11.glVertex3f(0.0F, height, 0.0F);
/* 159 */     GL11.glTexCoord2f(1.0F, 1.0F);
/* 160 */     GL11.glVertex3f(width, height, 0.0F);
/* 161 */     GL11.glTexCoord2f(1.0F, 0.0F);
/* 162 */     GL11.glVertex3f(width, 0.0F, 0.0F);
/* 163 */     GL11.glEnd();
/* 164 */     GL11.glPopMatrix();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRender2D(Render2DEvent event) {
/* 169 */     if (fullNullCheck()) {
/*     */       return;
/*     */     }
/* 172 */     if (((Boolean)this.playerViewer.getValue()).booleanValue()) {
/* 173 */       drawPlayer();
/*     */     }
/* 175 */     if (this.compass.getValue() != Compass.NONE) {
/* 176 */       drawCompass();
/*     */     }
/* 178 */     if (((Boolean)this.holeHud.getValue()).booleanValue()) {
/* 179 */       drawOverlay(event.partialTicks);
/*     */     }
/* 181 */     if (((Boolean)this.inventory.getValue()).booleanValue()) {
/* 182 */       renderInventory();
/*     */     }
/* 184 */     if (((Boolean)this.imageLogo.getValue()).booleanValue()) {
/* 185 */       drawImageLogo();
/*     */     }
/* 187 */     if (((Boolean)this.targetHud.getValue()).booleanValue()) {
/* 188 */       drawTargetHud(event.partialTicks);
/*     */     }
/* 190 */     if (((Boolean)this.clock.getValue()).booleanValue()) {
/* 191 */       RenderUtil.drawClock(((Float)this.clockX.getValue()).floatValue(), ((Float)this.clockY.getValue()).floatValue(), ((Float)this.clockRadius.getValue()).floatValue(), ((Integer)this.clockSlices.getValue()).intValue(), ((Integer)this.clockLoops.getValue()).intValue(), ((Float)this.clockLineWidth.getValue()).floatValue(), ((Boolean)this.clockFill.getValue()).booleanValue(), new Color(255, 0, 0, 255));
/*     */     }
/*     */   }
/*     */   
/*     */   public void drawTargetHud(float partialTicks) {
/* 196 */     if (this.design.getValue() == TargetHudDesign.NORMAL) {
/* 197 */       EntityPlayer target = (AutoCrystal.target != null) ? AutoCrystal.target : ((Killaura.target instanceof EntityPlayer) ? (EntityPlayer)Killaura.target : getClosestEnemy());
/* 198 */       if (target == null) {
/*     */         return;
/*     */       }
/* 201 */       if (((Boolean)this.targetHudBackground.getValue()).booleanValue()) {
/* 202 */         RenderUtil.drawRectangleCorrectly(((Integer)this.targetHudX.getValue()).intValue(), ((Integer)this.targetHudY.getValue()).intValue(), 210, 100, ColorUtil.toRGBA(20, 20, 20, 160));
/*     */       }
/* 204 */       GlStateManager.func_179101_C();
/* 205 */       GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
/* 206 */       GlStateManager.func_179090_x();
/* 207 */       GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
/* 208 */       GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/*     */       try {
/* 210 */         GuiInventory.func_147046_a(((Integer)this.targetHudX.getValue()).intValue() + 30, ((Integer)this.targetHudY.getValue()).intValue() + 90, 45, 0.0F, 0.0F, (EntityLivingBase)target);
/* 211 */       } catch (Exception e) {
/* 212 */         e.printStackTrace();
/*     */       } 
/* 214 */       GlStateManager.func_179091_B();
/* 215 */       GlStateManager.func_179098_w();
/* 216 */       GlStateManager.func_179147_l();
/* 217 */       GlStateManager.func_179120_a(770, 771, 1, 0);
/* 218 */       this.renderer.drawStringWithShadow(target.func_70005_c_(), (((Integer)this.targetHudX.getValue()).intValue() + 60), (((Integer)this.targetHudY.getValue()).intValue() + 10), ColorUtil.toRGBA(255, 0, 0, 255));
/* 219 */       float health = target.func_110143_aJ() + target.func_110139_bj();
/* 220 */       int healthColor = (health >= 16.0F) ? ColorUtil.toRGBA(0, 255, 0, 255) : ((health >= 10.0F) ? ColorUtil.toRGBA(255, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
/* 221 */       DecimalFormat df = new DecimalFormat("##.#");
/* 222 */       this.renderer.drawStringWithShadow(df.format((target.func_110143_aJ() + target.func_110139_bj())), (((Integer)this.targetHudX.getValue()).intValue() + 60 + this.renderer.getStringWidth(target.func_70005_c_() + "  ")), (((Integer)this.targetHudY.getValue()).intValue() + 10), healthColor);
/* 223 */       Integer ping = Integer.valueOf(EntityUtil.isFakePlayer(target) ? 0 : ((Util.mc.func_147114_u().func_175102_a(target.func_110124_au()) == null) ? 0 : Util.mc.func_147114_u().func_175102_a(target.func_110124_au()).func_178853_c()));
/* 224 */       int color = (ping.intValue() >= 100) ? ColorUtil.toRGBA(0, 255, 0, 255) : ((ping.intValue() > 50) ? ColorUtil.toRGBA(255, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
/* 225 */       this.renderer.drawStringWithShadow("Ping: " + ((ping == null) ? 0 : ping.intValue()), (((Integer)this.targetHudX.getValue()).intValue() + 60), (((Integer)this.targetHudY.getValue()).intValue() + this.renderer.getFontHeight() + 20), color);
/* 226 */       this.renderer.drawStringWithShadow("Pops: " + Banzem.totemPopManager.getTotemPops(target), (((Integer)this.targetHudX.getValue()).intValue() + 60), (((Integer)this.targetHudY.getValue()).intValue() + this.renderer.getFontHeight() * 2 + 30), ColorUtil.toRGBA(255, 0, 0, 255));
/* 227 */       GlStateManager.func_179098_w();
/* 228 */       int iteration = 0;
/* 229 */       int i = ((Integer)this.targetHudX.getValue()).intValue() + 50;
/* 230 */       int y = ((Integer)this.targetHudY.getValue()).intValue() + this.renderer.getFontHeight() * 3 + 44;
/* 231 */       for (ItemStack is : target.field_71071_by.field_70460_b) {
/* 232 */         iteration++;
/* 233 */         if (is.func_190926_b())
/* 234 */           continue;  int x = i - 90 + (9 - iteration) * 20 + 2;
/* 235 */         GlStateManager.func_179126_j();
/* 236 */         RenderUtil.itemRender.field_77023_b = 200.0F;
/* 237 */         RenderUtil.itemRender.func_180450_b(is, x, y);
/* 238 */         RenderUtil.itemRender.func_180453_a(mc.field_71466_p, is, x, y, "");
/* 239 */         RenderUtil.itemRender.field_77023_b = 0.0F;
/* 240 */         GlStateManager.func_179098_w();
/* 241 */         GlStateManager.func_179140_f();
/* 242 */         GlStateManager.func_179097_i();
/* 243 */         String s = (is.func_190916_E() > 1) ? (is.func_190916_E() + "") : "";
/* 244 */         this.renderer.drawStringWithShadow(s, (x + 19 - 2 - this.renderer.getStringWidth(s)), (y + 9), 16777215);
/* 245 */         int dmg = 0;
/* 246 */         int itemDurability = is.func_77958_k() - is.func_77952_i();
/* 247 */         float green = (is.func_77958_k() - is.func_77952_i()) / is.func_77958_k();
/* 248 */         float red = 1.0F - green;
/* 249 */         dmg = 100 - (int)(red * 100.0F);
/* 250 */         this.renderer.drawStringWithShadow(dmg + "", (x + 8) - this.renderer.getStringWidth(dmg + "") / 2.0F, (y - 5), ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0));
/*     */       } 
/* 252 */       drawOverlay(partialTicks, (Entity)target, ((Integer)this.targetHudX.getValue()).intValue() + 150, ((Integer)this.targetHudY.getValue()).intValue() + 6);
/* 253 */       this.renderer.drawStringWithShadow("Strength", (((Integer)this.targetHudX.getValue()).intValue() + 150), (((Integer)this.targetHudY.getValue()).intValue() + 60), target.func_70644_a(MobEffects.field_76420_g) ? ColorUtil.toRGBA(0, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
/* 254 */       this.renderer.drawStringWithShadow("Weakness", (((Integer)this.targetHudX.getValue()).intValue() + 150), (((Integer)this.targetHudY.getValue()).intValue() + this.renderer.getFontHeight() + 70), target.func_70644_a(MobEffects.field_76437_t) ? ColorUtil.toRGBA(0, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
/* 255 */     } else if (this.design.getValue() == TargetHudDesign.COMPACT) {
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onReceivePacket(PacketEvent.Receive event) {}
/*     */   
/*     */   public void drawImageLogo() {
/* 265 */     GlStateManager.func_179098_w();
/* 266 */     GlStateManager.func_179084_k();
/* 267 */     Util.mc.func_110434_K().func_110577_a(logo);
/* 268 */     drawCompleteImage(((Integer)this.imageX.getValue()).intValue(), ((Integer)this.imageY.getValue()).intValue(), ((Integer)this.imageWidth.getValue()).intValue(), ((Integer)this.imageHeight.getValue()).intValue());
/* 269 */     Util.mc.func_110434_K().func_147645_c(logo);
/* 270 */     GlStateManager.func_179147_l();
/* 271 */     GlStateManager.func_179090_x();
/*     */   }
/*     */   
/*     */   public void drawCompass() {
/* 275 */     ScaledResolution sr = new ScaledResolution(Util.mc);
/* 276 */     if (this.compass.getValue() == Compass.LINE) {
/* 277 */       float playerYaw = mc.field_71439_g.field_70177_z;
/* 278 */       float rotationYaw = MathUtil.wrap(playerYaw);
/* 279 */       RenderUtil.drawRect(((Integer)this.compassX.getValue()).intValue(), ((Integer)this.compassY.getValue()).intValue(), (((Integer)this.compassX.getValue()).intValue() + 100), (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight()), 1963986960);
/* 280 */       RenderUtil.glScissor(((Integer)this.compassX.getValue()).intValue(), ((Integer)this.compassY.getValue()).intValue(), (((Integer)this.compassX.getValue()).intValue() + 100), (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight()), sr);
/* 281 */       GL11.glEnable(3089);
/* 282 */       float zeroZeroYaw = MathUtil.wrap((float)(Math.atan2(0.0D - mc.field_71439_g.field_70161_v, 0.0D - mc.field_71439_g.field_70165_t) * 180.0D / Math.PI) - 90.0F);
/* 283 */       RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + zeroZeroYaw, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + zeroZeroYaw, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -61424);
/* 284 */       RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 45.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 45.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
/* 285 */       RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 45.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 45.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
/* 286 */       RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 135.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 135.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
/* 287 */       RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 135.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 135.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
/* 288 */       this.renderer.drawStringWithShadow("n", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 180.0F - this.renderer.getStringWidth("n") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
/* 289 */       this.renderer.drawStringWithShadow("n", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 180.0F - this.renderer.getStringWidth("n") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
/* 290 */       this.renderer.drawStringWithShadow("e", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 90.0F - this.renderer.getStringWidth("e") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
/* 291 */       this.renderer.drawStringWithShadow("s", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - this.renderer.getStringWidth("s") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
/* 292 */       this.renderer.drawStringWithShadow("w", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 90.0F - this.renderer.getStringWidth("w") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
/* 293 */       RenderUtil.drawLine((((Integer)this.compassX.getValue()).intValue() + 50), (((Integer)this.compassY.getValue()).intValue() + 1), (((Integer)this.compassX.getValue()).intValue() + 50), (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 1), 2.0F, -7303024);
/* 294 */       GL11.glDisable(3089);
/*     */     } else {
/* 296 */       double centerX = ((Integer)this.compassX.getValue()).intValue();
/* 297 */       double centerY = ((Integer)this.compassY.getValue()).intValue();
/* 298 */       for (Direction dir : Direction.values()) {
/* 299 */         double rad = getPosOnCompass(dir);
/* 300 */         this.renderer.drawStringWithShadow(dir.name(), (float)(centerX + getX(rad)), (float)(centerY + getY(rad)), (dir == Direction.N) ? -65536 : -1);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void drawPlayer(EntityPlayer player, int x, int y) {
/* 306 */     EntityPlayer ent = player;
/* 307 */     GlStateManager.func_179094_E();
/* 308 */     GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
/* 309 */     RenderHelper.func_74519_b();
/* 310 */     GlStateManager.func_179141_d();
/* 311 */     GlStateManager.func_179103_j(7424);
/* 312 */     GlStateManager.func_179141_d();
/* 313 */     GlStateManager.func_179126_j();
/* 314 */     GlStateManager.func_179114_b(0.0F, 0.0F, 5.0F, 0.0F);
/* 315 */     GlStateManager.func_179142_g();
/* 316 */     GlStateManager.func_179094_E();
/* 317 */     GlStateManager.func_179109_b((((Integer)this.playerViewerX.getValue()).intValue() + 25), (((Integer)this.playerViewerY.getValue()).intValue() + 25), 50.0F);
/* 318 */     GlStateManager.func_179152_a(-50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue());
/* 319 */     GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
/* 320 */     GlStateManager.func_179114_b(135.0F, 0.0F, 1.0F, 0.0F);
/* 321 */     RenderHelper.func_74519_b();
/* 322 */     GlStateManager.func_179114_b(-135.0F, 0.0F, 1.0F, 0.0F);
/* 323 */     GlStateManager.func_179114_b(-((float)Math.atan((((Integer)this.playerViewerY.getValue()).intValue() / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
/* 324 */     GlStateManager.func_179109_b(0.0F, 0.0F, 0.0F);
/* 325 */     RenderManager rendermanager = Util.mc.func_175598_ae();
/* 326 */     rendermanager.func_178631_a(180.0F);
/* 327 */     rendermanager.func_178633_a(false);
/*     */     try {
/* 329 */       rendermanager.func_188391_a((Entity)ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
/* 330 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 333 */     rendermanager.func_178633_a(true);
/* 334 */     GlStateManager.func_179121_F();
/* 335 */     RenderHelper.func_74518_a();
/* 336 */     GlStateManager.func_179101_C();
/* 337 */     GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
/* 338 */     GlStateManager.func_179090_x();
/* 339 */     GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
/* 340 */     GlStateManager.func_179143_c(515);
/* 341 */     GlStateManager.func_179117_G();
/* 342 */     GlStateManager.func_179097_i();
/* 343 */     GlStateManager.func_179121_F();
/*     */   }
/*     */   
/*     */   public void drawPlayer() {
/* 347 */     EntityPlayerSP ent = mc.field_71439_g;
/* 348 */     GlStateManager.func_179094_E();
/* 349 */     GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
/* 350 */     RenderHelper.func_74519_b();
/* 351 */     GlStateManager.func_179141_d();
/* 352 */     GlStateManager.func_179103_j(7424);
/* 353 */     GlStateManager.func_179141_d();
/* 354 */     GlStateManager.func_179126_j();
/* 355 */     GlStateManager.func_179114_b(0.0F, 0.0F, 5.0F, 0.0F);
/* 356 */     GlStateManager.func_179142_g();
/* 357 */     GlStateManager.func_179094_E();
/* 358 */     GlStateManager.func_179109_b((((Integer)this.playerViewerX.getValue()).intValue() + 25), (((Integer)this.playerViewerY.getValue()).intValue() + 25), 50.0F);
/* 359 */     GlStateManager.func_179152_a(-50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue());
/* 360 */     GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
/* 361 */     GlStateManager.func_179114_b(135.0F, 0.0F, 1.0F, 0.0F);
/* 362 */     RenderHelper.func_74519_b();
/* 363 */     GlStateManager.func_179114_b(-135.0F, 0.0F, 1.0F, 0.0F);
/* 364 */     GlStateManager.func_179114_b(-((float)Math.atan((((Integer)this.playerViewerY.getValue()).intValue() / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
/* 365 */     GlStateManager.func_179109_b(0.0F, 0.0F, 0.0F);
/* 366 */     RenderManager rendermanager = Util.mc.func_175598_ae();
/* 367 */     rendermanager.func_178631_a(180.0F);
/* 368 */     rendermanager.func_178633_a(false);
/*     */     try {
/* 370 */       rendermanager.func_188391_a((Entity)ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
/* 371 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 374 */     rendermanager.func_178633_a(true);
/* 375 */     GlStateManager.func_179121_F();
/* 376 */     RenderHelper.func_74518_a();
/* 377 */     GlStateManager.func_179101_C();
/* 378 */     GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
/* 379 */     GlStateManager.func_179090_x();
/* 380 */     GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
/* 381 */     GlStateManager.func_179143_c(515);
/* 382 */     GlStateManager.func_179117_G();
/* 383 */     GlStateManager.func_179097_i();
/* 384 */     GlStateManager.func_179121_F();
/*     */   }
/*     */   
/*     */   private double getX(double rad) {
/* 388 */     return Math.sin(rad) * (((Integer)this.scale.getValue()).intValue() * 10);
/*     */   }
/*     */   
/*     */   private double getY(double rad) {
/* 392 */     double epicPitch = MathHelper.func_76131_a(mc.field_71439_g.field_70125_A + 30.0F, -90.0F, 90.0F);
/* 393 */     double pitchRadians = Math.toRadians(epicPitch);
/* 394 */     return Math.cos(rad) * Math.sin(pitchRadians) * (((Integer)this.scale.getValue()).intValue() * 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawOverlay(float partialTicks) {
/* 404 */     float yaw = 0.0F;
/* 405 */     int dir = MathHelper.func_76128_c((mc.field_71439_g.field_70177_z * 4.0F / 360.0F) + 0.5D) & 0x3;
/* 406 */     switch (dir) {
/*     */       case 1:
/* 408 */         yaw = 90.0F;
/*     */         break;
/*     */       
/*     */       case 2:
/* 412 */         yaw = -180.0F;
/*     */         break;
/*     */       
/*     */       case 3:
/* 416 */         yaw = -90.0F;
/*     */         break;
/*     */     } 
/*     */     
/* 420 */     BlockPos northPos = traceToBlock(partialTicks, yaw);
/* 421 */     Block north = getBlock(northPos);
/* 422 */     if (north != null && north != Blocks.field_150350_a) {
/* 423 */       int damage = getBlockDamage(northPos);
/* 424 */       if (damage != 0) {
/* 425 */         RenderUtil.drawRect((((Integer)this.holeX.getValue()).intValue() + 16), ((Integer)this.holeY.getValue()).intValue(), (((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 16), 1627324416);
/*     */       }
/* 427 */       drawBlock(north, (((Integer)this.holeX.getValue()).intValue() + 16), ((Integer)this.holeY.getValue()).intValue());
/*     */     }  BlockPos southPos; Block south;
/* 429 */     if ((south = getBlock(southPos = traceToBlock(partialTicks, yaw - 180.0F))) != null && south != Blocks.field_150350_a) {
/* 430 */       int damage = getBlockDamage(southPos);
/* 431 */       if (damage != 0) {
/* 432 */         RenderUtil.drawRect((((Integer)this.holeX.getValue()).intValue() + 16), (((Integer)this.holeY.getValue()).intValue() + 32), (((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 48), 1627324416);
/*     */       }
/* 434 */       drawBlock(south, (((Integer)this.holeX.getValue()).intValue() + 16), (((Integer)this.holeY.getValue()).intValue() + 32));
/*     */     }  BlockPos eastPos; Block east;
/* 436 */     if ((east = getBlock(eastPos = traceToBlock(partialTicks, yaw + 90.0F))) != null && east != Blocks.field_150350_a) {
/* 437 */       int damage = getBlockDamage(eastPos);
/* 438 */       if (damage != 0) {
/* 439 */         RenderUtil.drawRect((((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 16), (((Integer)this.holeX.getValue()).intValue() + 48), (((Integer)this.holeY.getValue()).intValue() + 32), 1627324416);
/*     */       }
/* 441 */       drawBlock(east, (((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 16));
/*     */     }  BlockPos westPos; Block west;
/* 443 */     if ((west = getBlock(westPos = traceToBlock(partialTicks, yaw - 90.0F))) != null && west != Blocks.field_150350_a) {
/* 444 */       int damage = getBlockDamage(westPos);
/* 445 */       if (damage != 0) {
/* 446 */         RenderUtil.drawRect(((Integer)this.holeX.getValue()).intValue(), (((Integer)this.holeY.getValue()).intValue() + 16), (((Integer)this.holeX.getValue()).intValue() + 16), (((Integer)this.holeY.getValue()).intValue() + 32), 1627324416);
/*     */       }
/* 448 */       drawBlock(west, ((Integer)this.holeX.getValue()).intValue(), (((Integer)this.holeY.getValue()).intValue() + 16));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawOverlay(float partialTicks, Entity player, int x, int y) {
/* 459 */     float yaw = 0.0F;
/* 460 */     int dir = MathHelper.func_76128_c((player.field_70177_z * 4.0F / 360.0F) + 0.5D) & 0x3;
/* 461 */     switch (dir) {
/*     */       case 1:
/* 463 */         yaw = 90.0F;
/*     */         break;
/*     */       
/*     */       case 2:
/* 467 */         yaw = -180.0F;
/*     */         break;
/*     */       
/*     */       case 3:
/* 471 */         yaw = -90.0F;
/*     */         break;
/*     */     } 
/*     */     
/* 475 */     BlockPos northPos = traceToBlock(partialTicks, yaw, player);
/* 476 */     Block north = getBlock(northPos);
/* 477 */     if (north != null && north != Blocks.field_150350_a) {
/* 478 */       int damage = getBlockDamage(northPos);
/* 479 */       if (damage != 0) {
/* 480 */         RenderUtil.drawRect((x + 16), y, (x + 32), (y + 16), 1627324416);
/*     */       }
/* 482 */       drawBlock(north, (x + 16), y);
/*     */     }  BlockPos southPos; Block south;
/* 484 */     if ((south = getBlock(southPos = traceToBlock(partialTicks, yaw - 180.0F, player))) != null && south != Blocks.field_150350_a) {
/* 485 */       int damage = getBlockDamage(southPos);
/* 486 */       if (damage != 0) {
/* 487 */         RenderUtil.drawRect((x + 16), (y + 32), (x + 32), (y + 48), 1627324416);
/*     */       }
/* 489 */       drawBlock(south, (x + 16), (y + 32));
/*     */     }  BlockPos eastPos; Block east;
/* 491 */     if ((east = getBlock(eastPos = traceToBlock(partialTicks, yaw + 90.0F, player))) != null && east != Blocks.field_150350_a) {
/* 492 */       int damage = getBlockDamage(eastPos);
/* 493 */       if (damage != 0) {
/* 494 */         RenderUtil.drawRect((x + 32), (y + 16), (x + 48), (y + 32), 1627324416);
/*     */       }
/* 496 */       drawBlock(east, (x + 32), (y + 16));
/*     */     }  BlockPos westPos; Block west;
/* 498 */     if ((west = getBlock(westPos = traceToBlock(partialTicks, yaw - 90.0F, player))) != null && west != Blocks.field_150350_a) {
/* 499 */       int damage = getBlockDamage(westPos);
/* 500 */       if (damage != 0) {
/* 501 */         RenderUtil.drawRect(x, (y + 16), (x + 16), (y + 32), 1627324416);
/*     */       }
/* 503 */       drawBlock(west, x, (y + 16));
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getBlockDamage(BlockPos pos) {
/* 508 */     for (DestroyBlockProgress destBlockProgress : mc.field_71438_f.field_72738_E.values()) {
/* 509 */       if (destBlockProgress.func_180246_b().func_177958_n() != pos.func_177958_n() || destBlockProgress.func_180246_b().func_177956_o() != pos.func_177956_o() || destBlockProgress.func_180246_b().func_177952_p() != pos.func_177952_p())
/*     */         continue; 
/* 511 */       return destBlockProgress.func_73106_e();
/*     */     } 
/* 513 */     return 0;
/*     */   }
/*     */   
/*     */   private BlockPos traceToBlock(float partialTicks, float yaw) {
/* 517 */     Vec3d pos = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, partialTicks);
/* 518 */     Vec3d dir = MathUtil.direction(yaw);
/* 519 */     return new BlockPos(pos.field_72450_a + dir.field_72450_a, pos.field_72448_b, pos.field_72449_c + dir.field_72449_c);
/*     */   }
/*     */   
/*     */   private BlockPos traceToBlock(float partialTicks, float yaw, Entity player) {
/* 523 */     Vec3d pos = EntityUtil.interpolateEntity(player, partialTicks);
/* 524 */     Vec3d dir = MathUtil.direction(yaw);
/* 525 */     return new BlockPos(pos.field_72450_a + dir.field_72450_a, pos.field_72448_b, pos.field_72449_c + dir.field_72449_c);
/*     */   }
/*     */   
/*     */   private Block getBlock(BlockPos pos) {
/* 529 */     Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
/* 530 */     if (block == Blocks.field_150357_h || block == Blocks.field_150343_Z) {
/* 531 */       return block;
/*     */     }
/* 533 */     return Blocks.field_150350_a;
/*     */   }
/*     */   
/*     */   private void drawBlock(Block block, float x, float y) {
/* 537 */     ItemStack stack = new ItemStack(block);
/* 538 */     GlStateManager.func_179094_E();
/* 539 */     GlStateManager.func_179147_l();
/* 540 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/* 541 */     RenderHelper.func_74520_c();
/* 542 */     GlStateManager.func_179109_b(x, y, 0.0F);
/* 543 */     (mc.func_175599_af()).field_77023_b = 501.0F;
/* 544 */     Util.mc.func_175599_af().func_180450_b(stack, 0, 0);
/* 545 */     (mc.func_175599_af()).field_77023_b = 0.0F;
/* 546 */     RenderHelper.func_74518_a();
/* 547 */     GlStateManager.func_179084_k();
/* 548 */     GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/* 549 */     GlStateManager.func_179121_F();
/*     */   }
/*     */   
/*     */   public void renderInventory() {
/* 553 */     boxrender(((Integer)this.invX.getValue()).intValue() + ((Integer)this.fineinvX.getValue()).intValue(), ((Integer)this.invY.getValue()).intValue() + ((Integer)this.fineinvY.getValue()).intValue());
/* 554 */     itemrender(mc.field_71439_g.field_71071_by.field_70462_a, ((Integer)this.invX.getValue()).intValue() + ((Integer)this.fineinvX.getValue()).intValue(), ((Integer)this.invY.getValue()).intValue() + ((Integer)this.fineinvY.getValue()).intValue());
/*     */   }
/*     */   
/*     */   private void boxrender(int x, int y) {
/* 558 */     preboxrender();
/* 559 */     mc.field_71446_o.func_110577_a(box);
/* 560 */     RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
/* 561 */     RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + ((Integer)this.invH.getValue()).intValue(), 500);
/* 562 */     RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
/* 563 */     postboxrender();
/*     */   }
/*     */ 
/*     */   
/*     */   private void itemrender(NonNullList<ItemStack> items, int x, int y) {
/*     */     int i;
/* 569 */     for (i = 0; i < items.size() - 9; i++) {
/* 570 */       int iX = x + i % 9 * 18 + 8;
/* 571 */       int iY = y + i / 9 * 18 + 18;
/* 572 */       ItemStack itemStack = (ItemStack)items.get(i + 9);
/* 573 */       preitemrender();
/* 574 */       (mc.func_175599_af()).field_77023_b = 501.0F;
/* 575 */       RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
/* 576 */       RenderUtil.itemRender.func_180453_a(mc.field_71466_p, itemStack, iX, iY, null);
/* 577 */       (mc.func_175599_af()).field_77023_b = 0.0F;
/* 578 */       postitemrender();
/*     */     } 
/* 580 */     if (((Boolean)this.renderXCarry.getValue()).booleanValue())
/* 581 */       for (i = 1; i < 5; i++) {
/* 582 */         int iX = x + (i + 4) % 9 * 18 + 8;
/* 583 */         ItemStack itemStack = ((Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(i)).func_75211_c();
/* 584 */         if (itemStack != null && !itemStack.field_190928_g) {
/* 585 */           preitemrender();
/* 586 */           (mc.func_175599_af()).field_77023_b = 501.0F;
/* 587 */           RenderUtil.itemRender.func_180450_b(itemStack, iX, y + 1);
/* 588 */           RenderUtil.itemRender.func_180453_a(mc.field_71466_p, itemStack, iX, y + 1, null);
/* 589 */           (mc.func_175599_af()).field_77023_b = 0.0F;
/* 590 */           postitemrender();
/*     */         } 
/*     */       }  
/*     */   }
/*     */   
/*     */   public enum TargetHudDesign {
/* 596 */     NORMAL,
/* 597 */     COMPACT;
/*     */   }
/*     */   
/*     */   public enum Compass
/*     */   {
/* 602 */     NONE,
/* 603 */     CIRCLE,
/* 604 */     LINE;
/*     */   }
/*     */   
/*     */   private enum Direction
/*     */   {
/* 609 */     N,
/* 610 */     W,
/* 611 */     S,
/* 612 */     E;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\Components.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */