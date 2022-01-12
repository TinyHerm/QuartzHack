/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.render.NoRender;
/*    */ import me.mohalk.banzem.features.modules.render.ViewModel;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.entity.AbstractClientPlayer;
/*    */ import net.minecraft.client.renderer.GlStateManager;
/*    */ import net.minecraft.client.renderer.ItemRenderer;
/*    */ import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
/*    */ import net.minecraft.entity.EntityLivingBase;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.util.EnumHand;
/*    */ import net.minecraft.util.EnumHandSide;
/*    */ import org.spongepowered.asm.mixin.Final;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({ItemRenderer.class})
/*    */ public abstract class MixinItemRenderer
/*    */ {
/*    */   @Shadow
/*    */   @Final
/*    */   public Minecraft field_78455_a;
/*    */   private boolean injection = true;
/*    */   
/*    */   @Shadow
/*    */   public abstract void func_187457_a(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, EnumHand paramEnumHand, float paramFloat3, ItemStack paramItemStack, float paramFloat4);
/*    */   
/*    */   @Shadow
/*    */   protected abstract void func_187456_a(float paramFloat1, float paramFloat2, EnumHandSide paramEnumHandSide);
/*    */   
/*    */   @Inject(method = {"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_1874572, float p_1874573, EnumHand hand, float p_1874575, ItemStack stack, float p_1874577, CallbackInfo info) {}
/*    */   
/*    */   @Inject(method = {"renderFireInFirstPerson"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void renderFireInFirstPersonHook(CallbackInfo info) {
/* 40 */     if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).fire.getValue()).booleanValue()) {
/* 41 */       info.cancel();
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"renderSuffocationOverlay"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void renderSuffocationOverlay(CallbackInfo ci) {
/* 47 */     if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).blocks.getValue()).booleanValue()) {
/* 48 */       ci.cancel();
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"renderItemSide"}, at = {@At("HEAD")})
/*    */   public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
/* 54 */     if (ViewModel.INSTANCE.isEnabled()) {
/* 55 */       GlStateManager.func_179152_a(((Integer)ViewModel.INSTANCE.scaleX.getValue()).intValue() / 100.0F, ((Integer)ViewModel.INSTANCE.scaleY.getValue()).intValue() / 100.0F, ((Integer)ViewModel.INSTANCE.scaleZ.getValue()).intValue() / 100.0F);
/* 56 */       if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
/* 57 */         GlStateManager.func_179109_b(((Integer)ViewModel.INSTANCE.translateX.getValue()).intValue() / 200.0F, ((Integer)ViewModel.INSTANCE.translateY.getValue()).intValue() / 200.0F, ((Integer)ViewModel.INSTANCE.translateZ.getValue()).intValue() / 200.0F);
/* 58 */         GlStateManager.func_179114_b(((Integer)ViewModel.INSTANCE.rotateX.getValue()).intValue(), 1.0F, 0.0F, 0.0F);
/* 59 */         GlStateManager.func_179114_b(((Integer)ViewModel.INSTANCE.rotateY.getValue()).intValue(), 0.0F, 1.0F, 0.0F);
/* 60 */         GlStateManager.func_179114_b(((Integer)ViewModel.INSTANCE.rotateZ.getValue()).intValue(), 0.0F, 0.0F, 1.0F);
/* 61 */       } else if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
/* 62 */         GlStateManager.func_179109_b(-((Integer)ViewModel.INSTANCE.translateX.getValue()).intValue() / 200.0F, ((Integer)ViewModel.INSTANCE.translateY.getValue()).intValue() / 200.0F, ((Integer)ViewModel.INSTANCE.translateZ.getValue()).intValue() / 200.0F);
/* 63 */         GlStateManager.func_179114_b(-((Integer)ViewModel.INSTANCE.rotateX.getValue()).intValue(), 1.0F, 0.0F, 0.0F);
/* 64 */         GlStateManager.func_179114_b(((Integer)ViewModel.INSTANCE.rotateY.getValue()).intValue(), 0.0F, 1.0F, 0.0F);
/* 65 */         GlStateManager.func_179114_b(((Integer)ViewModel.INSTANCE.rotateZ.getValue()).intValue(), 0.0F, 0.0F, 1.0F);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinItemRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */