/*    */ package me.zero.alpine.listener;
/*    */ 
/*    */ import java.util.function.Predicate;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MethodRefListener<T>
/*    */   extends Listener<T>
/*    */ {
/*    */   private Class<T> target;
/*    */   
/*    */   @SafeVarargs
/*    */   public MethodRefListener(Class<T> target, EventHook<T> hook, Predicate<T>... filters) {
/* 20 */     super(hook, filters);
/* 21 */     this.target = target;
/*    */   }
/*    */   
/*    */   @SafeVarargs
/*    */   public MethodRefListener(Class<T> target, EventHook<T> hook, int priority, Predicate<T>... filters) {
/* 26 */     super(hook, priority, filters);
/* 27 */     this.target = target;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<T> getTarget() {
/* 32 */     return this.target;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\listener\MethodRefListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */