/*     */ package me.mohalk.banzem.mixin.mixins;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import me.mohalk.banzem.features.modules.client.Colors;
/*     */ import me.mohalk.banzem.features.modules.render.Chams;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import net.minecraft.client.model.ModelBase;
/*     */ import net.minecraft.client.renderer.entity.Render;
/*     */ import net.minecraft.client.renderer.entity.RenderLivingBase;
/*     */ import net.minecraft.client.renderer.entity.RenderManager;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ import org.spongepowered.asm.mixin.Mixin;
/*     */ import org.spongepowered.asm.mixin.injection.At;
/*     */ import org.spongepowered.asm.mixin.injection.Inject;
/*     */ import org.spongepowered.asm.mixin.injection.Redirect;
/*     */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*     */ 
/*     */ 
/*     */ @Mixin({RenderLivingBase.class})
/*     */ public abstract class MixinRenderLivingBase<T extends EntityLivingBase>
/*     */   extends Render<T>
/*     */ {
/*  27 */   private static final ResourceLocation glint = new ResourceLocation("eralp232/chams.png");
/*     */   
/*     */   public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
/*  30 */     super(renderManagerIn);
/*     */   }
/*     */ 
/*     */   
/*     */   @Redirect(method = {"renderModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
/*     */   private void renderModelHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
/*  36 */     boolean cancel = false;
/*  37 */     if (Chams.getInstance().isEnabled() && entityIn instanceof net.minecraft.entity.player.EntityPlayer && ((Boolean)(Chams.getInstance()).colored.getValue()).booleanValue() && !((Boolean)(Chams.getInstance()).textured.getValue()).booleanValue()) {
/*  38 */       if (!((Boolean)(Chams.getInstance()).textured.getValue()).booleanValue()) {
/*  39 */         GL11.glPushAttrib(1048575);
/*  40 */         GL11.glDisable(3008);
/*  41 */         GL11.glDisable(3553);
/*  42 */         GL11.glDisable(2896);
/*  43 */         GL11.glEnable(3042);
/*  44 */         GL11.glBlendFunc(770, 771);
/*  45 */         GL11.glLineWidth(1.5F);
/*  46 */         GL11.glEnable(2960);
/*  47 */         if (((Boolean)(Chams.getInstance()).rainbow.getValue()).booleanValue()) {
/*  48 */           Color rainbowColor1 = ((Boolean)(Chams.getInstance()).colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow(((Integer)(Chams.getInstance()).speed.getValue()).intValue() * 100, 0, ((Integer)(Chams.getInstance()).saturation.getValue()).intValue() / 100.0F, ((Integer)(Chams.getInstance()).brightness.getValue()).intValue() / 100.0F));
/*  49 */           Color rainbowColor = EntityUtil.getColor(entityIn, rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue(), ((Integer)(Chams.getInstance()).alpha.getValue()).intValue(), true);
/*  50 */           GL11.glDisable(2929);
/*  51 */           GL11.glDepthMask(false);
/*  52 */           GL11.glEnable(10754);
/*  53 */           GL11.glColor4f(rainbowColor.getRed() / 255.0F, rainbowColor.getGreen() / 255.0F, rainbowColor.getBlue() / 255.0F, ((Integer)(Chams.getInstance()).alpha.getValue()).intValue() / 255.0F);
/*  54 */           modelBase.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
/*  55 */           GL11.glEnable(2929);
/*  56 */           GL11.glDepthMask(true);
/*  57 */         } else if (((Boolean)(Chams.getInstance()).xqz.getValue()).booleanValue()) {
/*  58 */           Color hiddenColor = ((Boolean)(Chams.getInstance()).colorSync.getValue()).booleanValue() ? EntityUtil.getColor(entityIn, ((Integer)(Chams.getInstance()).hiddenRed.getValue()).intValue(), ((Integer)(Chams.getInstance()).hiddenGreen.getValue()).intValue(), ((Integer)(Chams.getInstance()).hiddenBlue.getValue()).intValue(), ((Integer)(Chams.getInstance()).hiddenAlpha.getValue()).intValue(), true) : EntityUtil.getColor(entityIn, ((Integer)(Chams.getInstance()).hiddenRed.getValue()).intValue(), ((Integer)(Chams.getInstance()).hiddenGreen.getValue()).intValue(), ((Integer)(Chams.getInstance()).hiddenBlue.getValue()).intValue(), ((Integer)(Chams.getInstance()).hiddenAlpha.getValue()).intValue(), true);
/*  59 */           Color visibleColor2 = ((Boolean)(Chams.getInstance()).colorSync.getValue()).booleanValue() ? EntityUtil.getColor(entityIn, ((Integer)(Chams.getInstance()).red.getValue()).intValue(), ((Integer)(Chams.getInstance()).green.getValue()).intValue(), ((Integer)(Chams.getInstance()).blue.getValue()).intValue(), ((Integer)(Chams.getInstance()).alpha.getValue()).intValue(), true) : EntityUtil.getColor(entityIn, ((Integer)(Chams.getInstance()).red.getValue()).intValue(), ((Integer)(Chams.getInstance()).green.getValue()).intValue(), ((Integer)(Chams.getInstance()).blue.getValue()).intValue(), ((Integer)(Chams.getInstance()).alpha.getValue()).intValue(), true);
/*  60 */           GL11.glDisable(2929);
/*  61 */           GL11.glDepthMask(false);
/*  62 */           GL11.glEnable(10754);
/*  63 */           GL11.glColor4f(hiddenColor.getRed() / 255.0F, hiddenColor.getGreen() / 255.0F, hiddenColor.getBlue() / 255.0F, ((Integer)(Chams.getInstance()).alpha.getValue()).intValue() / 255.0F);
/*  64 */           modelBase.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
/*  65 */           GL11.glEnable(2929);
/*  66 */           GL11.glDepthMask(true);
/*  67 */           GL11.glColor4f(visibleColor2.getRed() / 255.0F, visibleColor2.getGreen() / 255.0F, visibleColor2.getBlue() / 255.0F, ((Integer)(Chams.getInstance()).alpha.getValue()).intValue() / 255.0F);
/*  68 */           modelBase.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
/*     */         } else {
/*  70 */           Color visibleColor = ((Boolean)(Chams.getInstance()).colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : EntityUtil.getColor(entityIn, ((Integer)(Chams.getInstance()).red.getValue()).intValue(), ((Integer)(Chams.getInstance()).green.getValue()).intValue(), ((Integer)(Chams.getInstance()).blue.getValue()).intValue(), ((Integer)(Chams.getInstance()).alpha.getValue()).intValue(), true);
/*  71 */           GL11.glDisable(2929);
/*  72 */           GL11.glDepthMask(false);
/*  73 */           GL11.glEnable(10754);
/*  74 */           GL11.glColor4f(visibleColor.getRed() / 255.0F, visibleColor.getGreen() / 255.0F, visibleColor.getBlue() / 255.0F, ((Integer)(Chams.getInstance()).alpha.getValue()).intValue() / 255.0F);
/*  75 */           modelBase.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
/*  76 */           GL11.glEnable(2929);
/*  77 */           GL11.glDepthMask(true);
/*     */         } 
/*  79 */         GL11.glEnable(3042);
/*  80 */         GL11.glEnable(2896);
/*  81 */         GL11.glEnable(3553);
/*  82 */         GL11.glEnable(3008);
/*  83 */         GL11.glPopAttrib();
/*     */       } 
/*  85 */     } else if (((Boolean)(Chams.getInstance()).textured.getValue()).booleanValue()) {
/*  86 */       GL11.glDisable(2929);
/*  87 */       GL11.glDepthMask(false);
/*  88 */       Color visibleColor = ((Boolean)(Chams.getInstance()).colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : EntityUtil.getColor(entityIn, ((Integer)(Chams.getInstance()).red.getValue()).intValue(), ((Integer)(Chams.getInstance()).green.getValue()).intValue(), ((Integer)(Chams.getInstance()).blue.getValue()).intValue(), ((Integer)(Chams.getInstance()).alpha.getValue()).intValue(), true);
/*  89 */       GL11.glColor4f(visibleColor.getRed() / 255.0F, visibleColor.getGreen() / 255.0F, visibleColor.getBlue() / 255.0F, ((Integer)(Chams.getInstance()).alpha.getValue()).intValue() / 255.0F);
/*  90 */       modelBase.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
/*  91 */       GL11.glEnable(2929);
/*  92 */       GL11.glDepthMask(true);
/*  93 */     } else if (!cancel) {
/*  94 */       modelBase.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Inject(method = {"doRender"}, at = {@At("HEAD")})
/*     */   public void doRenderPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
/* 100 */     if (Chams.getInstance().isEnabled() && !((Boolean)(Chams.getInstance()).colored.getValue()).booleanValue() && entity != null) {
/* 101 */       GL11.glEnable(32823);
/* 102 */       GL11.glPolygonOffset(1.0F, -1100000.0F);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Inject(method = {"doRender"}, at = {@At("RETURN")})
/*     */   public void doRenderPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
/* 108 */     if (Chams.getInstance().isEnabled() && !((Boolean)(Chams.getInstance()).colored.getValue()).booleanValue() && entity != null) {
/* 109 */       GL11.glPolygonOffset(1.0F, 1000000.0F);
/* 110 */       GL11.glDisable(32823);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinRenderLivingBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */