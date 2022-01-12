/*     */ package me.zero.alpine.bus;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import me.zero.alpine.listener.Listenable;
/*     */ import me.zero.alpine.listener.Listener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface EventBus
/*     */ {
/*     */   void subscribe(Listenable paramListenable);
/*     */   
/*     */   void subscribe(Listener paramListener);
/*     */   
/*     */   void subscribeAll(Listenable... listenables) {
/*  47 */     Arrays.<Listenable>stream(listenables).forEach(this::subscribe);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void subscribeAll(Iterable<Listenable> listenables) {
/*  59 */     listenables.forEach(this::subscribe);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void subscribeAll(Listener... listeners) {
/*  71 */     Arrays.<Listener>stream(listeners).forEach(this::subscribe);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void unsubscribe(Listenable paramListenable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void unsubscribe(Listener paramListener);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void unsubscribeAll(Listenable... listenables) {
/* 101 */     Arrays.<Listenable>stream(listenables).forEach(this::unsubscribe);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void unsubscribeAll(Iterable<Listenable> listenables) {
/* 113 */     listenables.forEach(this::unsubscribe);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void unsubscribeAll(Listener... listeners) {
/* 125 */     Arrays.<Listener>stream(listeners).forEach(this::unsubscribe);
/*     */   }
/*     */   
/*     */   void post(Object paramObject);
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\bus\EventBus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */