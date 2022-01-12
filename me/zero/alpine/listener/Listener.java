/*    */ package me.zero.alpine.listener;
/*    */ 
/*    */ import java.util.function.Predicate;
/*    */ import net.jodah.typetools.TypeResolver;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Listener<T>
/*    */   implements EventHook<T>
/*    */ {
/*    */   private final Class<T> target;
/*    */   private final EventHook<T> hook;
/*    */   private final Predicate<T>[] filters;
/*    */   private final int priority;
/*    */   
/*    */   @SafeVarargs
/*    */   public Listener(EventHook<T> hook, Predicate<T>... filters) {
/* 44 */     this(hook, 0, filters);
/*    */   }
/*    */ 
/*    */   
/*    */   @SafeVarargs
/*    */   public Listener(EventHook<T> hook, int priority, Predicate<T>... filters) {
/* 50 */     this.hook = hook;
/* 51 */     this.priority = priority;
/* 52 */     this.target = TypeResolver.resolveRawArgument(EventHook.class, hook.getClass());
/* 53 */     this.filters = filters;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<T> getTarget() {
/* 63 */     return this.target;
/*    */   }
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
/*    */   public int getPriority() {
/* 76 */     return this.priority;
/*    */   }
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
/*    */   
/*    */   public void invoke(T event) {
/* 90 */     if (this.filters.length > 0) {
/* 91 */       for (Predicate<T> filter : this.filters) {
/* 92 */         if (!filter.test(event)) {
/*    */           return;
/*    */         }
/*    */       } 
/*    */     }
/* 97 */     this.hook.invoke(event);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\listener\Listener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */