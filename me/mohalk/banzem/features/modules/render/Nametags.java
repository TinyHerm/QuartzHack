/*     */ package me.mohalk.banzem.features.modules.render;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Objects;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.Render3DEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.modules.client.Colors;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.DamageUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.RotationUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.network.NetHandlerPlayClient;
/*     */ import net.minecraft.client.renderer.BufferBuilder;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.client.renderer.RenderHelper;
/*     */ import net.minecraft.client.renderer.Tessellator;
/*     */ import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
/*     */ import net.minecraft.enchantment.Enchantment;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.NBTTagList;
/*     */ import net.minecraft.util.text.TextFormatting;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ 
/*     */ public class Nametags
/*     */   extends Module
/*     */ {
/*  33 */   private static Nametags INSTANCE = new Nametags();
/*  34 */   private final Setting<Boolean> health = register(new Setting("Health", Boolean.valueOf(true)));
/*  35 */   private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(true)));
/*  36 */   private final Setting<Float> scaling = register(new Setting("Size", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
/*  37 */   private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
/*  38 */   private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(true)));
/*  39 */   private final Setting<Boolean> totemPops = register(new Setting("TotemPops", Boolean.valueOf(true)));
/*  40 */   private final Setting<Boolean> gamemode = register(new Setting("Gamemode", Boolean.valueOf(false)));
/*  41 */   private final Setting<Boolean> entityID = register(new Setting("ID", Boolean.valueOf(false)));
/*  42 */   private final Setting<Boolean> rect = register(new Setting("Rectangle", Boolean.valueOf(true)));
/*  43 */   private final Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(false), v -> ((Boolean)this.rect.getValue()).booleanValue()));
/*  44 */   private final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false), v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  45 */   private final Setting<Integer> redSetting = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  46 */   private final Setting<Integer> greenSetting = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  47 */   private final Setting<Integer> blueSetting = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  48 */   private final Setting<Integer> alphaSetting = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  49 */   private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.5F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
/*  50 */   private final Setting<Boolean> sneak = register(new Setting("SneakColor", Boolean.valueOf(false)));
/*  51 */   private final Setting<Boolean> heldStackName = register(new Setting("StackName", Boolean.valueOf(false)));
/*  52 */   private final Setting<Boolean> whiter = register(new Setting("White", Boolean.valueOf(false)));
/*  53 */   private final Setting<Boolean> onlyFov = register(new Setting("OnlyFov", Boolean.valueOf(false)));
/*  54 */   private final Setting<Boolean> scaleing = register(new Setting("Scale", Boolean.valueOf(false)));
/*  55 */   private final Setting<Float> factor = register(new Setting("Factor", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(1.0F), v -> ((Boolean)this.scaleing.getValue()).booleanValue()));
/*  56 */   private final Setting<Boolean> smartScale = register(new Setting("SmartScale", Boolean.valueOf(false), v -> ((Boolean)this.scaleing.getValue()).booleanValue()));
/*     */   
/*     */   public Nametags() {
/*  59 */     super("Nametags", "Better Nametags", Module.Category.RENDER, false, false, false);
/*  60 */     setInstance();
/*     */   }
/*     */   
/*     */   public static Nametags getInstance() {
/*  64 */     if (INSTANCE == null) {
/*  65 */       INSTANCE = new Nametags();
/*     */     }
/*  67 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  71 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRender3D(Render3DEvent event) {
/*  76 */     if (!fullNullCheck()) {
/*  77 */       for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*  78 */         if (player == null || player.equals(mc.field_71439_g) || !player.func_70089_S() || (player.func_82150_aj() && !((Boolean)this.invisibles.getValue()).booleanValue()) || (((Boolean)this.onlyFov.getValue()).booleanValue() && !RotationUtil.isInFov((Entity)player)))
/*     */           continue; 
/*  80 */         double x = interpolate(player.field_70142_S, player.field_70165_t, event.getPartialTicks()) - (mc.func_175598_ae()).field_78725_b;
/*  81 */         double y = interpolate(player.field_70137_T, player.field_70163_u, event.getPartialTicks()) - (mc.func_175598_ae()).field_78726_c;
/*  82 */         double z = interpolate(player.field_70136_U, player.field_70161_v, event.getPartialTicks()) - (mc.func_175598_ae()).field_78723_d;
/*  83 */         renderNameTag(player, x, y, z, event.getPartialTicks());
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void drawRect(float x, float y, float w, float h, int color) {
/*  89 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/*  90 */     float red = (color >> 16 & 0xFF) / 255.0F;
/*  91 */     float green = (color >> 8 & 0xFF) / 255.0F;
/*  92 */     float blue = (color & 0xFF) / 255.0F;
/*  93 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  94 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/*  95 */     GlStateManager.func_179147_l();
/*  96 */     GlStateManager.func_179090_x();
/*  97 */     GlStateManager.func_187441_d(((Float)this.lineWidth.getValue()).floatValue());
/*  98 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/*  99 */     bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
/* 100 */     bufferbuilder.func_181662_b(x, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 101 */     bufferbuilder.func_181662_b(w, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 102 */     bufferbuilder.func_181662_b(w, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 103 */     bufferbuilder.func_181662_b(x, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 104 */     tessellator.func_78381_a();
/* 105 */     GlStateManager.func_179098_w();
/* 106 */     GlStateManager.func_179084_k();
/*     */   }
/*     */   
/*     */   public void drawOutlineRect(float x, float y, float w, float h, int color) {
/* 110 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/* 111 */     float red = (color >> 16 & 0xFF) / 255.0F;
/* 112 */     float green = (color >> 8 & 0xFF) / 255.0F;
/* 113 */     float blue = (color & 0xFF) / 255.0F;
/* 114 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 115 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 116 */     GlStateManager.func_179147_l();
/* 117 */     GlStateManager.func_179090_x();
/* 118 */     GlStateManager.func_187441_d(((Float)this.lineWidth.getValue()).floatValue());
/* 119 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/* 120 */     bufferbuilder.func_181668_a(2, DefaultVertexFormats.field_181706_f);
/* 121 */     bufferbuilder.func_181662_b(x, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 122 */     bufferbuilder.func_181662_b(w, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 123 */     bufferbuilder.func_181662_b(w, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 124 */     bufferbuilder.func_181662_b(x, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 125 */     tessellator.func_78381_a();
/* 126 */     GlStateManager.func_179098_w();
/* 127 */     GlStateManager.func_179084_k();
/*     */   }
/*     */   
/*     */   private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
/* 131 */     double tempY = y;
/* 132 */     tempY += player.func_70093_af() ? 0.5D : 0.7D;
/* 133 */     Entity camera = Util.mc.func_175606_aa();
/* 134 */     assert camera != null;
/* 135 */     double originalPositionX = camera.field_70165_t;
/* 136 */     double originalPositionY = camera.field_70163_u;
/* 137 */     double originalPositionZ = camera.field_70161_v;
/* 138 */     camera.field_70165_t = interpolate(camera.field_70169_q, camera.field_70165_t, delta);
/* 139 */     camera.field_70163_u = interpolate(camera.field_70167_r, camera.field_70163_u, delta);
/* 140 */     camera.field_70161_v = interpolate(camera.field_70166_s, camera.field_70161_v, delta);
/* 141 */     String displayTag = getDisplayTag(player);
/* 142 */     double distance = camera.func_70011_f(x + (mc.func_175598_ae()).field_78730_l, y + (mc.func_175598_ae()).field_78731_m, z + (mc.func_175598_ae()).field_78728_n);
/* 143 */     int width = this.renderer.getStringWidth(displayTag) / 2;
/* 144 */     double scale = (0.0018D + ((Float)this.scaling.getValue()).floatValue() * distance * ((Float)this.factor.getValue()).floatValue()) / 1000.0D;
/* 145 */     if (distance <= 8.0D && ((Boolean)this.smartScale.getValue()).booleanValue()) {
/* 146 */       scale = 0.0245D;
/*     */     }
/* 148 */     if (!((Boolean)this.scaleing.getValue()).booleanValue()) {
/* 149 */       scale = ((Float)this.scaling.getValue()).floatValue() / 100.0D;
/*     */     }
/* 151 */     GlStateManager.func_179094_E();
/* 152 */     RenderHelper.func_74519_b();
/* 153 */     GlStateManager.func_179088_q();
/* 154 */     GlStateManager.func_179136_a(1.0F, -1500000.0F);
/* 155 */     GlStateManager.func_179140_f();
/* 156 */     GlStateManager.func_179109_b((float)x, (float)tempY + 1.4F, (float)z);
/* 157 */     GlStateManager.func_179114_b(-(mc.func_175598_ae()).field_78735_i, 0.0F, 1.0F, 0.0F);
/* 158 */     GlStateManager.func_179114_b((mc.func_175598_ae()).field_78732_j, (mc.field_71474_y.field_74320_O == 2) ? -1.0F : 1.0F, 0.0F, 0.0F);
/* 159 */     GlStateManager.func_179139_a(-scale, -scale, scale);
/* 160 */     GlStateManager.func_179097_i();
/* 161 */     GlStateManager.func_179147_l();
/* 162 */     GlStateManager.func_179147_l();
/* 163 */     if (((Boolean)this.rect.getValue()).booleanValue()) {
/* 164 */       drawRect((-width - 2), -(this.renderer.getFontHeight() + 1), width + 2.0F, 1.5F, 1426063360);
/* 165 */       if (((Boolean)this.outline.getValue()).booleanValue()) {
/* 166 */         int color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : (new Color(((Integer)this.redSetting.getValue()).intValue(), ((Integer)this.greenSetting.getValue()).intValue(), ((Integer)this.blueSetting.getValue()).intValue(), ((Integer)this.alphaSetting.getValue()).intValue())).getRGB();
/* 167 */         drawOutlineRect((-width - 2), -(mc.field_71466_p.field_78288_b + 1), width + 2.0F, 1.5F, color);
/*     */       } 
/*     */     } 
/* 170 */     GlStateManager.func_179084_k();
/* 171 */     ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
/* 172 */     if (renderMainHand.func_77962_s() && (renderMainHand.func_77973_b() instanceof net.minecraft.item.ItemTool || renderMainHand.func_77973_b() instanceof net.minecraft.item.ItemArmor)) {
/* 173 */       renderMainHand.field_77994_a = 1;
/*     */     }
/* 175 */     if (((Boolean)this.heldStackName.getValue()).booleanValue() && !renderMainHand.field_190928_g && renderMainHand.func_77973_b() != Items.field_190931_a) {
/* 176 */       String stackName = renderMainHand.func_82833_r();
/* 177 */       int stackNameWidth = this.renderer.getStringWidth(stackName) / 2;
/* 178 */       GL11.glPushMatrix();
/* 179 */       GL11.glScalef(0.75F, 0.75F, 0.0F);
/* 180 */       this.renderer.drawStringWithShadow(stackName, -stackNameWidth, -(getBiggestArmorTag(player) + 20.0F), -1);
/* 181 */       GL11.glScalef(1.5F, 1.5F, 1.0F);
/* 182 */       GL11.glPopMatrix();
/*     */     } 
/* 184 */     if (((Boolean)this.armor.getValue()).booleanValue()) {
/* 185 */       GlStateManager.func_179094_E();
/* 186 */       int xOffset = -8;
/* 187 */       for (ItemStack stack : player.field_71071_by.field_70460_b) {
/* 188 */         if (stack == null)
/* 189 */           continue;  xOffset -= 8;
/*     */       } 
/* 191 */       xOffset -= 8;
/* 192 */       ItemStack renderOffhand = player.func_184592_cb().func_77946_l();
/* 193 */       if (renderOffhand.func_77962_s() && (renderOffhand.func_77973_b() instanceof net.minecraft.item.ItemTool || renderOffhand.func_77973_b() instanceof net.minecraft.item.ItemArmor)) {
/* 194 */         renderOffhand.field_77994_a = 1;
/*     */       }
/* 196 */       renderItemStack(renderOffhand, xOffset, -26);
/* 197 */       xOffset += 16;
/* 198 */       for (ItemStack stack : player.field_71071_by.field_70460_b) {
/* 199 */         if (stack == null)
/* 200 */           continue;  ItemStack armourStack = stack.func_77946_l();
/* 201 */         if (armourStack.func_77962_s() && (armourStack.func_77973_b() instanceof net.minecraft.item.ItemTool || armourStack.func_77973_b() instanceof net.minecraft.item.ItemArmor)) {
/* 202 */           armourStack.field_77994_a = 1;
/*     */         }
/* 204 */         renderItemStack(armourStack, xOffset, -26);
/* 205 */         xOffset += 16;
/*     */       } 
/* 207 */       renderItemStack(renderMainHand, xOffset, -26);
/* 208 */       GlStateManager.func_179121_F();
/*     */     } 
/* 210 */     this.renderer.drawStringWithShadow(displayTag, -width, -(this.renderer.getFontHeight() - 1), getDisplayColour(player));
/* 211 */     camera.field_70165_t = originalPositionX;
/* 212 */     camera.field_70163_u = originalPositionY;
/* 213 */     camera.field_70161_v = originalPositionZ;
/* 214 */     GlStateManager.func_179126_j();
/* 215 */     GlStateManager.func_179084_k();
/* 216 */     GlStateManager.func_179113_r();
/* 217 */     GlStateManager.func_179136_a(1.0F, 1500000.0F);
/* 218 */     GlStateManager.func_179121_F();
/*     */   }
/*     */   
/*     */   private void renderItemStack(ItemStack stack, int x, int y) {
/* 222 */     GlStateManager.func_179094_E();
/* 223 */     GlStateManager.func_179132_a(true);
/* 224 */     GlStateManager.func_179086_m(256);
/* 225 */     RenderHelper.func_74519_b();
/* 226 */     (mc.func_175599_af()).field_77023_b = -150.0F;
/* 227 */     GlStateManager.func_179118_c();
/* 228 */     GlStateManager.func_179126_j();
/* 229 */     GlStateManager.func_179129_p();
/* 230 */     Util.mc.func_175599_af().func_180450_b(stack, x, y);
/* 231 */     Util.mc.func_175599_af().func_175030_a(mc.field_71466_p, stack, x, y);
/* 232 */     (mc.func_175599_af()).field_77023_b = 0.0F;
/* 233 */     RenderHelper.func_74518_a();
/* 234 */     GlStateManager.func_179089_o();
/* 235 */     GlStateManager.func_179141_d();
/* 236 */     GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
/* 237 */     GlStateManager.func_179097_i();
/* 238 */     renderEnchantmentText(stack, x, y);
/* 239 */     GlStateManager.func_179126_j();
/* 240 */     GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
/* 241 */     GlStateManager.func_179121_F();
/*     */   }
/*     */   
/*     */   private void renderEnchantmentText(ItemStack stack, int x, int y) {
/* 245 */     int enchantmentY = y - 8;
/* 246 */     if (stack.func_77973_b() == Items.field_151153_ao && stack.func_77962_s()) {
/* 247 */       this.renderer.drawStringWithShadow("god", (x * 2), enchantmentY, -3977919);
/* 248 */       enchantmentY -= 8;
/*     */     } 
/* 250 */     NBTTagList enchants = stack.func_77986_q();
/* 251 */     for (int index = 0; index < enchants.func_74745_c(); index++) {
/* 252 */       short id = enchants.func_150305_b(index).func_74765_d("id");
/* 253 */       short level = enchants.func_150305_b(index).func_74765_d("lvl");
/* 254 */       Enchantment enc = Enchantment.func_185262_c(id);
/* 255 */       if (enc != null) {
/* 256 */         String encName = enc.func_190936_d() ? (TextFormatting.RED + enc.func_77316_c(level).substring(11).substring(0, 1).toLowerCase()) : enc.func_77316_c(level).substring(0, 1).toLowerCase();
/* 257 */         encName = encName + level;
/* 258 */         this.renderer.drawStringWithShadow(encName, (x * 2), enchantmentY, -1);
/* 259 */         enchantmentY -= 8;
/*     */       } 
/* 261 */     }  if (DamageUtil.hasDurability(stack)) {
/* 262 */       int percent = DamageUtil.getRoundedDamage(stack);
/* 263 */       String color = (percent >= 60) ? "§a" : ((percent >= 25) ? "§e" : "§c");
/* 264 */       this.renderer.drawStringWithShadow(color + percent + "%", (x * 2), enchantmentY, -1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private float getBiggestArmorTag(EntityPlayer player) {
/* 272 */     float enchantmentY = 0.0F;
/* 273 */     boolean arm = false;
/* 274 */     for (ItemStack stack : player.field_71071_by.field_70460_b) {
/* 275 */       float encY = 0.0F;
/* 276 */       if (stack != null) {
/* 277 */         NBTTagList enchants = stack.func_77986_q();
/* 278 */         for (int index = 0; index < enchants.func_74745_c(); index++) {
/* 279 */           short id = enchants.func_150305_b(index).func_74765_d("id");
/* 280 */           Enchantment enc = Enchantment.func_185262_c(id);
/* 281 */           if (enc != null) {
/* 282 */             encY += 8.0F;
/* 283 */             arm = true;
/*     */           } 
/*     */         } 
/* 286 */       }  if (encY <= enchantmentY)
/* 287 */         continue;  enchantmentY = encY;
/*     */     } 
/* 289 */     ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
/* 290 */     if (renderMainHand.func_77962_s()) {
/* 291 */       float encY = 0.0F;
/* 292 */       NBTTagList enchants = renderMainHand.func_77986_q();
/* 293 */       for (int index2 = 0; index2 < enchants.func_74745_c(); index2++) {
/* 294 */         short id = enchants.func_150305_b(index2).func_74765_d("id");
/* 295 */         Enchantment enc2 = Enchantment.func_185262_c(id);
/* 296 */         if (enc2 != null) {
/* 297 */           encY += 8.0F;
/* 298 */           arm = true;
/*     */         } 
/* 300 */       }  if (encY > enchantmentY)
/* 301 */         enchantmentY = encY; 
/*     */     } 
/*     */     ItemStack renderOffHand;
/* 304 */     if ((renderOffHand = player.func_184592_cb().func_77946_l()).func_77962_s()) {
/* 305 */       float encY = 0.0F;
/* 306 */       NBTTagList enchants = renderOffHand.func_77986_q();
/* 307 */       for (int index = 0; index < enchants.func_74745_c(); index++) {
/* 308 */         short id = enchants.func_150305_b(index).func_74765_d("id");
/* 309 */         Enchantment enc = Enchantment.func_185262_c(id);
/* 310 */         if (enc != null) {
/* 311 */           encY += 8.0F;
/* 312 */           arm = true;
/*     */         } 
/* 314 */       }  if (encY > enchantmentY) {
/* 315 */         enchantmentY = encY;
/*     */       }
/*     */     } 
/* 318 */     return (arm ? false : 20) + enchantmentY;
/*     */   }
/*     */   
/*     */   private String getDisplayTag(EntityPlayer player) {
/* 322 */     String name = player.func_145748_c_().func_150254_d();
/* 323 */     if (name.contains(Util.mc.func_110432_I().func_111285_a())) {
/* 324 */       name = "You";
/*     */     }
/* 326 */     if (!((Boolean)this.health.getValue()).booleanValue()) {
/* 327 */       return name;
/*     */     }
/* 329 */     float health = EntityUtil.getHealth((Entity)player);
/* 330 */     String color = (health > 18.0F) ? "§a" : ((health > 16.0F) ? "§2" : ((health > 12.0F) ? "§e" : ((health > 8.0F) ? "§6" : ((health > 5.0F) ? "§c" : "§4"))));
/* 331 */     String pingStr = "";
/* 332 */     if (((Boolean)this.ping.getValue()).booleanValue()) {
/*     */       try {
/* 334 */         int responseTime = ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(Util.mc.func_147114_u())).func_175102_a(player.func_110124_au()).func_178853_c();
/* 335 */         pingStr = pingStr + responseTime + "ms ";
/* 336 */       } catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */     
/* 340 */     String popStr = " ";
/* 341 */     if (((Boolean)this.totemPops.getValue()).booleanValue()) {
/* 342 */       popStr = popStr + Banzem.totemPopManager.getTotemPopString(player);
/*     */     }
/* 344 */     String idString = "";
/* 345 */     if (((Boolean)this.entityID.getValue()).booleanValue()) {
/* 346 */       idString = idString + "ID: " + player.func_145782_y() + " ";
/*     */     }
/* 348 */     String gameModeStr = "";
/* 349 */     if (((Boolean)this.gamemode.getValue()).booleanValue()) {
/* 350 */       gameModeStr = player.func_184812_l_() ? (gameModeStr + "[C] ") : ((player.func_175149_v() || player.func_82150_aj()) ? (gameModeStr + "[I] ") : (gameModeStr + "[S] "));
/*     */     }
/* 352 */     name = (Math.floor(health) == health) ? (name + color + " " + ((health > 0.0F) ? (String)Integer.valueOf((int)Math.floor(health)) : "dead")) : (name + color + " " + ((health > 0.0F) ? (String)Integer.valueOf((int)health) : "dead"));
/* 353 */     return pingStr + idString + gameModeStr + name + popStr;
/*     */   }
/*     */   
/*     */   private int getDisplayColour(EntityPlayer player) {
/* 357 */     int colour = -5592406;
/* 358 */     if (((Boolean)this.whiter.getValue()).booleanValue()) {
/* 359 */       colour = -1;
/*     */     }
/* 361 */     if (Banzem.friendManager.isFriend(player)) {
/* 362 */       return -11157267;
/*     */     }
/* 364 */     if (player.func_82150_aj()) {
/* 365 */       colour = -1113785;
/* 366 */     } else if (player.func_70093_af() && ((Boolean)this.sneak.getValue()).booleanValue()) {
/* 367 */       colour = -6481515;
/*     */     } 
/* 369 */     return colour;
/*     */   }
/*     */   
/*     */   private double interpolate(double previous, double current, float delta) {
/* 373 */     return previous + (current - previous) * delta;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\render\Nametags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */