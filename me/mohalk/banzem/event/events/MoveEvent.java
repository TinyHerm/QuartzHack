/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ import net.minecraft.entity.MoverType;
/*    */ import net.minecraftforge.fml.common.eventhandler.Cancelable;
/*    */ 
/*    */ @Cancelable
/*    */ public class MoveEvent
/*    */   extends EventStage {
/*    */   private MoverType type;
/*    */   private double x;
/*    */   private double y;
/*    */   private double z;
/*    */   
/*    */   public MoveEvent(int stage, MoverType type, double x, double y, double z) {
/* 16 */     super(stage);
/* 17 */     this.type = type;
/* 18 */     this.x = x;
/* 19 */     this.y = y;
/* 20 */     this.z = z;
/*    */   }
/*    */   
/*    */   public MoverType getType() {
/* 24 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(MoverType type) {
/* 28 */     this.type = type;
/*    */   }
/*    */   
/*    */   public double getX() {
/* 32 */     return this.x;
/*    */   }
/*    */   
/*    */   public void setX(double x) {
/* 36 */     this.x = x;
/*    */   }
/*    */   
/*    */   public double getY() {
/* 40 */     return this.y;
/*    */   }
/*    */   
/*    */   public void setY(double y) {
/* 44 */     this.y = y;
/*    */   }
/*    */   
/*    */   public double getZ() {
/* 48 */     return this.z;
/*    */   }
/*    */   
/*    */   public void setZ(double z) {
/* 52 */     this.z = z;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\MoveEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */