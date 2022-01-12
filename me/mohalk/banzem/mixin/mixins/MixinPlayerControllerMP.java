/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import me.mohalk.banzem.event.events.BlockEvent;
/*    */ import me.mohalk.banzem.event.events.ProcessRightClickBlockEvent;
/*    */ import me.mohalk.banzem.features.modules.player.Speedmine;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.material.Material;
/*    */ import net.minecraft.block.state.IBlockState;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.entity.EntityPlayerSP;
/*    */ import net.minecraft.client.multiplayer.PlayerControllerMP;
/*    */ import net.minecraft.client.multiplayer.WorldClient;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.init.Blocks;
/*    */ import net.minecraft.item.ItemBlock;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.util.EnumActionResult;
/*    */ import net.minecraft.util.EnumFacing;
/*    */ import net.minecraft.util.EnumHand;
/*    */ import net.minecraft.util.math.AxisAlignedBB;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.math.Vec3d;
/*    */ import net.minecraft.world.IBlockAccess;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.Event;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.Redirect;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ 
/*    */ @Mixin({PlayerControllerMP.class})
/*    */ public class MixinPlayerControllerMP
/*    */ {
/*    */   @Inject(method = {"resetBlockRemoving"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void resetBlockRemovingHook(CallbackInfo info) {
/* 40 */     if (Speedmine.getInstance().isOn() && ((Boolean)(Speedmine.getInstance()).reset.getValue()).booleanValue()) {
/* 41 */       info.cancel();
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"clickBlock"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
/* 47 */     BlockEvent event = new BlockEvent(3, pos, face);
/* 48 */     MinecraftForge.EVENT_BUS.post((Event)event);
/*    */   }
/*    */   
/*    */   @Inject(method = {"onPlayerDamageBlock"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
/* 53 */     BlockEvent event = new BlockEvent(4, pos, face);
/* 54 */     MinecraftForge.EVENT_BUS.post((Event)event);
/*    */   }
/*    */   
/*    */   @Redirect(method = {"processRightClickBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemBlock;canPlaceBlockOnSide(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z"))
/*    */   public boolean canPlaceBlockOnSideHook(ItemBlock itemBlock, World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
/* 59 */     Block block = worldIn.func_180495_p(pos).func_177230_c();
/* 60 */     if (block == Blocks.field_150431_aC && block.func_176200_f((IBlockAccess)worldIn, pos)) {
/* 61 */       side = EnumFacing.UP;
/* 62 */     } else if (!block.func_176200_f((IBlockAccess)worldIn, pos)) {
/* 63 */       pos = pos.func_177972_a(side);
/*    */     } 
/* 65 */     IBlockState iblockstate1 = worldIn.func_180495_p(pos);
/* 66 */     AxisAlignedBB axisalignedbb = itemBlock.field_150939_a.func_176223_P().func_185890_d((IBlockAccess)worldIn, pos);
/* 67 */     if (iblockstate1.func_185904_a() == Material.field_151594_q && itemBlock.field_150939_a == Blocks.field_150467_bQ) {
/* 68 */       return true;
/*    */     }
/* 70 */     return (iblockstate1.func_177230_c().func_176200_f((IBlockAccess)worldIn, pos) && itemBlock.field_150939_a.func_176198_a(worldIn, pos, side));
/*    */   }
/*    */   
/*    */   @Inject(method = {"processRightClickBlock"}, at = {@At("HEAD")}, cancellable = true)
/*    */   public void processRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir) {
/* 75 */     ProcessRightClickBlockEvent event = new ProcessRightClickBlockEvent(pos, hand, (Minecraft.func_71410_x()).field_71439_g.func_184586_b(hand));
/* 76 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 77 */     if (event.isCanceled())
/* 78 */       cir.cancel(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinPlayerControllerMP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */