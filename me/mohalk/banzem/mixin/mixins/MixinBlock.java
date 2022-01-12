/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.features.modules.render.XRay;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.state.IBlockState;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.world.World;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ 
/*    */ @Mixin({Block.class})
/*    */ public abstract class MixinBlock
/*    */ {
/*    */   @Shadow
/*    */   @Deprecated
/*    */   public abstract float func_176195_g(IBlockState paramIBlockState, World paramWorld, BlockPos paramBlockPos);
/*    */   
/*    */   @Inject(method = {"isFullCube"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void isFullCubeHook(IBlockState blockState, CallbackInfoReturnable<Boolean> info) {
/*    */     try {
/* 25 */       if (XRay.getInstance().isOn()) {
/* 26 */         info.setReturnValue(Boolean.valueOf(XRay.getInstance().shouldRender(Block.class.cast(this))));
/* 27 */         info.cancel();
/*    */       }
/*    */     
/* 30 */     } catch (Exception exception) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */