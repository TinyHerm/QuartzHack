/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.event.events.JesusEvent;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.BlockLiquid;
/*    */ import net.minecraft.block.material.Material;
/*    */ import net.minecraft.block.state.IBlockState;
/*    */ import net.minecraft.util.math.AxisAlignedBB;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.world.IBlockAccess;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.Event;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({BlockLiquid.class})
/*    */ public class MixinBlockLiquid
/*    */   extends Block
/*    */ {
/*    */   protected MixinBlockLiquid(Material materialIn) {
/* 23 */     super(materialIn);
/*    */   }
/*    */   
/*    */   @Inject(method = {"getCollisionBoundingBox"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void getCollisionBoundingBoxHook(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> info) {
/* 28 */     JesusEvent event = new JesusEvent(0, pos);
/* 29 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 30 */     if (event.isCanceled())
/* 31 */       info.setReturnValue(event.getBoundingBox()); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinBlockLiquid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */