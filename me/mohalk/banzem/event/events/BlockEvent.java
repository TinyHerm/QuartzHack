/*    */ package me.mohalk.banzem.event.events;
/*    */ 
/*    */ import me.mohalk.banzem.event.EventStage;
/*    */ import net.minecraft.util.EnumFacing;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraftforge.fml.common.eventhandler.Cancelable;
/*    */ 
/*    */ @Cancelable
/*    */ public class BlockEvent
/*    */   extends EventStage {
/*    */   public BlockPos pos;
/*    */   public EnumFacing facing;
/*    */   
/*    */   public BlockEvent(int stage, BlockPos pos, EnumFacing facing) {
/* 15 */     super(stage);
/* 16 */     this.pos = pos;
/* 17 */     this.facing = facing;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\event\events\BlockEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */