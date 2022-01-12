/*     */ package me.zero.alpine.bus;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.stream.Collectors;
/*     */ import me.zero.alpine.listener.EventHandler;
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
/*     */ public class EventManager
/*     */   implements EventBus
/*     */ {
/*  27 */   private final Map<Listenable, List<Listener>> SUBSCRIPTION_CACHE = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  32 */   private final Map<Class<?>, List<Listener>> SUBSCRIPTION_MAP = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*     */   public void subscribe(Listenable listenable) {
/*  36 */     List<Listener> listeners = this.SUBSCRIPTION_CACHE.computeIfAbsent(listenable, o -> (List)Arrays.<Field>stream(o.getClass().getDeclaredFields()).filter(EventManager::isValidField).map(()).filter(Objects::nonNull).collect(Collectors.toList()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  43 */     listeners.forEach(this::subscribe);
/*     */   }
/*     */ 
/*     */   
/*     */   public void subscribe(Listener listener) {
/*  48 */     List<Listener> listeners = this.SUBSCRIPTION_MAP.computeIfAbsent(listener.getTarget(), target -> new CopyOnWriteArrayList());
/*     */     
/*  50 */     int index = 0;
/*  51 */     for (; index < listeners.size() && 
/*  52 */       listener.getPriority() <= ((Listener)listeners.get(index)).getPriority(); index++);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     listeners.add(index, listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public void unsubscribe(Listenable listenable) {
/*  62 */     List<Listener> objectListeners = this.SUBSCRIPTION_CACHE.get(listenable);
/*  63 */     if (objectListeners == null) {
/*     */       return;
/*     */     }
/*  66 */     this.SUBSCRIPTION_MAP.values().forEach(listeners -> listeners.removeIf(objectListeners::contains));
/*     */   }
/*     */ 
/*     */   
/*     */   public void unsubscribe(Listener listener) {
/*  71 */     ((List)this.SUBSCRIPTION_MAP.get(listener.getTarget())).removeIf(l -> l.equals(listener));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void post(Object event) {
/*  77 */     List<Listener> listeners = this.SUBSCRIPTION_MAP.get(event.getClass());
/*  78 */     if (listeners != null) {
/*  79 */       listeners.forEach(listener -> listener.invoke(event));
/*     */     }
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
/*     */   
/*     */   private static boolean isValidField(Field field) {
/*  93 */     return (field.isAnnotationPresent((Class)EventHandler.class) && Listener.class.isAssignableFrom(field.getType()));
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
/*     */ 
/*     */   
/*     */   private static Listener asListener(Listenable listenable, Field field) {
/*     */     try {
/* 108 */       boolean accessible = field.isAccessible();
/* 109 */       field.setAccessible(true);
/* 110 */       Listener listener = (Listener)field.get(listenable);
/* 111 */       field.setAccessible(accessible);
/*     */       
/* 113 */       if (listener == null) {
/* 114 */         return null;
/*     */       }
/* 116 */       return listener;
/* 117 */     } catch (IllegalAccessException e) {
/* 118 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\bus\EventManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */