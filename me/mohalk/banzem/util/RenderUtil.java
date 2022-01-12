/*      */ package me.mohalk.banzem.util;
/*      */ import java.awt.Color;
/*      */ import java.nio.FloatBuffer;
/*      */ import java.nio.IntBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Objects;
/*      */ import me.mohalk.banzem.Banzem;
/*      */ import me.mohalk.banzem.features.modules.client.Colors;
/*      */ import net.minecraft.block.material.Material;
/*      */ import net.minecraft.block.state.IBlockState;
/*      */ import net.minecraft.client.Minecraft;
/*      */ import net.minecraft.client.gui.ScaledResolution;
/*      */ import net.minecraft.client.model.ModelBiped;
/*      */ import net.minecraft.client.renderer.BufferBuilder;
/*      */ import net.minecraft.client.renderer.GlStateManager;
/*      */ import net.minecraft.client.renderer.OpenGlHelper;
/*      */ import net.minecraft.client.renderer.RenderGlobal;
/*      */ import net.minecraft.client.renderer.RenderItem;
/*      */ import net.minecraft.client.renderer.Tessellator;
/*      */ import net.minecraft.client.renderer.culling.Frustum;
/*      */ import net.minecraft.client.renderer.culling.ICamera;
/*      */ import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
/*      */ import net.minecraft.client.shader.Framebuffer;
/*      */ import net.minecraft.entity.Entity;
/*      */ import net.minecraft.entity.player.EntityPlayer;
/*      */ import net.minecraft.util.EnumFacing;
/*      */ import net.minecraft.util.ResourceLocation;
/*      */ import net.minecraft.util.math.AxisAlignedBB;
/*      */ import net.minecraft.util.math.BlockPos;
/*      */ import net.minecraft.util.math.Vec2f;
/*      */ import net.minecraft.util.math.Vec3d;
/*      */ import net.minecraft.world.World;
/*      */ import org.lwjgl.BufferUtils;
/*      */ import org.lwjgl.opengl.EXTFramebufferObject;
/*      */ import org.lwjgl.opengl.GL11;
/*      */ import org.lwjgl.util.glu.Disk;
/*      */ import org.lwjgl.util.glu.GLU;
/*      */ import org.lwjgl.util.glu.Sphere;
/*      */ 
/*      */ public class RenderUtil implements Util {
/*   43 */   private static final Frustum frustrum = new Frustum();
/*   44 */   private static final FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
/*   45 */   private static final IntBuffer viewport = BufferUtils.createIntBuffer(16);
/*   46 */   private static final FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
/*   47 */   private static final FloatBuffer projection = BufferUtils.createFloatBuffer(16);
/*   48 */   public static RenderItem itemRender = mc.func_175599_af();
/*   49 */   public static ICamera camera = (ICamera)new Frustum();
/*   50 */   private static boolean depth = GL11.glIsEnabled(2896);
/*   51 */   private static boolean texture = GL11.glIsEnabled(3042);
/*   52 */   private static boolean clean = GL11.glIsEnabled(3553);
/*   53 */   private static boolean bind = GL11.glIsEnabled(2929);
/*   54 */   private static boolean override = GL11.glIsEnabled(2848);
/*      */ 
/*      */   
/*      */   public static void updateModelViewProjectionMatrix() {
/*   58 */     GL11.glGetFloat(2982, modelView);
/*   59 */     GL11.glGetFloat(2983, projection);
/*   60 */     GL11.glGetInteger(2978, viewport);
/*   61 */     ScaledResolution res = new ScaledResolution(Minecraft.func_71410_x());
/*   62 */     GLUProjection.getInstance().updateMatrices(viewport, modelView, projection, (res.func_78326_a() / (Minecraft.func_71410_x()).field_71443_c), (res.func_78328_b() / (Minecraft.func_71410_x()).field_71440_d));
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawRectangleCorrectly(int x, int y, int w, int h, int color) {
/*   67 */     GL11.glLineWidth(1.0F);
/*   68 */     Gui.func_73734_a(x, y, x + w, y + h, color);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawWaypointImage(BlockPos pos, GLUProjection.Projection projection, Color color, String name, boolean rectangle, Color rectangleColor) {
/*   73 */     GlStateManager.func_179094_E();
/*   74 */     GlStateManager.func_179137_b(projection.getX(), projection.getY(), 0.0D);
/*   75 */     String text = name + ": " + Math.round(mc.field_71439_g.func_70011_f(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p())) + "M";
/*   76 */     float textWidth = Banzem.textManager.getStringWidth(text);
/*   77 */     Banzem.textManager.drawString(text, -(textWidth / 2.0F), -(Banzem.textManager.getFontHeight() / 2.0F) + Banzem.textManager.getFontHeight() / 2.0F, color.getRGB(), false);
/*   78 */     GlStateManager.func_179137_b(-projection.getX(), -projection.getY(), 0.0D);
/*   79 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
/*   84 */     return new AxisAlignedBB(bb.field_72340_a - (mc.func_175598_ae()).field_78730_l, bb.field_72338_b - (mc.func_175598_ae()).field_78731_m, bb.field_72339_c - (mc.func_175598_ae()).field_78728_n, bb.field_72336_d - (mc.func_175598_ae()).field_78730_l, bb.field_72337_e - (mc.func_175598_ae()).field_78731_m, bb.field_72334_f - (mc.func_175598_ae()).field_78728_n);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
/*   89 */     Tessellator tessellator = Tessellator.func_178181_a();
/*   90 */     BufferBuilder BufferBuilder2 = tessellator.func_178180_c();
/*   91 */     BufferBuilder2.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/*   92 */     BufferBuilder2.func_181662_b(x, (y + height), zLevel).func_187315_a((textureX * 0.00390625F), ((textureY + height) * 0.00390625F)).func_181675_d();
/*   93 */     BufferBuilder2.func_181662_b((x + width), (y + height), zLevel).func_187315_a(((textureX + width) * 0.00390625F), ((textureY + height) * 0.00390625F)).func_181675_d();
/*   94 */     BufferBuilder2.func_181662_b((x + width), y, zLevel).func_187315_a(((textureX + width) * 0.00390625F), (textureY * 0.00390625F)).func_181675_d();
/*   95 */     BufferBuilder2.func_181662_b(x, y, zLevel).func_187315_a((textureX * 0.00390625F), (textureY * 0.00390625F)).func_181675_d();
/*   96 */     tessellator.func_78381_a();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawOpenGradientBox(BlockPos pos, Color startColor, Color endColor, double height) {
/*  101 */     for (EnumFacing face : EnumFacing.values()) {
/*  102 */       if (face != EnumFacing.UP) {
/*  103 */         drawGradientPlane(pos, face, startColor, endColor, height);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void drawClosedGradientBox(BlockPos pos, Color startColor, Color endColor, double height) {
/*  109 */     for (EnumFacing face : EnumFacing.values()) {
/*  110 */       drawGradientPlane(pos, face, startColor, endColor, height);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawTricolorGradientBox(BlockPos pos, Color startColor, Color midColor, Color endColor) {
/*  116 */     for (EnumFacing face : EnumFacing.values()) {
/*  117 */       if (face != EnumFacing.UP)
/*  118 */         drawGradientPlane(pos, face, startColor, midColor, true, false); 
/*      */     } 
/*  120 */     for (EnumFacing face : EnumFacing.values()) {
/*  121 */       if (face != EnumFacing.DOWN) {
/*  122 */         drawGradientPlane(pos, face, midColor, endColor, true, true);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void drawGradientPlane(BlockPos pos, EnumFacing face, Color startColor, Color endColor, boolean half, boolean top) {
/*  128 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  129 */     BufferBuilder builder = tessellator.func_178180_c();
/*  130 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/*  131 */     Vec3d interp = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, mc.func_184121_ak());
/*  132 */     AxisAlignedBB bb = iblockstate.func_185918_c((World)mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c);
/*  133 */     float red = startColor.getRed() / 255.0F;
/*  134 */     float green = startColor.getGreen() / 255.0F;
/*  135 */     float blue = startColor.getBlue() / 255.0F;
/*  136 */     float alpha = startColor.getAlpha() / 255.0F;
/*  137 */     float red1 = endColor.getRed() / 255.0F;
/*  138 */     float green1 = endColor.getGreen() / 255.0F;
/*  139 */     float blue1 = endColor.getBlue() / 255.0F;
/*  140 */     float alpha1 = endColor.getAlpha() / 255.0F;
/*  141 */     double x1 = 0.0D;
/*  142 */     double y1 = 0.0D;
/*  143 */     double z1 = 0.0D;
/*  144 */     double x2 = 0.0D;
/*  145 */     double y2 = 0.0D;
/*  146 */     double z2 = 0.0D;
/*  147 */     if (face == EnumFacing.DOWN) {
/*  148 */       x1 = bb.field_72340_a;
/*  149 */       x2 = bb.field_72336_d;
/*  150 */       y1 = bb.field_72338_b + (top ? 0.5D : 0.0D);
/*  151 */       y2 = bb.field_72338_b + (top ? 0.5D : 0.0D);
/*  152 */       z1 = bb.field_72339_c;
/*  153 */       z2 = bb.field_72334_f;
/*  154 */     } else if (face == EnumFacing.UP) {
/*  155 */       x1 = bb.field_72340_a;
/*  156 */       x2 = bb.field_72336_d;
/*  157 */       y1 = bb.field_72337_e / (half ? 2 : true);
/*  158 */       y2 = bb.field_72337_e / (half ? 2 : true);
/*  159 */       z1 = bb.field_72339_c;
/*  160 */       z2 = bb.field_72334_f;
/*  161 */     } else if (face == EnumFacing.EAST) {
/*  162 */       x1 = bb.field_72336_d;
/*  163 */       x2 = bb.field_72336_d;
/*  164 */       y1 = bb.field_72338_b + (top ? 0.5D : 0.0D);
/*  165 */       y2 = bb.field_72337_e / (half ? 2 : true);
/*  166 */       z1 = bb.field_72339_c;
/*  167 */       z2 = bb.field_72334_f;
/*  168 */     } else if (face == EnumFacing.WEST) {
/*  169 */       x1 = bb.field_72340_a;
/*  170 */       x2 = bb.field_72340_a;
/*  171 */       y1 = bb.field_72338_b + (top ? 0.5D : 0.0D);
/*  172 */       y2 = bb.field_72337_e / (half ? 2 : true);
/*  173 */       z1 = bb.field_72339_c;
/*  174 */       z2 = bb.field_72334_f;
/*  175 */     } else if (face == EnumFacing.SOUTH) {
/*  176 */       x1 = bb.field_72340_a;
/*  177 */       x2 = bb.field_72336_d;
/*  178 */       y1 = bb.field_72338_b + (top ? 0.5D : 0.0D);
/*  179 */       y2 = bb.field_72337_e / (half ? 2 : true);
/*  180 */       z1 = bb.field_72334_f;
/*  181 */       z2 = bb.field_72334_f;
/*  182 */     } else if (face == EnumFacing.NORTH) {
/*  183 */       x1 = bb.field_72340_a;
/*  184 */       x2 = bb.field_72336_d;
/*  185 */       y1 = bb.field_72338_b + (top ? 0.5D : 0.0D);
/*  186 */       y2 = bb.field_72337_e / (half ? 2 : true);
/*  187 */       z1 = bb.field_72339_c;
/*  188 */       z2 = bb.field_72339_c;
/*      */     } 
/*  190 */     GlStateManager.func_179094_E();
/*  191 */     GlStateManager.func_179097_i();
/*  192 */     GlStateManager.func_179090_x();
/*  193 */     GlStateManager.func_179147_l();
/*  194 */     GlStateManager.func_179118_c();
/*  195 */     GlStateManager.func_179132_a(false);
/*  196 */     builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
/*  197 */     if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
/*  198 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  199 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  200 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  201 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  202 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  203 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  204 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  205 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  206 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  207 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  208 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  209 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  210 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  211 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  212 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  213 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  214 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  215 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  216 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  217 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  218 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  219 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  220 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  221 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  222 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  223 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  224 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  225 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  226 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  227 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  228 */     } else if (face == EnumFacing.UP) {
/*  229 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  230 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  231 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  232 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  233 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  234 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  235 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  236 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  237 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  238 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  239 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  240 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  241 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  242 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  243 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  244 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  245 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  246 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  247 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  248 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  249 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  250 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  251 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  252 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  253 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  254 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  255 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  256 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  257 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  258 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  259 */     } else if (face == EnumFacing.DOWN) {
/*  260 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  261 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  262 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  263 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  264 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  265 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  266 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  267 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  268 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  269 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  270 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  271 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  272 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  273 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  274 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  275 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  276 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  277 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  278 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  279 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  280 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  281 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  282 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  283 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  284 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  285 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  286 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  287 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  288 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  289 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*      */     } 
/*  291 */     tessellator.func_78381_a();
/*  292 */     GlStateManager.func_179132_a(true);
/*  293 */     GlStateManager.func_179084_k();
/*  294 */     GlStateManager.func_179141_d();
/*  295 */     GlStateManager.func_179098_w();
/*  296 */     GlStateManager.func_179126_j();
/*  297 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawGradientPlane(BlockPos pos, EnumFacing face, Color startColor, Color endColor, double height) {
/*  302 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  303 */     BufferBuilder builder = tessellator.func_178180_c();
/*  304 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/*  305 */     Vec3d interp = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, mc.func_184121_ak());
/*  306 */     AxisAlignedBB bb = iblockstate.func_185918_c((World)mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c).func_72321_a(0.0D, height, 0.0D);
/*  307 */     float red = startColor.getRed() / 255.0F;
/*  308 */     float green = startColor.getGreen() / 255.0F;
/*  309 */     float blue = startColor.getBlue() / 255.0F;
/*  310 */     float alpha = startColor.getAlpha() / 255.0F;
/*  311 */     float red1 = endColor.getRed() / 255.0F;
/*  312 */     float green1 = endColor.getGreen() / 255.0F;
/*  313 */     float blue1 = endColor.getBlue() / 255.0F;
/*  314 */     float alpha1 = endColor.getAlpha() / 255.0F;
/*  315 */     double x1 = 0.0D;
/*  316 */     double y1 = 0.0D;
/*  317 */     double z1 = 0.0D;
/*  318 */     double x2 = 0.0D;
/*  319 */     double y2 = 0.0D;
/*  320 */     double z2 = 0.0D;
/*  321 */     if (face == EnumFacing.DOWN) {
/*  322 */       x1 = bb.field_72340_a;
/*  323 */       x2 = bb.field_72336_d;
/*  324 */       y1 = bb.field_72338_b;
/*  325 */       y2 = bb.field_72338_b;
/*  326 */       z1 = bb.field_72339_c;
/*  327 */       z2 = bb.field_72334_f;
/*  328 */     } else if (face == EnumFacing.UP) {
/*  329 */       x1 = bb.field_72340_a;
/*  330 */       x2 = bb.field_72336_d;
/*  331 */       y1 = bb.field_72337_e;
/*  332 */       y2 = bb.field_72337_e;
/*  333 */       z1 = bb.field_72339_c;
/*  334 */       z2 = bb.field_72334_f;
/*  335 */     } else if (face == EnumFacing.EAST) {
/*  336 */       x1 = bb.field_72336_d;
/*  337 */       x2 = bb.field_72336_d;
/*  338 */       y1 = bb.field_72338_b;
/*  339 */       y2 = bb.field_72337_e;
/*  340 */       z1 = bb.field_72339_c;
/*  341 */       z2 = bb.field_72334_f;
/*  342 */     } else if (face == EnumFacing.WEST) {
/*  343 */       x1 = bb.field_72340_a;
/*  344 */       x2 = bb.field_72340_a;
/*  345 */       y1 = bb.field_72338_b;
/*  346 */       y2 = bb.field_72337_e;
/*  347 */       z1 = bb.field_72339_c;
/*  348 */       z2 = bb.field_72334_f;
/*  349 */     } else if (face == EnumFacing.SOUTH) {
/*  350 */       x1 = bb.field_72340_a;
/*  351 */       x2 = bb.field_72336_d;
/*  352 */       y1 = bb.field_72338_b;
/*  353 */       y2 = bb.field_72337_e;
/*  354 */       z1 = bb.field_72334_f;
/*  355 */       z2 = bb.field_72334_f;
/*  356 */     } else if (face == EnumFacing.NORTH) {
/*  357 */       x1 = bb.field_72340_a;
/*  358 */       x2 = bb.field_72336_d;
/*  359 */       y1 = bb.field_72338_b;
/*  360 */       y2 = bb.field_72337_e;
/*  361 */       z1 = bb.field_72339_c;
/*  362 */       z2 = bb.field_72339_c;
/*      */     } 
/*  364 */     GlStateManager.func_179094_E();
/*  365 */     GlStateManager.func_179097_i();
/*  366 */     GlStateManager.func_179090_x();
/*  367 */     GlStateManager.func_179147_l();
/*  368 */     GlStateManager.func_179118_c();
/*  369 */     GlStateManager.func_179132_a(false);
/*  370 */     builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
/*  371 */     if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
/*  372 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  373 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  374 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  375 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  376 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  377 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  378 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  379 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  380 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  381 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  382 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  383 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  384 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  385 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  386 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  387 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  388 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  389 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  390 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  391 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  392 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  393 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  394 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  395 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  396 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  397 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  398 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  399 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  400 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  401 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  402 */     } else if (face == EnumFacing.UP) {
/*  403 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  404 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  405 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  406 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  407 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  408 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  409 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  410 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  411 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  412 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  413 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  414 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  415 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  416 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  417 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  418 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  419 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  420 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  421 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  422 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  423 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  424 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  425 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  426 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  427 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  428 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  429 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  430 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  431 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  432 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  433 */     } else if (face == EnumFacing.DOWN) {
/*  434 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  435 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  436 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  437 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  438 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  439 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  440 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  441 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  442 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  443 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  444 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  445 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  446 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  447 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  448 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  449 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  450 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  451 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  452 */       builder.func_181662_b(x1, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  453 */       builder.func_181662_b(x2, y1, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  454 */       builder.func_181662_b(x1, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  455 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  456 */       builder.func_181662_b(x2, y1, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  457 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  458 */       builder.func_181662_b(x1, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  459 */       builder.func_181662_b(x1, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  460 */       builder.func_181662_b(x2, y2, z1).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  461 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  462 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  463 */       builder.func_181662_b(x2, y2, z2).func_181666_a(red, green, blue, alpha).func_181675_d();
/*      */     } 
/*  465 */     tessellator.func_78381_a();
/*  466 */     GlStateManager.func_179132_a(true);
/*  467 */     GlStateManager.func_179084_k();
/*  468 */     GlStateManager.func_179141_d();
/*  469 */     GlStateManager.func_179098_w();
/*  470 */     GlStateManager.func_179126_j();
/*  471 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawGradientRect(int x, int y, int w, int h, int startColor, int endColor) {
/*  476 */     float f = (startColor >> 24 & 0xFF) / 255.0F;
/*  477 */     float f1 = (startColor >> 16 & 0xFF) / 255.0F;
/*  478 */     float f2 = (startColor >> 8 & 0xFF) / 255.0F;
/*  479 */     float f3 = (startColor & 0xFF) / 255.0F;
/*  480 */     float f4 = (endColor >> 24 & 0xFF) / 255.0F;
/*  481 */     float f5 = (endColor >> 16 & 0xFF) / 255.0F;
/*  482 */     float f6 = (endColor >> 8 & 0xFF) / 255.0F;
/*  483 */     float f7 = (endColor & 0xFF) / 255.0F;
/*  484 */     GlStateManager.func_179090_x();
/*  485 */     GlStateManager.func_179147_l();
/*  486 */     GlStateManager.func_179118_c();
/*  487 */     GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
/*  488 */     GlStateManager.func_179103_j(7425);
/*  489 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  490 */     BufferBuilder vertexbuffer = tessellator.func_178180_c();
/*  491 */     vertexbuffer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
/*  492 */     vertexbuffer.func_181662_b(x + w, y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
/*  493 */     vertexbuffer.func_181662_b(x, y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
/*  494 */     vertexbuffer.func_181662_b(x, y + h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
/*  495 */     vertexbuffer.func_181662_b(x + w, y + h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
/*  496 */     tessellator.func_78381_a();
/*  497 */     GlStateManager.func_179103_j(7424);
/*  498 */     GlStateManager.func_179084_k();
/*  499 */     GlStateManager.func_179141_d();
/*  500 */     GlStateManager.func_179098_w();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawGradientBlockOutline(BlockPos pos, Color startColor, Color endColor, float linewidth, double height) {
/*  505 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/*  506 */     Vec3d interp = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, mc.func_184121_ak());
/*  507 */     drawGradientBlockOutline(iblockstate.func_185918_c((World)mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c).func_72321_a(0.0D, height, 0.0D), startColor, endColor, linewidth);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawProperGradientBlockOutline(BlockPos pos, Color startColor, Color midColor, Color endColor, float linewidth) {
/*  512 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/*  513 */     Vec3d interp = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, mc.func_184121_ak());
/*  514 */     drawProperGradientBlockOutline(iblockstate.func_185918_c((World)mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), startColor, midColor, endColor, linewidth);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawProperGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color midColor, Color endColor, float linewidth) {
/*  519 */     float red = endColor.getRed() / 255.0F;
/*  520 */     float green = endColor.getGreen() / 255.0F;
/*  521 */     float blue = endColor.getBlue() / 255.0F;
/*  522 */     float alpha = endColor.getAlpha() / 255.0F;
/*  523 */     float red1 = midColor.getRed() / 255.0F;
/*  524 */     float green1 = midColor.getGreen() / 255.0F;
/*  525 */     float blue1 = midColor.getBlue() / 255.0F;
/*  526 */     float alpha1 = midColor.getAlpha() / 255.0F;
/*  527 */     float red2 = startColor.getRed() / 255.0F;
/*  528 */     float green2 = startColor.getGreen() / 255.0F;
/*  529 */     float blue2 = startColor.getBlue() / 255.0F;
/*  530 */     float alpha2 = startColor.getAlpha() / 255.0F;
/*  531 */     double dif = (bb.field_72337_e - bb.field_72338_b) / 2.0D;
/*  532 */     GlStateManager.func_179094_E();
/*  533 */     GlStateManager.func_179147_l();
/*  534 */     GlStateManager.func_179097_i();
/*  535 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/*  536 */     GlStateManager.func_179090_x();
/*  537 */     GlStateManager.func_179132_a(false);
/*  538 */     GL11.glEnable(2848);
/*  539 */     GL11.glHint(3154, 4354);
/*  540 */     GL11.glLineWidth(linewidth);
/*  541 */     GL11.glBegin(1);
/*  542 */     GL11.glColor4d(red, green, blue, alpha);
/*  543 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c);
/*  544 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
/*  545 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
/*  546 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
/*  547 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
/*  548 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
/*  549 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
/*  550 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c);
/*  551 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c);
/*  552 */     GL11.glColor4d(red1, green1, blue1, alpha1);
/*  553 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72339_c);
/*  554 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72339_c);
/*  555 */     GL11.glColor4f(red2, green2, blue2, alpha2);
/*  556 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
/*  557 */     GL11.glColor4d(red, green, blue, alpha);
/*  558 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
/*  559 */     GL11.glColor4d(red1, green1, blue1, alpha1);
/*  560 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72334_f);
/*  561 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72334_f);
/*  562 */     GL11.glColor4d(red2, green2, blue2, alpha2);
/*  563 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
/*  564 */     GL11.glColor4d(red, green, blue, alpha);
/*  565 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
/*  566 */     GL11.glColor4d(red1, green1, blue1, alpha1);
/*  567 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72334_f);
/*  568 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72334_f);
/*  569 */     GL11.glColor4d(red2, green2, blue2, alpha2);
/*  570 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f);
/*  571 */     GL11.glColor4d(red, green, blue, alpha);
/*  572 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
/*  573 */     GL11.glColor4d(red1, green1, blue1, alpha1);
/*  574 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72339_c);
/*  575 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72339_c);
/*  576 */     GL11.glColor4d(red2, green2, blue2, alpha2);
/*  577 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
/*  578 */     GL11.glColor4d(red2, green2, blue2, alpha2);
/*  579 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
/*  580 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
/*  581 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
/*  582 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f);
/*  583 */     GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f);
/*  584 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
/*  585 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
/*  586 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
/*  587 */     GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
/*  588 */     GL11.glEnd();
/*  589 */     GL11.glDisable(2848);
/*  590 */     GlStateManager.func_179132_a(true);
/*  591 */     GlStateManager.func_179126_j();
/*  592 */     GlStateManager.func_179098_w();
/*  593 */     GlStateManager.func_179084_k();
/*  594 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
/*  599 */     float red = startColor.getRed() / 255.0F;
/*  600 */     float green = startColor.getGreen() / 255.0F;
/*  601 */     float blue = startColor.getBlue() / 255.0F;
/*  602 */     float alpha = startColor.getAlpha() / 255.0F;
/*  603 */     float red1 = endColor.getRed() / 255.0F;
/*  604 */     float green1 = endColor.getGreen() / 255.0F;
/*  605 */     float blue1 = endColor.getBlue() / 255.0F;
/*  606 */     float alpha1 = endColor.getAlpha() / 255.0F;
/*  607 */     GlStateManager.func_179094_E();
/*  608 */     GlStateManager.func_179147_l();
/*  609 */     GlStateManager.func_179097_i();
/*  610 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/*  611 */     GlStateManager.func_179090_x();
/*  612 */     GlStateManager.func_179132_a(false);
/*  613 */     GL11.glEnable(2848);
/*  614 */     GL11.glHint(3154, 4354);
/*  615 */     GL11.glLineWidth(linewidth);
/*  616 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  617 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/*  618 */     bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
/*  619 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  620 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  621 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  622 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  623 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  624 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  625 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  626 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  627 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  628 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  629 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  630 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  631 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  632 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  633 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  634 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  635 */     tessellator.func_78381_a();
/*  636 */     GL11.glDisable(2848);
/*  637 */     GlStateManager.func_179132_a(true);
/*  638 */     GlStateManager.func_179126_j();
/*  639 */     GlStateManager.func_179098_w();
/*  640 */     GlStateManager.func_179084_k();
/*  641 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawGradientFilledBox(BlockPos pos, Color startColor, Color endColor) {
/*  646 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/*  647 */     Vec3d interp = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, mc.func_184121_ak());
/*  648 */     drawGradientFilledBox(iblockstate.func_185918_c((World)mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), startColor, endColor);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawGradientFilledBox(AxisAlignedBB bb, Color startColor, Color endColor) {
/*  653 */     GlStateManager.func_179094_E();
/*  654 */     GlStateManager.func_179147_l();
/*  655 */     GlStateManager.func_179097_i();
/*  656 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/*  657 */     GlStateManager.func_179090_x();
/*  658 */     GlStateManager.func_179132_a(false);
/*  659 */     float alpha = endColor.getAlpha() / 255.0F;
/*  660 */     float red = endColor.getRed() / 255.0F;
/*  661 */     float green = endColor.getGreen() / 255.0F;
/*  662 */     float blue = endColor.getBlue() / 255.0F;
/*  663 */     float alpha1 = startColor.getAlpha() / 255.0F;
/*  664 */     float red1 = startColor.getRed() / 255.0F;
/*  665 */     float green1 = startColor.getGreen() / 255.0F;
/*  666 */     float blue1 = startColor.getBlue() / 255.0F;
/*  667 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  668 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/*  669 */     bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
/*  670 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  671 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  672 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  673 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  674 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  675 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  676 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  677 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  678 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  679 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  680 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  681 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  682 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  683 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  684 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  685 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  686 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  687 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  688 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  689 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  690 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  691 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  692 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  693 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  694 */     tessellator.func_78381_a();
/*  695 */     GlStateManager.func_179132_a(true);
/*  696 */     GlStateManager.func_179126_j();
/*  697 */     GlStateManager.func_179098_w();
/*  698 */     GlStateManager.func_179084_k();
/*  699 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawGradientRect(float x, float y, float w, float h, int startColor, int endColor) {
/*  704 */     float f = (startColor >> 24 & 0xFF) / 255.0F;
/*  705 */     float f1 = (startColor >> 16 & 0xFF) / 255.0F;
/*  706 */     float f2 = (startColor >> 8 & 0xFF) / 255.0F;
/*  707 */     float f3 = (startColor & 0xFF) / 255.0F;
/*  708 */     float f4 = (endColor >> 24 & 0xFF) / 255.0F;
/*  709 */     float f5 = (endColor >> 16 & 0xFF) / 255.0F;
/*  710 */     float f6 = (endColor >> 8 & 0xFF) / 255.0F;
/*  711 */     float f7 = (endColor & 0xFF) / 255.0F;
/*  712 */     GlStateManager.func_179090_x();
/*  713 */     GlStateManager.func_179147_l();
/*  714 */     GlStateManager.func_179118_c();
/*  715 */     GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
/*  716 */     GlStateManager.func_179103_j(7425);
/*  717 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  718 */     BufferBuilder vertexbuffer = tessellator.func_178180_c();
/*  719 */     vertexbuffer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
/*  720 */     vertexbuffer.func_181662_b(x + w, y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
/*  721 */     vertexbuffer.func_181662_b(x, y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
/*  722 */     vertexbuffer.func_181662_b(x, y + h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
/*  723 */     vertexbuffer.func_181662_b(x + w, y + h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
/*  724 */     tessellator.func_78381_a();
/*  725 */     GlStateManager.func_179103_j(7424);
/*  726 */     GlStateManager.func_179084_k();
/*  727 */     GlStateManager.func_179141_d();
/*  728 */     GlStateManager.func_179098_w();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawFilledCircle(double x, double y, double z, Color color, double radius) {
/*  733 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  734 */     BufferBuilder builder = tessellator.func_178180_c();
/*  735 */     builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void drawGradientBoxTest(BlockPos pos, Color startColor, Color endColor) {}
/*      */ 
/*      */   
/*      */   public static void blockESP(BlockPos b, Color c, double length, double length2) {
/*  744 */     blockEsp(b, c, length, length2);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
/*  749 */     if (box) {
/*  750 */       drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
/*      */     }
/*  752 */     if (outline) {
/*  753 */       drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawSexyBoxBanzemIsRetardedFuckYouESP(AxisAlignedBB a, Color boxColor, Color outlineColor, float lineWidth, boolean outline, boolean box, boolean colorSync, float alpha, float scale, float slab) {
/*  759 */     double f = 0.5D * (1.0F - scale);
/*  760 */     AxisAlignedBB bb = interpolateAxis(new AxisAlignedBB(a.field_72340_a + f, a.field_72338_b + f + (1.0F - slab), a.field_72339_c + f, a.field_72336_d - f, a.field_72337_e - f, a.field_72334_f - f));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  768 */     float rB = boxColor.getRed() / 255.0F;
/*  769 */     float gB = boxColor.getGreen() / 255.0F;
/*  770 */     float bB = boxColor.getBlue() / 255.0F;
/*  771 */     float aB = boxColor.getAlpha() / 255.0F;
/*  772 */     float rO = outlineColor.getRed() / 255.0F;
/*  773 */     float gO = outlineColor.getGreen() / 255.0F;
/*  774 */     float bO = outlineColor.getBlue() / 255.0F;
/*  775 */     float aO = outlineColor.getAlpha() / 255.0F;
/*  776 */     if (colorSync) {
/*  777 */       rB = Colors.INSTANCE.getCurrentColor().getRed() / 255.0F;
/*  778 */       gB = Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F;
/*  779 */       bB = Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F;
/*  780 */       rO = Colors.INSTANCE.getCurrentColor().getRed() / 255.0F;
/*  781 */       gO = Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F;
/*  782 */       bO = Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F;
/*      */     } 
/*  784 */     if (alpha > 1.0F) alpha = 1.0F; 
/*  785 */     aB *= alpha;
/*  786 */     aO *= alpha;
/*  787 */     if (box) {
/*  788 */       GlStateManager.func_179094_E();
/*  789 */       GlStateManager.func_179147_l();
/*  790 */       GlStateManager.func_179097_i();
/*  791 */       GlStateManager.func_179120_a(770, 771, 0, 1);
/*  792 */       GlStateManager.func_179090_x();
/*  793 */       GlStateManager.func_179132_a(false);
/*  794 */       GL11.glEnable(2848);
/*  795 */       GL11.glHint(3154, 4354);
/*  796 */       RenderGlobal.func_189696_b(bb, rB, gB, bB, aB);
/*  797 */       GL11.glDisable(2848);
/*  798 */       GlStateManager.func_179132_a(true);
/*  799 */       GlStateManager.func_179126_j();
/*  800 */       GlStateManager.func_179098_w();
/*  801 */       GlStateManager.func_179084_k();
/*  802 */       GlStateManager.func_179121_F();
/*      */     } 
/*  804 */     if (outline) {
/*  805 */       GlStateManager.func_179094_E();
/*  806 */       GlStateManager.func_179147_l();
/*  807 */       GlStateManager.func_179097_i();
/*  808 */       GlStateManager.func_179120_a(770, 771, 0, 1);
/*  809 */       GlStateManager.func_179090_x();
/*  810 */       GlStateManager.func_179132_a(false);
/*  811 */       GL11.glEnable(2848);
/*  812 */       GL11.glHint(3154, 4354);
/*  813 */       GL11.glLineWidth(lineWidth);
/*  814 */       Tessellator tessellator = Tessellator.func_178181_a();
/*  815 */       BufferBuilder bufferbuilder = tessellator.func_178180_c();
/*  816 */       bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
/*  817 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  818 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  819 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  820 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  821 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  822 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  823 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  824 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  825 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  826 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  827 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  828 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  829 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  830 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  831 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  832 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(rO, gO, bO, aO).func_181675_d();
/*  833 */       tessellator.func_78381_a();
/*  834 */       GL11.glDisable(2848);
/*  835 */       GlStateManager.func_179132_a(true);
/*  836 */       GlStateManager.func_179126_j();
/*  837 */       GlStateManager.func_179098_w();
/*  838 */       GlStateManager.func_179084_k();
/*  839 */       GlStateManager.func_179121_F();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height, boolean gradientBox, boolean gradientOutline, boolean invertGradientBox, boolean invertGradientOutline, int gradientAlpha) {
/*  845 */     if (box) {
/*  846 */       drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), height, gradientBox, invertGradientBox, gradientAlpha);
/*      */     }
/*  848 */     if (outline) {
/*  849 */       drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, height, gradientOutline, invertGradientOutline, gradientAlpha);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr) {
/*  855 */     GL11.glScissor((int)(x * sr.func_78325_e()), (int)(mc.field_71440_d - y1 * sr.func_78325_e()), (int)((x1 - x) * sr.func_78325_e()), (int)((y1 - y) * sr.func_78325_e()));
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
/*  860 */     float red = (hex >> 16 & 0xFF) / 255.0F;
/*  861 */     float green = (hex >> 8 & 0xFF) / 255.0F;
/*  862 */     float blue = (hex & 0xFF) / 255.0F;
/*  863 */     float alpha = (hex >> 24 & 0xFF) / 255.0F;
/*  864 */     GlStateManager.func_179094_E();
/*  865 */     GlStateManager.func_179090_x();
/*  866 */     GlStateManager.func_179147_l();
/*  867 */     GlStateManager.func_179118_c();
/*  868 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/*  869 */     GlStateManager.func_179103_j(7425);
/*  870 */     GL11.glLineWidth(thickness);
/*  871 */     GL11.glEnable(2848);
/*  872 */     GL11.glHint(3154, 4354);
/*  873 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  874 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/*  875 */     bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
/*  876 */     bufferbuilder.func_181662_b(x, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  877 */     bufferbuilder.func_181662_b(x1, y1, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  878 */     tessellator.func_78381_a();
/*  879 */     GlStateManager.func_179103_j(7424);
/*  880 */     GL11.glDisable(2848);
/*  881 */     GlStateManager.func_179084_k();
/*  882 */     GlStateManager.func_179141_d();
/*  883 */     GlStateManager.func_179098_w();
/*  884 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBox(BlockPos pos, Color color) {
/*  889 */     AxisAlignedBB bb = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/*  890 */     camera.func_78547_a(((Entity)Objects.requireNonNull((T)mc.func_175606_aa())).field_70165_t, (mc.func_175606_aa()).field_70163_u, (mc.func_175606_aa()).field_70161_v);
/*  891 */     if (camera.func_78546_a(new AxisAlignedBB(bb.field_72340_a + (mc.func_175598_ae()).field_78730_l, bb.field_72338_b + (mc.func_175598_ae()).field_78731_m, bb.field_72339_c + (mc.func_175598_ae()).field_78728_n, bb.field_72336_d + (mc.func_175598_ae()).field_78730_l, bb.field_72337_e + (mc.func_175598_ae()).field_78731_m, bb.field_72334_f + (mc.func_175598_ae()).field_78728_n))) {
/*  892 */       GlStateManager.func_179094_E();
/*  893 */       GlStateManager.func_179147_l();
/*  894 */       GlStateManager.func_179097_i();
/*  895 */       GlStateManager.func_179120_a(770, 771, 0, 1);
/*  896 */       GlStateManager.func_179090_x();
/*  897 */       GlStateManager.func_179132_a(false);
/*  898 */       GL11.glEnable(2848);
/*  899 */       GL11.glHint(3154, 4354);
/*  900 */       RenderGlobal.func_189696_b(bb, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
/*  901 */       GL11.glDisable(2848);
/*  902 */       GlStateManager.func_179132_a(true);
/*  903 */       GlStateManager.func_179126_j();
/*  904 */       GlStateManager.func_179098_w();
/*  905 */       GlStateManager.func_179084_k();
/*  906 */       GlStateManager.func_179121_F();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBetterGradientBox(BlockPos pos, Color startColor, Color endColor) {
/*  912 */     float red = startColor.getRed() / 255.0F;
/*  913 */     float green = startColor.getGreen() / 255.0F;
/*  914 */     float blue = startColor.getBlue() / 255.0F;
/*  915 */     float alpha = startColor.getAlpha() / 255.0F;
/*  916 */     float red1 = endColor.getRed() / 255.0F;
/*  917 */     float green1 = endColor.getGreen() / 255.0F;
/*  918 */     float blue1 = endColor.getBlue() / 255.0F;
/*  919 */     float alpha1 = endColor.getAlpha() / 255.0F;
/*  920 */     AxisAlignedBB bb = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/*  921 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  922 */     BufferBuilder builder = tessellator.func_178180_c();
/*  923 */     GlStateManager.func_179094_E();
/*  924 */     GlStateManager.func_179147_l();
/*  925 */     GlStateManager.func_179097_i();
/*  926 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/*  927 */     GlStateManager.func_179090_x();
/*  928 */     GlStateManager.func_179132_a(false);
/*  929 */     GL11.glEnable(2848);
/*  930 */     GL11.glHint(3154, 4354);
/*  931 */     builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
/*  932 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  933 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  934 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  935 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  936 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  937 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  938 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  939 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  940 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  941 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  942 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  943 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  944 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  945 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  946 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  947 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  948 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  949 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  950 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  951 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  952 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  953 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  954 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  955 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  956 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  957 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  958 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  959 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  960 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*  961 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBetterGradientBox(BlockPos pos, Color startColor, Color midColor, Color endColor) {
/*  966 */     float red = startColor.getRed() / 255.0F;
/*  967 */     float green = startColor.getGreen() / 255.0F;
/*  968 */     float blue = startColor.getBlue() / 255.0F;
/*  969 */     float alpha = startColor.getAlpha() / 255.0F;
/*  970 */     float red1 = endColor.getRed() / 255.0F;
/*  971 */     float green1 = endColor.getGreen() / 255.0F;
/*  972 */     float blue1 = endColor.getBlue() / 255.0F;
/*  973 */     float alpha1 = endColor.getAlpha() / 255.0F;
/*  974 */     float red2 = midColor.getRed() / 255.0F;
/*  975 */     float green2 = midColor.getGreen() / 255.0F;
/*  976 */     float blue2 = midColor.getBlue() / 255.0F;
/*  977 */     float alpha2 = midColor.getAlpha() / 255.0F;
/*  978 */     AxisAlignedBB bb = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/*  979 */     double offset = (bb.field_72337_e - bb.field_72338_b) / 2.0D;
/*  980 */     Tessellator tessellator = Tessellator.func_178181_a();
/*  981 */     BufferBuilder builder = tessellator.func_178180_c();
/*  982 */     GlStateManager.func_179094_E();
/*  983 */     GlStateManager.func_179147_l();
/*  984 */     GlStateManager.func_179097_i();
/*  985 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/*  986 */     GlStateManager.func_179090_x();
/*  987 */     GlStateManager.func_179132_a(false);
/*  988 */     GL11.glEnable(2848);
/*  989 */     GL11.glHint(3154, 4354);
/*  990 */     builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
/*  991 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  992 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  993 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  994 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  995 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/*  996 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/*  997 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/*  998 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/*  999 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/* 1000 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1001 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/* 1002 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/* 1003 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/* 1004 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/* 1005 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1006 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1007 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1008 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/* 1009 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1010 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
/* 1011 */     tessellator.func_78381_a();
/* 1012 */     GL11.glDisable(2848);
/* 1013 */     GlStateManager.func_179132_a(true);
/* 1014 */     GlStateManager.func_179126_j();
/* 1015 */     GlStateManager.func_179098_w();
/* 1016 */     GlStateManager.func_179084_k();
/* 1017 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawEvenBetterGradientBox(BlockPos pos, Color startColor, Color midColor, Color endColor) {
/* 1022 */     float red = startColor.getRed() / 255.0F;
/* 1023 */     float green = startColor.getGreen() / 255.0F;
/* 1024 */     float blue = startColor.getBlue() / 255.0F;
/* 1025 */     float alpha = startColor.getAlpha() / 255.0F;
/* 1026 */     float red1 = endColor.getRed() / 255.0F;
/* 1027 */     float green1 = endColor.getGreen() / 255.0F;
/* 1028 */     float blue1 = endColor.getBlue() / 255.0F;
/* 1029 */     float alpha1 = endColor.getAlpha() / 255.0F;
/* 1030 */     float red2 = midColor.getRed() / 255.0F;
/* 1031 */     float green2 = midColor.getGreen() / 255.0F;
/* 1032 */     float blue2 = midColor.getBlue() / 255.0F;
/* 1033 */     float alpha2 = midColor.getAlpha() / 255.0F;
/* 1034 */     AxisAlignedBB bb = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/* 1035 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1036 */     BufferBuilder builder = tessellator.func_178180_c();
/* 1037 */     GlStateManager.func_179094_E();
/* 1038 */     GlStateManager.func_179147_l();
/* 1039 */     GlStateManager.func_179097_i();
/* 1040 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/* 1041 */     GlStateManager.func_179090_x();
/* 1042 */     GlStateManager.func_179132_a(false);
/* 1043 */     GL11.glEnable(2848);
/* 1044 */     GL11.glHint(3154, 4354);
/* 1045 */     builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
/* 1046 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1047 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1048 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1049 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1050 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1051 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1052 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1053 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1054 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1055 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1056 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1057 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1058 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1059 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1060 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1061 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1062 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1063 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1064 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1065 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1066 */     builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1067 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1068 */     builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
/* 1069 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1070 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1071 */     builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1072 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1073 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1074 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1075 */     builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1076 */     tessellator.func_78381_a();
/* 1077 */     GL11.glDisable(2848);
/* 1078 */     GlStateManager.func_179132_a(true);
/* 1079 */     GlStateManager.func_179126_j();
/* 1080 */     GlStateManager.func_179098_w();
/* 1081 */     GlStateManager.func_179084_k();
/* 1082 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBox(BlockPos pos, Color color, double height, boolean gradient, boolean invert, int alpha) {
/* 1087 */     if (gradient) {
/* 1088 */       Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
/* 1089 */       drawOpenGradientBox(pos, invert ? endColor : color, invert ? color : endColor, height);
/*      */       return;
/*      */     } 
/* 1092 */     AxisAlignedBB bb = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m + height, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/* 1093 */     camera.func_78547_a(((Entity)Objects.requireNonNull((T)mc.func_175606_aa())).field_70165_t, (mc.func_175606_aa()).field_70163_u, (mc.func_175606_aa()).field_70161_v);
/* 1094 */     if (camera.func_78546_a(new AxisAlignedBB(bb.field_72340_a + (mc.func_175598_ae()).field_78730_l, bb.field_72338_b + (mc.func_175598_ae()).field_78731_m, bb.field_72339_c + (mc.func_175598_ae()).field_78728_n, bb.field_72336_d + (mc.func_175598_ae()).field_78730_l, bb.field_72337_e + (mc.func_175598_ae()).field_78731_m, bb.field_72334_f + (mc.func_175598_ae()).field_78728_n))) {
/* 1095 */       GlStateManager.func_179094_E();
/* 1096 */       GlStateManager.func_179147_l();
/* 1097 */       GlStateManager.func_179097_i();
/* 1098 */       GlStateManager.func_179120_a(770, 771, 0, 1);
/* 1099 */       GlStateManager.func_179090_x();
/* 1100 */       GlStateManager.func_179132_a(false);
/* 1101 */       GL11.glEnable(2848);
/* 1102 */       GL11.glHint(3154, 4354);
/* 1103 */       RenderGlobal.func_189696_b(bb, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
/* 1104 */       GL11.glDisable(2848);
/* 1105 */       GlStateManager.func_179132_a(true);
/* 1106 */       GlStateManager.func_179126_j();
/* 1107 */       GlStateManager.func_179098_w();
/* 1108 */       GlStateManager.func_179084_k();
/* 1109 */       GlStateManager.func_179121_F();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air) {
/* 1115 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/* 1116 */     if ((air || iblockstate.func_185904_a() != Material.field_151579_a) && mc.field_71441_e.func_175723_af().func_177746_a(pos)) {
/* 1117 */       Vec3d interp = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, mc.func_184121_ak());
/* 1118 */       drawBlockOutline(iblockstate.func_185918_c((World)mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), color, linewidth);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air, double height, boolean gradient, boolean invert, int alpha) {
/* 1124 */     if (gradient) {
/* 1125 */       Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
/* 1126 */       drawGradientBlockOutline(pos, invert ? endColor : color, invert ? color : endColor, linewidth, height);
/*      */       return;
/*      */     } 
/* 1129 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/* 1130 */     if ((air || iblockstate.func_185904_a() != Material.field_151579_a) && mc.field_71441_e.func_175723_af().func_177746_a(pos)) {
/* 1131 */       AxisAlignedBB blockAxis = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m + height, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/* 1132 */       drawBlockOutline(blockAxis.func_186662_g(0.0020000000949949026D), color, linewidth);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBlockOutline(AxisAlignedBB bb, Color color, float linewidth) {
/* 1138 */     float red = color.getRed() / 255.0F;
/* 1139 */     float green = color.getGreen() / 255.0F;
/* 1140 */     float blue = color.getBlue() / 255.0F;
/* 1141 */     float alpha = color.getAlpha() / 255.0F;
/* 1142 */     GlStateManager.func_179094_E();
/* 1143 */     GlStateManager.func_179147_l();
/* 1144 */     GlStateManager.func_179097_i();
/* 1145 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/* 1146 */     GlStateManager.func_179090_x();
/* 1147 */     GlStateManager.func_179132_a(false);
/* 1148 */     GL11.glEnable(2848);
/* 1149 */     GL11.glHint(3154, 4354);
/* 1150 */     GL11.glLineWidth(linewidth);
/* 1151 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1152 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 1153 */     bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
/* 1154 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1155 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1156 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1157 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1158 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1159 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1160 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1161 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1162 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1163 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1164 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1165 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1166 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1167 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1168 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1169 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1170 */     tessellator.func_78381_a();
/* 1171 */     GL11.glDisable(2848);
/* 1172 */     GlStateManager.func_179132_a(true);
/* 1173 */     GlStateManager.func_179126_j();
/* 1174 */     GlStateManager.func_179098_w();
/* 1175 */     GlStateManager.func_179084_k();
/* 1176 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBoxESP(BlockPos pos, Color color, float lineWidth, boolean outline, boolean box, int boxAlpha) {
/* 1181 */     AxisAlignedBB bb = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/* 1182 */     camera.func_78547_a(((Entity)Objects.requireNonNull((T)mc.func_175606_aa())).field_70165_t, (mc.func_175606_aa()).field_70163_u, (mc.func_175606_aa()).field_70161_v);
/* 1183 */     if (camera.func_78546_a(new AxisAlignedBB(bb.field_72340_a + (mc.func_175598_ae()).field_78730_l, bb.field_72338_b + (mc.func_175598_ae()).field_78731_m, bb.field_72339_c + (mc.func_175598_ae()).field_78728_n, bb.field_72336_d + (mc.func_175598_ae()).field_78730_l, bb.field_72337_e + (mc.func_175598_ae()).field_78731_m, bb.field_72334_f + (mc.func_175598_ae()).field_78728_n))) {
/* 1184 */       GlStateManager.func_179094_E();
/* 1185 */       GlStateManager.func_179147_l();
/* 1186 */       GlStateManager.func_179097_i();
/* 1187 */       GlStateManager.func_179120_a(770, 771, 0, 1);
/* 1188 */       GlStateManager.func_179090_x();
/* 1189 */       GlStateManager.func_179132_a(false);
/* 1190 */       GL11.glEnable(2848);
/* 1191 */       GL11.glHint(3154, 4354);
/* 1192 */       GL11.glLineWidth(lineWidth);
/* 1193 */       double dist = mc.field_71439_g.func_70011_f((pos.func_177958_n() + 0.5F), (pos.func_177956_o() + 0.5F), (pos.func_177952_p() + 0.5F)) * 0.75D;
/* 1194 */       if (box) {
/* 1195 */         RenderGlobal.func_189696_b(bb, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, boxAlpha / 255.0F);
/*      */       }
/* 1197 */       if (outline) {
/* 1198 */         RenderGlobal.func_189694_a(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
/*      */       }
/* 1200 */       GL11.glDisable(2848);
/* 1201 */       GlStateManager.func_179132_a(true);
/* 1202 */       GlStateManager.func_179126_j();
/* 1203 */       GlStateManager.func_179098_w();
/* 1204 */       GlStateManager.func_179084_k();
/* 1205 */       GlStateManager.func_179121_F();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawText(AxisAlignedBB pos, String text) {
/* 1211 */     if (pos == null || text == null) {
/*      */       return;
/*      */     }
/* 1214 */     GlStateManager.func_179094_E();
/* 1215 */     glBillboardDistanceScaled((float)pos.field_72340_a + 0.5F, (float)pos.field_72338_b + 0.5F, (float)pos.field_72339_c + 0.5F, (EntityPlayer)mc.field_71439_g, 1.0F);
/* 1216 */     GlStateManager.func_179097_i();
/* 1217 */     GlStateManager.func_179137_b(-(Banzem.textManager.getStringWidth(text) / 2.0D), 0.0D, 0.0D);
/* 1218 */     Banzem.textManager.drawStringWithShadow(text, 0.0F, 0.0F, -5592406);
/* 1219 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawOutlinedBlockESP(BlockPos pos, Color color, float linewidth) {
/* 1224 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
/* 1225 */     Vec3d interp = EntityUtil.interpolateEntity((Entity)mc.field_71439_g, mc.func_184121_ak());
/* 1226 */     drawBoundingBox(iblockstate.func_185918_c((World)mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), linewidth, ColorUtil.toRGBA(color));
/*      */   }
/*      */ 
/*      */   
/*      */   public static void blockEsp(BlockPos blockPos, Color c, double length, double length2) {
/* 1231 */     double x = blockPos.func_177958_n() - mc.field_175616_W.field_78725_b;
/* 1232 */     double y = blockPos.func_177956_o() - mc.field_175616_W.field_78726_c;
/* 1233 */     double z = blockPos.func_177952_p() - mc.field_175616_W.field_78723_d;
/* 1234 */     GL11.glPushMatrix();
/* 1235 */     GL11.glBlendFunc(770, 771);
/* 1236 */     GL11.glEnable(3042);
/* 1237 */     GL11.glLineWidth(2.0F);
/* 1238 */     GL11.glDisable(3553);
/* 1239 */     GL11.glDisable(2929);
/* 1240 */     GL11.glDepthMask(false);
/* 1241 */     GL11.glColor4d((c.getRed() / 255.0F), (c.getGreen() / 255.0F), (c.getBlue() / 255.0F), 0.25D);
/* 1242 */     drawColorBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0D, z + length), 0.0F, 0.0F, 0.0F, 0.0F);
/* 1243 */     GL11.glColor4d(0.0D, 0.0D, 0.0D, 0.5D);
/* 1244 */     drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0D, z + length));
/* 1245 */     GL11.glLineWidth(2.0F);
/* 1246 */     GL11.glEnable(3553);
/* 1247 */     GL11.glEnable(2929);
/* 1248 */     GL11.glDepthMask(true);
/* 1249 */     GL11.glDisable(3042);
/* 1250 */     GL11.glPopMatrix();
/* 1251 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawRect(float x, float y, float w, float h, int color) {
/* 1256 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/* 1257 */     float red = (color >> 16 & 0xFF) / 255.0F;
/* 1258 */     float green = (color >> 8 & 0xFF) / 255.0F;
/* 1259 */     float blue = (color & 0xFF) / 255.0F;
/* 1260 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1261 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 1262 */     GlStateManager.func_179147_l();
/* 1263 */     GlStateManager.func_179090_x();
/* 1264 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/* 1265 */     bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
/* 1266 */     bufferbuilder.func_181662_b(x, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1267 */     bufferbuilder.func_181662_b(w, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1268 */     bufferbuilder.func_181662_b(w, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1269 */     bufferbuilder.func_181662_b(x, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1270 */     tessellator.func_78381_a();
/* 1271 */     GlStateManager.func_179098_w();
/* 1272 */     GlStateManager.func_179084_k();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
/* 1277 */     Tessellator ts = Tessellator.func_178181_a();
/* 1278 */     BufferBuilder vb = ts.func_178180_c();
/* 1279 */     vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/* 1280 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1281 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1282 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1283 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1284 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1285 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1286 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1287 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1288 */     ts.func_78381_a();
/* 1289 */     vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/* 1290 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1291 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1292 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1293 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1294 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1295 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1296 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1297 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1298 */     ts.func_78381_a();
/* 1299 */     vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/* 1300 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1301 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1302 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1303 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1304 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1305 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1306 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1307 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1308 */     ts.func_78381_a();
/* 1309 */     vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/* 1310 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1311 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1312 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1313 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1314 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1315 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1316 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1317 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1318 */     ts.func_78381_a();
/* 1319 */     vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/* 1320 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1321 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1322 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1323 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1324 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1325 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1326 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1327 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1328 */     ts.func_78381_a();
/* 1329 */     vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/* 1330 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1331 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1332 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1333 */     vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1334 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1335 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1336 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1337 */     vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1338 */     ts.func_78381_a();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
/* 1343 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1344 */     BufferBuilder vertexbuffer = tessellator.func_178180_c();
/* 1345 */     vertexbuffer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
/* 1346 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
/* 1347 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
/* 1348 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
/* 1349 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
/* 1350 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
/* 1351 */     tessellator.func_78381_a();
/* 1352 */     vertexbuffer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
/* 1353 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
/* 1354 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
/* 1355 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
/* 1356 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
/* 1357 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
/* 1358 */     tessellator.func_78381_a();
/* 1359 */     vertexbuffer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
/* 1360 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
/* 1361 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
/* 1362 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
/* 1363 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
/* 1364 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
/* 1365 */     vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
/* 1366 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
/* 1367 */     vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
/* 1368 */     tessellator.func_78381_a();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void glrendermethod() {
/* 1373 */     GL11.glEnable(3042);
/* 1374 */     GL11.glBlendFunc(770, 771);
/* 1375 */     GL11.glEnable(2848);
/* 1376 */     GL11.glLineWidth(2.0F);
/* 1377 */     GL11.glDisable(3553);
/* 1378 */     GL11.glEnable(2884);
/* 1379 */     GL11.glDisable(2929);
/* 1380 */     double viewerPosX = (mc.func_175598_ae()).field_78730_l;
/* 1381 */     double viewerPosY = (mc.func_175598_ae()).field_78731_m;
/* 1382 */     double viewerPosZ = (mc.func_175598_ae()).field_78728_n;
/* 1383 */     GL11.glPushMatrix();
/* 1384 */     GL11.glTranslated(-viewerPosX, -viewerPosY, -viewerPosZ);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void glStart(float n, float n2, float n3, float n4) {
/* 1389 */     glrendermethod();
/* 1390 */     GL11.glColor4f(n, n2, n3, n4);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void glEnd() {
/* 1395 */     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 1396 */     GL11.glPopMatrix();
/* 1397 */     GL11.glEnable(2929);
/* 1398 */     GL11.glEnable(3553);
/* 1399 */     GL11.glDisable(3042);
/* 1400 */     GL11.glDisable(2848);
/*      */   }
/*      */ 
/*      */   
/*      */   public static AxisAlignedBB getBoundingBox(BlockPos blockPos) {
/* 1405 */     return mc.field_71441_e.func_180495_p(blockPos).func_185900_c((IBlockAccess)mc.field_71441_e, blockPos).func_186670_a(blockPos);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawOutlinedBox(AxisAlignedBB axisAlignedBB) {
/* 1410 */     GL11.glBegin(1);
/* 1411 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
/* 1412 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
/* 1413 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
/* 1414 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
/* 1415 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
/* 1416 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
/* 1417 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
/* 1418 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
/* 1419 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
/* 1420 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
/* 1421 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
/* 1422 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
/* 1423 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
/* 1424 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
/* 1425 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
/* 1426 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
/* 1427 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
/* 1428 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
/* 1429 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
/* 1430 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
/* 1431 */     GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
/* 1432 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
/* 1433 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
/* 1434 */     GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
/* 1435 */     GL11.glEnd();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawFilledBoxESPN(BlockPos pos, Color color) {
/* 1440 */     AxisAlignedBB bb = new AxisAlignedBB(pos.func_177958_n() - (mc.func_175598_ae()).field_78730_l, pos.func_177956_o() - (mc.func_175598_ae()).field_78731_m, pos.func_177952_p() - (mc.func_175598_ae()).field_78728_n, (pos.func_177958_n() + 1) - (mc.func_175598_ae()).field_78730_l, (pos.func_177956_o() + 1) - (mc.func_175598_ae()).field_78731_m, (pos.func_177952_p() + 1) - (mc.func_175598_ae()).field_78728_n);
/* 1441 */     int rgba = ColorUtil.toRGBA(color);
/* 1442 */     drawFilledBox(bb, rgba);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawFilledBox(AxisAlignedBB bb, int color) {
/* 1447 */     GlStateManager.func_179094_E();
/* 1448 */     GlStateManager.func_179147_l();
/* 1449 */     GlStateManager.func_179097_i();
/* 1450 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/* 1451 */     GlStateManager.func_179090_x();
/* 1452 */     GlStateManager.func_179132_a(false);
/* 1453 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/* 1454 */     float red = (color >> 16 & 0xFF) / 255.0F;
/* 1455 */     float green = (color >> 8 & 0xFF) / 255.0F;
/* 1456 */     float blue = (color & 0xFF) / 255.0F;
/* 1457 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1458 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 1459 */     bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
/* 1460 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1461 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1462 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1463 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1464 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1465 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1466 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1467 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1468 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1469 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1470 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1471 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1472 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1473 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1474 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1475 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1476 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1477 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1478 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1479 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1480 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1481 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1482 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1483 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1484 */     tessellator.func_78381_a();
/* 1485 */     GlStateManager.func_179132_a(true);
/* 1486 */     GlStateManager.func_179126_j();
/* 1487 */     GlStateManager.func_179098_w();
/* 1488 */     GlStateManager.func_179084_k();
/* 1489 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBoundingBox(AxisAlignedBB bb, float width, int color) {
/* 1494 */     GlStateManager.func_179094_E();
/* 1495 */     GlStateManager.func_179147_l();
/* 1496 */     GlStateManager.func_179097_i();
/* 1497 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/* 1498 */     GlStateManager.func_179090_x();
/* 1499 */     GlStateManager.func_179132_a(false);
/* 1500 */     GL11.glEnable(2848);
/* 1501 */     GL11.glHint(3154, 4354);
/* 1502 */     GL11.glLineWidth(width);
/* 1503 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/* 1504 */     float red = (color >> 16 & 0xFF) / 255.0F;
/* 1505 */     float green = (color >> 8 & 0xFF) / 255.0F;
/* 1506 */     float blue = (color & 0xFF) / 255.0F;
/* 1507 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1508 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 1509 */     bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
/* 1510 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1511 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1512 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1513 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1514 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1515 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1516 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1517 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1518 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1519 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1520 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1521 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1522 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1523 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1524 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1525 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1526 */     tessellator.func_78381_a();
/* 1527 */     GL11.glDisable(2848);
/* 1528 */     GlStateManager.func_179132_a(true);
/* 1529 */     GlStateManager.func_179126_j();
/* 1530 */     GlStateManager.func_179098_w();
/* 1531 */     GlStateManager.func_179084_k();
/* 1532 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void glBillboard(float x, float y, float z) {
/* 1537 */     float scale = 0.02666667F;
/* 1538 */     GlStateManager.func_179137_b(x - (mc.func_175598_ae()).field_78725_b, y - (mc.func_175598_ae()).field_78726_c, z - (mc.func_175598_ae()).field_78723_d);
/* 1539 */     GlStateManager.func_187432_a(0.0F, 1.0F, 0.0F);
/* 1540 */     GlStateManager.func_179114_b(-mc.field_71439_g.field_70177_z, 0.0F, 1.0F, 0.0F);
/* 1541 */     GlStateManager.func_179114_b(mc.field_71439_g.field_70125_A, (mc.field_71474_y.field_74320_O == 2) ? -1.0F : 1.0F, 0.0F, 0.0F);
/* 1542 */     GlStateManager.func_179152_a(-scale, -scale, scale);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
/* 1547 */     glBillboard(x, y, z);
/* 1548 */     int distance = (int)player.func_70011_f(x, y, z);
/* 1549 */     float scaleDistance = distance / 2.0F / (2.0F + 2.0F - scale);
/* 1550 */     if (scaleDistance < 1.0F) {
/* 1551 */       scaleDistance = 1.0F;
/*      */     }
/* 1553 */     GlStateManager.func_179152_a(scaleDistance, scaleDistance, scaleDistance);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawColoredBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
/* 1558 */     GlStateManager.func_179094_E();
/* 1559 */     GlStateManager.func_179147_l();
/* 1560 */     GlStateManager.func_179097_i();
/* 1561 */     GlStateManager.func_179120_a(770, 771, 0, 1);
/* 1562 */     GlStateManager.func_179090_x();
/* 1563 */     GlStateManager.func_179132_a(false);
/* 1564 */     GL11.glEnable(2848);
/* 1565 */     GL11.glHint(3154, 4354);
/* 1566 */     GL11.glLineWidth(width);
/* 1567 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1568 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 1569 */     bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
/* 1570 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, 0.0F).func_181675_d();
/* 1571 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1572 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1573 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1574 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1575 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1576 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1577 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1578 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1579 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1580 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1581 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, 0.0F).func_181675_d();
/* 1582 */     bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1583 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, 0.0F).func_181675_d();
/* 1584 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1585 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, 0.0F).func_181675_d();
/* 1586 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1587 */     bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, 0.0F).func_181675_d();
/* 1588 */     tessellator.func_78381_a();
/* 1589 */     GL11.glDisable(2848);
/* 1590 */     GlStateManager.func_179132_a(true);
/* 1591 */     GlStateManager.func_179126_j();
/* 1592 */     GlStateManager.func_179098_w();
/* 1593 */     GlStateManager.func_179084_k();
/* 1594 */     GlStateManager.func_179121_F();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
/* 1599 */     Sphere s = new Sphere();
/* 1600 */     GL11.glPushMatrix();
/* 1601 */     GL11.glBlendFunc(770, 771);
/* 1602 */     GL11.glEnable(3042);
/* 1603 */     GL11.glLineWidth(1.2F);
/* 1604 */     GL11.glDisable(3553);
/* 1605 */     GL11.glDisable(2929);
/* 1606 */     GL11.glDepthMask(false);
/* 1607 */     s.setDrawStyle(100013);
/* 1608 */     GL11.glTranslated(x - mc.field_175616_W.field_78725_b, y - mc.field_175616_W.field_78726_c, z - mc.field_175616_W.field_78723_d);
/* 1609 */     s.draw(size, slices, stacks);
/* 1610 */     GL11.glLineWidth(2.0F);
/* 1611 */     GL11.glEnable(3553);
/* 1612 */     GL11.glEnable(2929);
/* 1613 */     GL11.glDepthMask(true);
/* 1614 */     GL11.glDisable(3042);
/* 1615 */     GL11.glPopMatrix();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawBar(GLUProjection.Projection projection, float width, float height, float totalWidth, Color startColor, Color outlineColor) {
/* 1620 */     if (projection.getType() == GLUProjection.Projection.Type.INSIDE) {
/* 1621 */       GlStateManager.func_179094_E();
/* 1622 */       GlStateManager.func_179137_b(projection.getX(), projection.getY(), 0.0D);
/* 1623 */       drawOutlineRect(-(totalWidth / 2.0F), -(height / 2.0F), totalWidth, height, outlineColor.getRGB());
/* 1624 */       drawRect(-(totalWidth / 2.0F), -(height / 2.0F), width, height, startColor.getRGB());
/* 1625 */       GlStateManager.func_179137_b(-projection.getX(), -projection.getY(), 0.0D);
/* 1626 */       GlStateManager.func_179121_F();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawProjectedText(GLUProjection.Projection projection, float addX, float addY, String text, Color color, boolean shadow) {
/* 1632 */     if (projection.getType() == GLUProjection.Projection.Type.INSIDE) {
/* 1633 */       GlStateManager.func_179094_E();
/* 1634 */       GlStateManager.func_179137_b(projection.getX(), projection.getY(), 0.0D);
/* 1635 */       Banzem.textManager.drawString(text, -(Banzem.textManager.getStringWidth(text) / 2.0F) + addX, addY, color.getRGB(), shadow);
/* 1636 */       GlStateManager.func_179137_b(-projection.getX(), -projection.getY(), 0.0D);
/* 1637 */       GlStateManager.func_179121_F();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawChungusESP(GLUProjection.Projection projection, float width, float height, ResourceLocation location) {
/* 1643 */     if (projection.getType() == GLUProjection.Projection.Type.INSIDE) {
/* 1644 */       GlStateManager.func_179094_E();
/* 1645 */       GlStateManager.func_179137_b(projection.getX(), projection.getY(), 0.0D);
/* 1646 */       mc.func_110434_K().func_110577_a(location);
/* 1647 */       GlStateManager.func_179098_w();
/* 1648 */       GlStateManager.func_179084_k();
/* 1649 */       mc.func_110434_K().func_110577_a(location);
/* 1650 */       drawCompleteImage(0.0F, 0.0F, width, height);
/* 1651 */       mc.func_110434_K().func_147645_c(location);
/* 1652 */       GlStateManager.func_179147_l();
/* 1653 */       GlStateManager.func_179090_x();
/* 1654 */       GlStateManager.func_179137_b(-projection.getX(), -projection.getY(), 0.0D);
/* 1655 */       GlStateManager.func_179121_F();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawCompleteImage(float posX, float posY, float width, float height) {
/* 1661 */     GL11.glPushMatrix();
/* 1662 */     GL11.glTranslatef(posX, posY, 0.0F);
/* 1663 */     GL11.glBegin(7);
/* 1664 */     GL11.glTexCoord2f(0.0F, 0.0F);
/* 1665 */     GL11.glVertex3f(0.0F, 0.0F, 0.0F);
/* 1666 */     GL11.glTexCoord2f(0.0F, 1.0F);
/* 1667 */     GL11.glVertex3f(0.0F, height, 0.0F);
/* 1668 */     GL11.glTexCoord2f(1.0F, 1.0F);
/* 1669 */     GL11.glVertex3f(width, height, 0.0F);
/* 1670 */     GL11.glTexCoord2f(1.0F, 0.0F);
/* 1671 */     GL11.glVertex3f(width, 0.0F, 0.0F);
/* 1672 */     GL11.glEnd();
/* 1673 */     GL11.glPopMatrix();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawOutlineRect(float x, float y, float w, float h, int color) {
/* 1678 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/* 1679 */     float red = (color >> 16 & 0xFF) / 255.0F;
/* 1680 */     float green = (color >> 8 & 0xFF) / 255.0F;
/* 1681 */     float blue = (color & 0xFF) / 255.0F;
/* 1682 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1683 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 1684 */     GlStateManager.func_179147_l();
/* 1685 */     GlStateManager.func_179090_x();
/* 1686 */     GlStateManager.func_187441_d(1.0F);
/* 1687 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/* 1688 */     bufferbuilder.func_181668_a(2, DefaultVertexFormats.field_181706_f);
/* 1689 */     bufferbuilder.func_181662_b(x, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1690 */     bufferbuilder.func_181662_b(w, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1691 */     bufferbuilder.func_181662_b(w, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1692 */     bufferbuilder.func_181662_b(x, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1693 */     tessellator.func_78381_a();
/* 1694 */     GlStateManager.func_179098_w();
/* 1695 */     GlStateManager.func_179084_k();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void draw3DRect(float x, float y, float w, float h, Color startColor, Color endColor, float lineWidth) {
/* 1700 */     float alpha = startColor.getAlpha() / 255.0F;
/* 1701 */     float red = startColor.getRed() / 255.0F;
/* 1702 */     float green = startColor.getGreen() / 255.0F;
/* 1703 */     float blue = startColor.getBlue() / 255.0F;
/* 1704 */     float alpha1 = endColor.getAlpha() / 255.0F;
/* 1705 */     float red1 = endColor.getRed() / 255.0F;
/* 1706 */     float green1 = endColor.getGreen() / 255.0F;
/* 1707 */     float blue1 = endColor.getBlue() / 255.0F;
/* 1708 */     Tessellator tessellator = Tessellator.func_178181_a();
/* 1709 */     BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 1710 */     GlStateManager.func_179147_l();
/* 1711 */     GlStateManager.func_179090_x();
/* 1712 */     GlStateManager.func_187441_d(lineWidth);
/* 1713 */     GlStateManager.func_179120_a(770, 771, 1, 0);
/* 1714 */     bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
/* 1715 */     bufferbuilder.func_181662_b(x, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1716 */     bufferbuilder.func_181662_b(w, h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1717 */     bufferbuilder.func_181662_b(w, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1718 */     bufferbuilder.func_181662_b(x, y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 1719 */     tessellator.func_78381_a();
/* 1720 */     GlStateManager.func_179098_w();
/* 1721 */     GlStateManager.func_179084_k();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawClock(float x, float y, float radius, int slices, int loops, float lineWidth, boolean fill, Color color) {
/* 1726 */     Disk disk = new Disk();
/* 1727 */     int hourAngle = 180 + -(Calendar.getInstance().get(10) * 30 + Calendar.getInstance().get(12) / 2);
/* 1728 */     int minuteAngle = 180 + -(Calendar.getInstance().get(12) * 6 + Calendar.getInstance().get(13) / 10);
/* 1729 */     int secondAngle = 180 + -(Calendar.getInstance().get(13) * 6);
/* 1730 */     int totalMinutesTime = Calendar.getInstance().get(12);
/* 1731 */     int totalHoursTime = Calendar.getInstance().get(10);
/* 1732 */     if (fill) {
/* 1733 */       GL11.glPushMatrix();
/* 1734 */       GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
/* 1735 */       GL11.glBlendFunc(770, 771);
/* 1736 */       GL11.glEnable(3042);
/* 1737 */       GL11.glLineWidth(lineWidth);
/* 1738 */       GL11.glDisable(3553);
/* 1739 */       disk.setOrientation(100020);
/* 1740 */       disk.setDrawStyle(100012);
/* 1741 */       GL11.glTranslated(x, y, 0.0D);
/* 1742 */       disk.draw(0.0F, radius, slices, loops);
/* 1743 */       GL11.glEnable(3553);
/* 1744 */       GL11.glDisable(3042);
/* 1745 */       GL11.glPopMatrix();
/*      */     } else {
/* 1747 */       GL11.glPushMatrix();
/* 1748 */       GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
/* 1749 */       GL11.glEnable(3042);
/* 1750 */       GL11.glLineWidth(lineWidth);
/* 1751 */       GL11.glDisable(3553);
/* 1752 */       GL11.glBegin(3);
/* 1753 */       ArrayList<Vec2f> hVectors = new ArrayList<>();
/* 1754 */       float hue = (float)(System.currentTimeMillis() % 7200L) / 7200.0F;
/* 1755 */       for (int i = 0; i <= 360; i++) {
/* 1756 */         Vec2f vec = new Vec2f(x + (float)Math.sin(i * Math.PI / 180.0D) * radius, y + (float)Math.cos(i * Math.PI / 180.0D) * radius);
/* 1757 */         hVectors.add(vec);
/*      */       } 
/* 1759 */       Color color1 = new Color(Color.HSBtoRGB(hue, 1.0F, 1.0F));
/* 1760 */       for (int j = 0; j < hVectors.size() - 1; j++) {
/* 1761 */         GL11.glColor4f(color1.getRed() / 255.0F, color1.getGreen() / 255.0F, color1.getBlue() / 255.0F, color1.getAlpha() / 255.0F);
/* 1762 */         GL11.glVertex3d(((Vec2f)hVectors.get(j)).field_189982_i, ((Vec2f)hVectors.get(j)).field_189983_j, 0.0D);
/* 1763 */         GL11.glVertex3d(((Vec2f)hVectors.get(j + 1)).field_189982_i, ((Vec2f)hVectors.get(j + 1)).field_189983_j, 0.0D);
/* 1764 */         color1 = new Color(Color.HSBtoRGB(hue += 0.0027777778F, 1.0F, 1.0F));
/*      */       } 
/* 1766 */       GL11.glEnd();
/* 1767 */       GL11.glEnable(3553);
/* 1768 */       GL11.glDisable(3042);
/* 1769 */       GL11.glPopMatrix();
/*      */     } 
/* 1771 */     drawLine(x, y, x + (float)Math.sin(hourAngle * Math.PI / 180.0D) * radius / 2.0F, y + (float)Math.cos(hourAngle * Math.PI / 180.0D) * radius / 2.0F, 1.0F, Color.WHITE.getRGB());
/* 1772 */     drawLine(x, y, x + (float)Math.sin(minuteAngle * Math.PI / 180.0D) * (radius - radius / 10.0F), y + (float)Math.cos(minuteAngle * Math.PI / 180.0D) * (radius - radius / 10.0F), 1.0F, Color.WHITE.getRGB());
/* 1773 */     drawLine(x, y, x + (float)Math.sin(secondAngle * Math.PI / 180.0D) * (radius - radius / 10.0F), y + (float)Math.cos(secondAngle * Math.PI / 180.0D) * (radius - radius / 10.0F), 1.0F, Color.RED.getRGB());
/*      */   }
/*      */ 
/*      */   
/*      */   public static void GLPre(float lineWidth) {
/* 1778 */     depth = GL11.glIsEnabled(2896);
/* 1779 */     texture = GL11.glIsEnabled(3042);
/* 1780 */     clean = GL11.glIsEnabled(3553);
/* 1781 */     bind = GL11.glIsEnabled(2929);
/* 1782 */     override = GL11.glIsEnabled(2848);
/* 1783 */     GLPre(depth, texture, clean, bind, override, lineWidth);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void GlPost() {
/* 1788 */     GLPost(depth, texture, clean, bind, override);
/*      */   }
/*      */ 
/*      */   
/*      */   private static void GLPre(boolean depth, boolean texture, boolean clean, boolean bind, boolean override, float lineWidth) {
/* 1793 */     if (depth) {
/* 1794 */       GL11.glDisable(2896);
/*      */     }
/* 1796 */     if (!texture) {
/* 1797 */       GL11.glEnable(3042);
/*      */     }
/* 1799 */     GL11.glLineWidth(lineWidth);
/* 1800 */     if (clean) {
/* 1801 */       GL11.glDisable(3553);
/*      */     }
/* 1803 */     if (bind) {
/* 1804 */       GL11.glDisable(2929);
/*      */     }
/* 1806 */     if (!override) {
/* 1807 */       GL11.glEnable(2848);
/*      */     }
/* 1809 */     GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
/* 1810 */     GL11.glHint(3154, 4354);
/* 1811 */     GlStateManager.func_179132_a(false);
/*      */   }
/*      */ 
/*      */   
/*      */   public static float[][] getBipedRotations(ModelBiped biped) {
/* 1816 */     float[][] rotations = new float[5][];
/* 1817 */     float[] headRotation = { biped.field_78116_c.field_78795_f, biped.field_78116_c.field_78796_g, biped.field_78116_c.field_78808_h };
/* 1818 */     rotations[0] = headRotation;
/* 1819 */     float[] rightArmRotation = { biped.field_178723_h.field_78795_f, biped.field_178723_h.field_78796_g, biped.field_178723_h.field_78808_h };
/* 1820 */     rotations[1] = rightArmRotation;
/* 1821 */     float[] leftArmRotation = { biped.field_178724_i.field_78795_f, biped.field_178724_i.field_78796_g, biped.field_178724_i.field_78808_h };
/* 1822 */     rotations[2] = leftArmRotation;
/* 1823 */     float[] rightLegRotation = { biped.field_178721_j.field_78795_f, biped.field_178721_j.field_78796_g, biped.field_178721_j.field_78808_h };
/* 1824 */     rotations[3] = rightLegRotation;
/* 1825 */     float[] leftLegRotation = { biped.field_178722_k.field_78795_f, biped.field_178722_k.field_78796_g, biped.field_178722_k.field_78808_h };
/* 1826 */     rotations[4] = leftLegRotation;
/* 1827 */     return rotations;
/*      */   }
/*      */ 
/*      */   
/*      */   private static void GLPost(boolean depth, boolean texture, boolean clean, boolean bind, boolean override) {
/* 1832 */     GlStateManager.func_179132_a(true);
/* 1833 */     if (!override) {
/* 1834 */       GL11.glDisable(2848);
/*      */     }
/* 1836 */     if (bind) {
/* 1837 */       GL11.glEnable(2929);
/*      */     }
/* 1839 */     if (clean) {
/* 1840 */       GL11.glEnable(3553);
/*      */     }
/* 1842 */     if (!texture) {
/* 1843 */       GL11.glDisable(3042);
/*      */     }
/* 1845 */     if (depth) {
/* 1846 */       GL11.glEnable(2896);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawArc(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
/* 1852 */     GL11.glBegin(4);
/* 1853 */     int i = (int)(num_segments / 360.0F / start_angle) + 1;
/* 1854 */     while (i <= num_segments / 360.0F / end_angle) {
/* 1855 */       double previousangle = 6.283185307179586D * (i - 1) / num_segments;
/* 1856 */       double angle = 6.283185307179586D * i / num_segments;
/* 1857 */       GL11.glVertex2d(cx, cy);
/* 1858 */       GL11.glVertex2d(cx + Math.cos(angle) * r, cy + Math.sin(angle) * r);
/* 1859 */       GL11.glVertex2d(cx + Math.cos(previousangle) * r, cy + Math.sin(previousangle) * r);
/* 1860 */       i++;
/*      */     } 
/* 1862 */     glEnd();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawArcOutline(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
/* 1867 */     GL11.glBegin(2);
/* 1868 */     int i = (int)(num_segments / 360.0F / start_angle) + 1;
/* 1869 */     while (i <= num_segments / 360.0F / end_angle) {
/* 1870 */       double angle = 6.283185307179586D * i / num_segments;
/* 1871 */       GL11.glVertex2d(cx + Math.cos(angle) * r, cy + Math.sin(angle) * r);
/* 1872 */       i++;
/*      */     } 
/* 1874 */     glEnd();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawCircleOutline(float x, float y, float radius) {
/* 1879 */     drawCircleOutline(x, y, radius, 0, 360, 40);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawCircleOutline(float x, float y, float radius, int start, int end, int segments) {
/* 1884 */     drawArcOutline(x, y, radius, start, end, segments);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawCircle(float x, float y, float radius) {
/* 1889 */     drawCircle(x, y, radius, 0, 360, 64);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawCircle(float x, float y, float radius, int start, int end, int segments) {
/* 1894 */     drawArc(x, y, radius, start, end, segments);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawOutlinedRoundedRectangle(int x, int y, int width, int height, float radius, float dR, float dG, float dB, float dA, float outlineWidth) {
/* 1899 */     drawRoundedRectangle(x, y, width, height, radius);
/* 1900 */     GL11.glColor4f(dR, dG, dB, dA);
/* 1901 */     drawRoundedRectangle(x + outlineWidth, y + outlineWidth, width - outlineWidth * 2.0F, height - outlineWidth * 2.0F, radius);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawRectangle(float x, float y, float width, float height) {
/* 1906 */     GL11.glEnable(3042);
/* 1907 */     GL11.glBlendFunc(770, 771);
/* 1908 */     GL11.glBegin(2);
/* 1909 */     GL11.glVertex2d(width, 0.0D);
/* 1910 */     GL11.glVertex2d(0.0D, 0.0D);
/* 1911 */     GL11.glVertex2d(0.0D, height);
/* 1912 */     GL11.glVertex2d(width, height);
/* 1913 */     glEnd();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawRectangleXY(float x, float y, float width, float height) {
/* 1918 */     GL11.glEnable(3042);
/* 1919 */     GL11.glBlendFunc(770, 771);
/* 1920 */     GL11.glBegin(2);
/* 1921 */     GL11.glVertex2d((x + width), y);
/* 1922 */     GL11.glVertex2d(x, y);
/* 1923 */     GL11.glVertex2d(x, (y + height));
/* 1924 */     GL11.glVertex2d((x + width), (y + height));
/* 1925 */     glEnd();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawFilledRectangle(float x, float y, float width, float height) {
/* 1930 */     GL11.glEnable(3042);
/* 1931 */     GL11.glBlendFunc(770, 771);
/* 1932 */     GL11.glBegin(7);
/* 1933 */     GL11.glVertex2d((x + width), y);
/* 1934 */     GL11.glVertex2d(x, y);
/* 1935 */     GL11.glVertex2d(x, (y + height));
/* 1936 */     GL11.glVertex2d((x + width), (y + height));
/* 1937 */     glEnd();
/*      */   }
/*      */ 
/*      */   
/*      */   public static Vec3d to2D(double x, double y, double z) {
/* 1942 */     GL11.glGetFloat(2982, modelView);
/* 1943 */     GL11.glGetFloat(2983, projection);
/* 1944 */     GL11.glGetInteger(2978, viewport);
/* 1945 */     boolean result = GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, screenCoords);
/* 1946 */     if (result) {
/* 1947 */       return new Vec3d(screenCoords.get(0), (Display.getHeight() - screenCoords.get(1)), screenCoords.get(2));
/*      */     }
/* 1949 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, boolean outline, float outlineWidth, int color) {
/* 1954 */     boolean blend = GL11.glIsEnabled(3042);
/* 1955 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/* 1956 */     GL11.glEnable(3042);
/* 1957 */     GL11.glDisable(3553);
/* 1958 */     GL11.glBlendFunc(770, 771);
/* 1959 */     GL11.glEnable(2848);
/* 1960 */     GL11.glPushMatrix();
/* 1961 */     hexColor(color);
/* 1962 */     GL11.glBegin(7);
/* 1963 */     GL11.glVertex2d(x, y);
/* 1964 */     GL11.glVertex2d((x - size / widthDiv), (y + size));
/* 1965 */     GL11.glVertex2d(x, (y + size / heightDiv));
/* 1966 */     GL11.glVertex2d((x + size / widthDiv), (y + size));
/* 1967 */     GL11.glVertex2d(x, y);
/* 1968 */     GL11.glEnd();
/* 1969 */     if (outline) {
/* 1970 */       GL11.glLineWidth(outlineWidth);
/* 1971 */       GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
/* 1972 */       GL11.glBegin(2);
/* 1973 */       GL11.glVertex2d(x, y);
/* 1974 */       GL11.glVertex2d((x - size / widthDiv), (y + size));
/* 1975 */       GL11.glVertex2d(x, (y + size / heightDiv));
/* 1976 */       GL11.glVertex2d((x + size / widthDiv), (y + size));
/* 1977 */       GL11.glVertex2d(x, y);
/* 1978 */       GL11.glEnd();
/*      */     } 
/* 1980 */     GL11.glPopMatrix();
/* 1981 */     GL11.glEnable(3553);
/* 1982 */     if (!blend) {
/* 1983 */       GL11.glDisable(3042);
/*      */     }
/* 1985 */     GL11.glDisable(2848);
/*      */   }
/*      */ 
/*      */   
/*      */   public static int getRainbow(int speed, int offset, float s, float b) {
/* 1990 */     float hue = (float)((System.currentTimeMillis() + offset) % speed);
/* 1991 */     return Color.getHSBColor(hue / speed, s, b).getRGB();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void hexColor(int hexColor) {
/* 1996 */     float red = (hexColor >> 16 & 0xFF) / 255.0F;
/* 1997 */     float green = (hexColor >> 8 & 0xFF) / 255.0F;
/* 1998 */     float blue = (hexColor & 0xFF) / 255.0F;
/* 1999 */     float alpha = (hexColor >> 24 & 0xFF) / 255.0F;
/* 2000 */     GL11.glColor4f(red, green, blue, alpha);
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isInViewFrustrum(Entity entity) {
/* 2005 */     return (isInViewFrustrum(entity.func_174813_aQ()) || entity.field_70158_ak);
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isInViewFrustrum(AxisAlignedBB bb) {
/* 2010 */     Entity current = Minecraft.func_71410_x().func_175606_aa();
/* 2011 */     frustrum.func_78547_a(((Entity)Objects.requireNonNull((T)current)).field_70165_t, current.field_70163_u, current.field_70161_v);
/* 2012 */     return frustrum.func_78546_a(bb);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void drawRoundedRectangle(float x, float y, float width, float height, float radius) {
/* 2017 */     GL11.glEnable(3042);
/* 2018 */     drawArc(x + width - radius, y + height - radius, radius, 0.0F, 90.0F, 16);
/* 2019 */     drawArc(x + radius, y + height - radius, radius, 90.0F, 180.0F, 16);
/* 2020 */     drawArc(x + radius, y + radius, radius, 180.0F, 270.0F, 16);
/* 2021 */     drawArc(x + width - radius, y + radius, radius, 270.0F, 360.0F, 16);
/* 2022 */     GL11.glBegin(4);
/* 2023 */     GL11.glVertex2d((x + width - radius), y);
/* 2024 */     GL11.glVertex2d((x + radius), y);
/* 2025 */     GL11.glVertex2d((x + width - radius), (y + radius));
/* 2026 */     GL11.glVertex2d((x + width - radius), (y + radius));
/* 2027 */     GL11.glVertex2d((x + radius), y);
/* 2028 */     GL11.glVertex2d((x + radius), (y + radius));
/* 2029 */     GL11.glVertex2d((x + width), (y + radius));
/* 2030 */     GL11.glVertex2d(x, (y + radius));
/* 2031 */     GL11.glVertex2d(x, (y + height - radius));
/* 2032 */     GL11.glVertex2d((x + width), (y + radius));
/* 2033 */     GL11.glVertex2d(x, (y + height - radius));
/* 2034 */     GL11.glVertex2d((x + width), (y + height - radius));
/* 2035 */     GL11.glVertex2d((x + width - radius), (y + height - radius));
/* 2036 */     GL11.glVertex2d((x + radius), (y + height - radius));
/* 2037 */     GL11.glVertex2d((x + width - radius), (y + height));
/* 2038 */     GL11.glVertex2d((x + width - radius), (y + height));
/* 2039 */     GL11.glVertex2d((x + radius), (y + height - radius));
/* 2040 */     GL11.glVertex2d((x + radius), (y + height));
/* 2041 */     glEnd();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void renderOne(float lineWidth) {
/* 2046 */     checkSetupFBO();
/* 2047 */     GL11.glPushAttrib(1048575);
/* 2048 */     GL11.glDisable(3008);
/* 2049 */     GL11.glDisable(3553);
/* 2050 */     GL11.glDisable(2896);
/* 2051 */     GL11.glEnable(3042);
/* 2052 */     GL11.glBlendFunc(770, 771);
/* 2053 */     GL11.glLineWidth(lineWidth);
/* 2054 */     GL11.glEnable(2848);
/* 2055 */     GL11.glEnable(2960);
/* 2056 */     GL11.glClear(1024);
/* 2057 */     GL11.glClearStencil(15);
/* 2058 */     GL11.glStencilFunc(512, 1, 15);
/* 2059 */     GL11.glStencilOp(7681, 7681, 7681);
/* 2060 */     GL11.glPolygonMode(1032, 6913);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void renderTwo() {
/* 2065 */     GL11.glStencilFunc(512, 0, 15);
/* 2066 */     GL11.glStencilOp(7681, 7681, 7681);
/* 2067 */     GL11.glPolygonMode(1032, 6914);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void renderThree() {
/* 2072 */     GL11.glStencilFunc(514, 1, 15);
/* 2073 */     GL11.glStencilOp(7680, 7680, 7680);
/* 2074 */     GL11.glPolygonMode(1032, 6913);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void renderFour(Color color) {
/* 2079 */     setColor(color);
/* 2080 */     GL11.glDepthMask(false);
/* 2081 */     GL11.glDisable(2929);
/* 2082 */     GL11.glEnable(10754);
/* 2083 */     GL11.glPolygonOffset(1.0F, -2000000.0F);
/* 2084 */     OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void renderFive() {
/* 2089 */     GL11.glPolygonOffset(1.0F, 2000000.0F);
/* 2090 */     GL11.glDisable(10754);
/* 2091 */     GL11.glEnable(2929);
/* 2092 */     GL11.glDepthMask(true);
/* 2093 */     GL11.glDisable(2960);
/* 2094 */     GL11.glDisable(2848);
/* 2095 */     GL11.glHint(3154, 4352);
/* 2096 */     GL11.glEnable(3042);
/* 2097 */     GL11.glEnable(2896);
/* 2098 */     GL11.glEnable(3553);
/* 2099 */     GL11.glEnable(3008);
/* 2100 */     GL11.glPopAttrib();
/*      */   }
/*      */ 
/*      */   
/*      */   public static void rotationHelper(float xAngle, float yAngle, float zAngle) {
/* 2105 */     GlStateManager.func_179114_b(yAngle, 0.0F, 1.0F, 0.0F);
/* 2106 */     GlStateManager.func_179114_b(zAngle, 0.0F, 0.0F, 1.0F);
/* 2107 */     GlStateManager.func_179114_b(xAngle, 1.0F, 0.0F, 0.0F);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void setColor(Color color) {
/* 2112 */     GL11.glColor4d(color.getRed() / 255.0D, color.getGreen() / 255.0D, color.getBlue() / 255.0D, color.getAlpha() / 255.0D);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void checkSetupFBO() {
/* 2117 */     Framebuffer fbo = mc.field_147124_at;
/* 2118 */     if (fbo != null && fbo.field_147624_h > -1) {
/* 2119 */       setupFBO(fbo);
/* 2120 */       fbo.field_147624_h = -1;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static void setupFBO(Framebuffer fbo) {
/* 2126 */     EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.field_147624_h);
/* 2127 */     int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
/* 2128 */     EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferID);
/* 2129 */     EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.field_71443_c, mc.field_71440_d);
/* 2130 */     EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferID);
/* 2131 */     EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferID);
/*      */   }
/*      */   
/*      */   public static class RenderTesselator
/*      */     extends Tessellator
/*      */   {
/* 2137 */     public static RenderTesselator INSTANCE = new RenderTesselator();
/*      */ 
/*      */     
/*      */     public RenderTesselator() {
/* 2141 */       super(2097152);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void prepare(int mode) {
/* 2146 */       prepareGL();
/* 2147 */       begin(mode);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void prepareGL() {
/* 2152 */       GL11.glBlendFunc(770, 771);
/* 2153 */       GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
/* 2154 */       GlStateManager.func_187441_d(1.5F);
/* 2155 */       GlStateManager.func_179090_x();
/* 2156 */       GlStateManager.func_179132_a(false);
/* 2157 */       GlStateManager.func_179147_l();
/* 2158 */       GlStateManager.func_179097_i();
/* 2159 */       GlStateManager.func_179140_f();
/* 2160 */       GlStateManager.func_179129_p();
/* 2161 */       GlStateManager.func_179141_d();
/* 2162 */       GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void begin(int mode) {
/* 2167 */       INSTANCE.func_178180_c().func_181668_a(mode, DefaultVertexFormats.field_181706_f);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void release() {
/* 2172 */       render();
/* 2173 */       releaseGL();
/*      */     }
/*      */ 
/*      */     
/*      */     public static void render() {
/* 2178 */       INSTANCE.func_78381_a();
/*      */     }
/*      */ 
/*      */     
/*      */     public static void releaseGL() {
/* 2183 */       GlStateManager.func_179089_o();
/* 2184 */       GlStateManager.func_179132_a(true);
/* 2185 */       GlStateManager.func_179098_w();
/* 2186 */       GlStateManager.func_179147_l();
/* 2187 */       GlStateManager.func_179126_j();
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawBox(BlockPos blockPos, int argb, int sides) {
/* 2192 */       int a = argb >>> 24 & 0xFF;
/* 2193 */       int r = argb >>> 16 & 0xFF;
/* 2194 */       int g = argb >>> 8 & 0xFF;
/* 2195 */       int b = argb & 0xFF;
/* 2196 */       drawBox(blockPos, r, g, b, a, sides);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawBox(float x, float y, float z, int argb, int sides) {
/* 2201 */       int a = argb >>> 24 & 0xFF;
/* 2202 */       int r = argb >>> 16 & 0xFF;
/* 2203 */       int g = argb >>> 8 & 0xFF;
/* 2204 */       int b = argb & 0xFF;
/* 2205 */       drawBox(INSTANCE.func_178180_c(), x, y, z, 1.0F, 1.0F, 1.0F, r, g, b, a, sides);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
/* 2210 */       drawBox(INSTANCE.func_178180_c(), blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), 1.0F, 1.0F, 1.0F, r, g, b, a, sides);
/*      */     }
/*      */ 
/*      */     
/*      */     public static BufferBuilder getBufferBuilder() {
/* 2215 */       return INSTANCE.func_178180_c();
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
/* 2220 */       if ((sides & 0x1) != 0) {
/* 2221 */         buffer.func_181662_b((x + w), y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2222 */         buffer.func_181662_b((x + w), y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2223 */         buffer.func_181662_b(x, y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2224 */         buffer.func_181662_b(x, y, z).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2226 */       if ((sides & 0x2) != 0) {
/* 2227 */         buffer.func_181662_b((x + w), (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/* 2228 */         buffer.func_181662_b(x, (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/* 2229 */         buffer.func_181662_b(x, (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2230 */         buffer.func_181662_b((x + w), (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2232 */       if ((sides & 0x4) != 0) {
/* 2233 */         buffer.func_181662_b((x + w), y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2234 */         buffer.func_181662_b(x, y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2235 */         buffer.func_181662_b(x, (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/* 2236 */         buffer.func_181662_b((x + w), (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2238 */       if ((sides & 0x8) != 0) {
/* 2239 */         buffer.func_181662_b(x, y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2240 */         buffer.func_181662_b((x + w), y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2241 */         buffer.func_181662_b((x + w), (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2242 */         buffer.func_181662_b(x, (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2244 */       if ((sides & 0x10) != 0) {
/* 2245 */         buffer.func_181662_b(x, y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2246 */         buffer.func_181662_b(x, y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2247 */         buffer.func_181662_b(x, (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2248 */         buffer.func_181662_b(x, (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2250 */       if ((sides & 0x20) != 0) {
/* 2251 */         buffer.func_181662_b((x + w), y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2252 */         buffer.func_181662_b((x + w), y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2253 */         buffer.func_181662_b((x + w), (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/* 2254 */         buffer.func_181662_b((x + w), (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawLines(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
/* 2260 */       if ((sides & 0x11) != 0) {
/* 2261 */         buffer.func_181662_b(x, y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2262 */         buffer.func_181662_b(x, y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2264 */       if ((sides & 0x12) != 0) {
/* 2265 */         buffer.func_181662_b(x, (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/* 2266 */         buffer.func_181662_b(x, (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2268 */       if ((sides & 0x21) != 0) {
/* 2269 */         buffer.func_181662_b((x + w), y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2270 */         buffer.func_181662_b((x + w), y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2272 */       if ((sides & 0x22) != 0) {
/* 2273 */         buffer.func_181662_b((x + w), (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/* 2274 */         buffer.func_181662_b((x + w), (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2276 */       if ((sides & 0x5) != 0) {
/* 2277 */         buffer.func_181662_b(x, y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2278 */         buffer.func_181662_b((x + w), y, z).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2280 */       if ((sides & 0x6) != 0) {
/* 2281 */         buffer.func_181662_b(x, (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/* 2282 */         buffer.func_181662_b((x + w), (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2284 */       if ((sides & 0x9) != 0) {
/* 2285 */         buffer.func_181662_b(x, y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2286 */         buffer.func_181662_b((x + w), y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2288 */       if ((sides & 0xA) != 0) {
/* 2289 */         buffer.func_181662_b(x, (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2290 */         buffer.func_181662_b((x + w), (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2292 */       if ((sides & 0x14) != 0) {
/* 2293 */         buffer.func_181662_b(x, y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2294 */         buffer.func_181662_b(x, (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2296 */       if ((sides & 0x24) != 0) {
/* 2297 */         buffer.func_181662_b((x + w), y, z).func_181669_b(r, g, b, a).func_181675_d();
/* 2298 */         buffer.func_181662_b((x + w), (y + h), z).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2300 */       if ((sides & 0x18) != 0) {
/* 2301 */         buffer.func_181662_b(x, y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2302 */         buffer.func_181662_b(x, (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/* 2304 */       if ((sides & 0x28) != 0) {
/* 2305 */         buffer.func_181662_b((x + w), y, (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/* 2306 */         buffer.func_181662_b((x + w), (y + h), (z + d)).func_181669_b(r, g, b, a).func_181675_d();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
/* 2312 */       GlStateManager.func_179094_E();
/* 2313 */       GlStateManager.func_179147_l();
/* 2314 */       GlStateManager.func_179097_i();
/* 2315 */       GlStateManager.func_179120_a(770, 771, 0, 1);
/* 2316 */       GlStateManager.func_179090_x();
/* 2317 */       GlStateManager.func_179132_a(false);
/* 2318 */       GL11.glEnable(2848);
/* 2319 */       GL11.glHint(3154, 4354);
/* 2320 */       GL11.glLineWidth(width);
/* 2321 */       Tessellator tessellator = Tessellator.func_178181_a();
/* 2322 */       BufferBuilder bufferbuilder = tessellator.func_178180_c();
/* 2323 */       bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
/* 2324 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2325 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2326 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2327 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2328 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2329 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2330 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2331 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2332 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2333 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2334 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2335 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2336 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2337 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2338 */       bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2339 */       bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
/* 2340 */       tessellator.func_78381_a();
/* 2341 */       GL11.glDisable(2848);
/* 2342 */       GlStateManager.func_179132_a(true);
/* 2343 */       GlStateManager.func_179126_j();
/* 2344 */       GlStateManager.func_179098_w();
/* 2345 */       GlStateManager.func_179084_k();
/* 2346 */       GlStateManager.func_179121_F();
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int argb, int alpha2) {
/* 2351 */       int a = argb >>> 24 & 0xFF;
/* 2352 */       int r = argb >>> 16 & 0xFF;
/* 2353 */       int g = argb >>> 8 & 0xFF;
/* 2354 */       int b = argb & 0xFF;
/* 2355 */       drawFullBox(bb, blockPos, width, r, g, b, a, alpha2);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha, int alpha2) {
/* 2360 */       prepare(7);
/* 2361 */       drawBox(blockPos, red, green, blue, alpha, 63);
/* 2362 */       release();
/* 2363 */       drawBoundingBox(bb, width, red, green, blue, alpha2);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawHalfBox(BlockPos blockPos, int argb, int sides) {
/* 2368 */       int a = argb >>> 24 & 0xFF;
/* 2369 */       int r = argb >>> 16 & 0xFF;
/* 2370 */       int g = argb >>> 8 & 0xFF;
/* 2371 */       int b = argb & 0xFF;
/* 2372 */       drawHalfBox(blockPos, r, g, b, a, sides);
/*      */     }
/*      */ 
/*      */     
/*      */     public static void drawHalfBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
/* 2377 */       drawBox(INSTANCE.func_178180_c(), blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), 1.0F, 0.5F, 1.0F, r, g, b, a, sides);
/*      */     }
/*      */   }
/*      */   
/*      */   public static final class GeometryMasks
/*      */   {
/* 2383 */     public static final HashMap<EnumFacing, Integer> FACEMAP = new HashMap<>();
/*      */     
/*      */     static {
/* 2386 */       FACEMAP.put(EnumFacing.DOWN, Integer.valueOf(1));
/* 2387 */       FACEMAP.put(EnumFacing.WEST, Integer.valueOf(16));
/* 2388 */       FACEMAP.put(EnumFacing.NORTH, Integer.valueOf(4));
/* 2389 */       FACEMAP.put(EnumFacing.SOUTH, Integer.valueOf(8));
/* 2390 */       FACEMAP.put(EnumFacing.EAST, Integer.valueOf(32));
/* 2391 */       FACEMAP.put(EnumFacing.UP, Integer.valueOf(2));
/*      */     }
/*      */     
/*      */     public static final class Line {
/*      */       public static final int DOWN_WEST = 17;
/*      */       public static final int UP_WEST = 18;
/*      */       public static final int DOWN_EAST = 33;
/*      */       public static final int UP_EAST = 34;
/*      */       public static final int DOWN_NORTH = 5;
/*      */       public static final int UP_NORTH = 6;
/*      */       public static final int DOWN_SOUTH = 9;
/*      */       public static final int UP_SOUTH = 10;
/*      */       public static final int NORTH_WEST = 20;
/*      */       public static final int NORTH_EAST = 36;
/*      */       public static final int SOUTH_WEST = 24;
/*      */       public static final int SOUTH_EAST = 40;
/*      */       public static final int ALL = 63;
/*      */     }
/*      */     
/*      */     public static final class Quad {
/*      */       public static final int DOWN = 1;
/*      */       public static final int UP = 2;
/*      */       public static final int NORTH = 4;
/*      */       public static final int SOUTH = 8;
/*      */       public static final int WEST = 16;
/*      */       public static final int EAST = 32;
/*      */       public static final int ALL = 63;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\RenderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */