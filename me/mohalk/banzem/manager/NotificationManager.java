/*    */ package me.mohalk.banzem.manager;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.modules.client.HUD;
/*    */ import me.mohalk.banzem.features.notifications.Notifications;
/*    */ 
/*    */ public class NotificationManager
/*    */ {
/* 10 */   private final ArrayList<Notifications> notifications = new ArrayList<>();
/*    */   
/*    */   public void handleNotifications(int posY) {
/* 13 */     for (int i = 0; i < getNotifications().size(); i++) {
/* 14 */       ((Notifications)getNotifications().get(i)).onDraw(posY);
/* 15 */       posY -= ((HUD)Banzem.moduleManager.getModuleByClass((Class)HUD.class)).renderer.getFontHeight() + 5;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void addNotification(String text, long duration) {
/* 20 */     getNotifications().add(new Notifications(text, duration));
/*    */   }
/*    */   
/*    */   public ArrayList<Notifications> getNotifications() {
/* 24 */     return this.notifications;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\NotificationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */