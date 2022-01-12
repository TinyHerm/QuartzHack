/*    */ package me.mohalk.banzem.manager;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import me.mohalk.banzem.features.Feature;
/*    */ import me.mohalk.banzem.features.modules.client.Managers;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.util.math.MathHelper;
/*    */ 
/*    */ public class SpeedManager
/*    */   extends Feature {
/*    */   public static final double LAST_JUMP_INFO_DURATION_DEFAULT = 3.0D;
/*    */   public static boolean didJumpThisTick = false;
/*    */   public static boolean isJumping = false;
/* 16 */   public double firstJumpSpeed = 0.0D;
/* 17 */   public double lastJumpSpeed = 0.0D;
/* 18 */   public double percentJumpSpeedChanged = 0.0D;
/* 19 */   public double jumpSpeedChanged = 0.0D;
/*    */   public boolean didJumpLastTick = false;
/* 21 */   public long jumpInfoStartTime = 0L;
/*    */   public boolean wasFirstJump = true;
/* 23 */   public double speedometerCurrentSpeed = 0.0D;
/* 24 */   public HashMap<EntityPlayer, Double> playerSpeeds = new HashMap<>();
/* 25 */   private final int distancer = 20;
/*    */   
/*    */   public static void setDidJumpThisTick(boolean val) {
/* 28 */     didJumpThisTick = val;
/*    */   }
/*    */   
/*    */   public static void setIsJumping(boolean val) {
/* 32 */     isJumping = val;
/*    */   }
/*    */   
/*    */   public float lastJumpInfoTimeRemaining() {
/* 36 */     return (float)(Minecraft.func_71386_F() - this.jumpInfoStartTime) / 1000.0F;
/*    */   }
/*    */   
/*    */   public void updateValues() {
/* 40 */     double distTraveledLastTickX = mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q;
/* 41 */     double distTraveledLastTickZ = mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s;
/* 42 */     this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
/* 43 */     if (didJumpThisTick && (!mc.field_71439_g.field_70122_E || isJumping)) {
/* 44 */       if (didJumpThisTick && !this.didJumpLastTick) {
/* 45 */         this.wasFirstJump = (this.lastJumpSpeed == 0.0D);
/* 46 */         this.percentJumpSpeedChanged = (this.speedometerCurrentSpeed != 0.0D) ? (this.speedometerCurrentSpeed / this.lastJumpSpeed - 1.0D) : -1.0D;
/* 47 */         this.jumpSpeedChanged = this.speedometerCurrentSpeed - this.lastJumpSpeed;
/* 48 */         this.jumpInfoStartTime = Minecraft.func_71386_F();
/* 49 */         this.lastJumpSpeed = this.speedometerCurrentSpeed;
/* 50 */         this.firstJumpSpeed = this.wasFirstJump ? this.lastJumpSpeed : 0.0D;
/*    */       } 
/* 52 */       this.didJumpLastTick = didJumpThisTick;
/*    */     } else {
/* 54 */       this.didJumpLastTick = false;
/* 55 */       this.lastJumpSpeed = 0.0D;
/*    */     } 
/* 57 */     if (((Boolean)(Managers.getInstance()).speed.getValue()).booleanValue()) {
/* 58 */       updatePlayers();
/*    */     }
/*    */   }
/*    */   
/*    */   public void updatePlayers() {
/* 63 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 64 */       getClass(); getClass(); if (mc.field_71439_g.func_70068_e((Entity)player) >= (20 * 20))
/*    */         continue; 
/* 66 */       double distTraveledLastTickX = player.field_70165_t - player.field_70169_q;
/* 67 */       double distTraveledLastTickZ = player.field_70161_v - player.field_70166_s;
/* 68 */       double playerSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
/* 69 */       this.playerSpeeds.put(player, Double.valueOf(playerSpeed));
/*    */     } 
/*    */   }
/*    */   
/*    */   public double getPlayerSpeed(EntityPlayer player) {
/* 74 */     if (this.playerSpeeds.get(player) == null) {
/* 75 */       return 0.0D;
/*    */     }
/* 77 */     return turnIntoKpH(((Double)this.playerSpeeds.get(player)).doubleValue());
/*    */   }
/*    */   
/*    */   public double turnIntoKpH(double input) {
/* 81 */     return MathHelper.func_76133_a(input) * 71.2729367892D;
/*    */   }
/*    */   
/*    */   public double getSpeedKpH() {
/* 85 */     double speedometerkphdouble = turnIntoKpH(this.speedometerCurrentSpeed);
/* 86 */     speedometerkphdouble = Math.round(10.0D * speedometerkphdouble) / 10.0D;
/* 87 */     return speedometerkphdouble;
/*    */   }
/*    */   
/*    */   public double getSpeedMpS() {
/* 91 */     double speedometerMpsdouble = turnIntoKpH(this.speedometerCurrentSpeed) / 3.6D;
/* 92 */     speedometerMpsdouble = Math.round(10.0D * speedometerMpsdouble) / 10.0D;
/* 93 */     return speedometerMpsdouble;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\SpeedManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */