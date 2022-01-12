/*    */ package me.zero.alpine.event.type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Cancellable
/*    */   implements ICancellable
/*    */ {
/*    */   private boolean cancelled;
/*    */   
/*    */   public void cancel() {
/* 18 */     this.cancelled = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCancelled() {
/* 23 */     return this.cancelled;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\event\type\Cancellable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */