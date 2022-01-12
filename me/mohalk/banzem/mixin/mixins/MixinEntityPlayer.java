/*    */ package me.mohalk.banzem.mixin.mixins;
/*    */ 
/*    */ import com.mojang.authlib.GameProfile;
/*    */ import net.minecraft.entity.EntityLivingBase;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.world.World;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ 
/*    */ @Mixin({EntityPlayer.class})
/*    */ public abstract class MixinEntityPlayer
/*    */   extends EntityLivingBase {
/*    */   public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
/* 13 */     super(worldIn);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\MixinEntityPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */