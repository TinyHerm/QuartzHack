/*    */ package me.zero.alpine.bus.type;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import me.zero.alpine.bus.EventBus;
/*    */ import me.zero.alpine.bus.EventManager;
/*    */ import me.zero.alpine.listener.Listenable;
/*    */ import me.zero.alpine.listener.Listener;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AttachableEventManager
/*    */   extends EventManager
/*    */   implements AttachableEventBus
/*    */ {
/* 22 */   private final List<EventBus> attached = new ArrayList<>();
/*    */ 
/*    */   
/*    */   public void subscribe(Listenable listenable) {
/* 26 */     super.subscribe(listenable);
/*    */     
/* 28 */     if (!this.attached.isEmpty()) {
/* 29 */       this.attached.forEach(bus -> bus.subscribe(listenable));
/*    */     }
/*    */   }
/*    */   
/*    */   public void subscribe(Listener listener) {
/* 34 */     super.subscribe(listener);
/*    */     
/* 36 */     if (!this.attached.isEmpty()) {
/* 37 */       this.attached.forEach(bus -> bus.subscribe(listener));
/*    */     }
/*    */   }
/*    */   
/*    */   public void unsubscribe(Listenable listenable) {
/* 42 */     super.unsubscribe(listenable);
/*    */     
/* 44 */     if (!this.attached.isEmpty()) {
/* 45 */       this.attached.forEach(bus -> bus.unsubscribe(listenable));
/*    */     }
/*    */   }
/*    */   
/*    */   public void unsubscribe(Listener listener) {
/* 50 */     super.unsubscribe(listener);
/*    */     
/* 52 */     if (!this.attached.isEmpty()) {
/* 53 */       this.attached.forEach(bus -> bus.unsubscribe(listener));
/*    */     }
/*    */   }
/*    */   
/*    */   public void post(Object event) {
/* 58 */     super.post(event);
/*    */     
/* 60 */     if (!this.attached.isEmpty()) {
/* 61 */       this.attached.forEach(bus -> bus.post(event));
/*    */     }
/*    */   }
/*    */   
/*    */   public void attach(EventBus bus) {
/* 66 */     if (!this.attached.contains(bus)) {
/* 67 */       this.attached.add(bus);
/*    */     }
/*    */   }
/*    */   
/*    */   public void detach(EventBus bus) {
/* 72 */     this.attached.remove(bus);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\bus\type\AttachableEventManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */