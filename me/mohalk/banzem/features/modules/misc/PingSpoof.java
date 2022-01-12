/*    */ package me.mohalk.banzem.features.modules.misc;
/*    */ 
/*    */ import java.util.Queue;
/*    */ import java.util.concurrent.ConcurrentLinkedQueue;
/*    */ import me.mohalk.banzem.event.events.PacketEvent;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.MathUtil;
/*    */ import me.mohalk.banzem.util.Timer;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PingSpoof
/*    */   extends Module
/*    */ {
/* 18 */   private final Setting<Boolean> seconds = register(new Setting("Seconds", Boolean.valueOf(false)));
/* 19 */   private final Setting<Integer> delay = register(new Setting("DelayMS", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(1000), v -> !((Boolean)this.seconds.getValue()).booleanValue()));
/* 20 */   private final Setting<Integer> secondDelay = register(new Setting("DelayS", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(30), v -> ((Boolean)this.seconds.getValue()).booleanValue()));
/* 21 */   private final Setting<Boolean> offOnLogout = register(new Setting("Logout", Boolean.valueOf(false)));
/* 22 */   private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
/* 23 */   private final Timer timer = new Timer();
/*    */   
/*    */   private boolean receive = true;
/*    */   
/*    */   public PingSpoof() {
/* 28 */     super("PingSpoof", "Spoofs your ping!", Module.Category.MISC, true, false, false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onLoad() {
/* 34 */     if (((Boolean)this.offOnLogout.getValue()).booleanValue()) {
/* 35 */       disable();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onLogout() {
/* 42 */     if (((Boolean)this.offOnLogout.getValue()).booleanValue()) {
/* 43 */       disable();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 50 */     clearQueue();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 56 */     clearQueue();
/*    */   }
/*    */ 
/*    */   
/*    */   @SubscribeEvent
/*    */   public void onPacketSend(PacketEvent.Send event) {
/* 62 */     if (this.receive && mc.field_71439_g != null && !mc.func_71356_B() && mc.field_71439_g.func_70089_S() && event.getStage() == 0 && event.getPacket() instanceof net.minecraft.network.play.client.CPacketKeepAlive) {
/* 63 */       this.packets.add(event.getPacket());
/* 64 */       event.setCanceled(true);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void clearQueue() {
/* 70 */     if (mc.field_71439_g != null && !mc.func_71356_B() && mc.field_71439_g.func_70089_S() && ((!((Boolean)this.seconds.getValue()).booleanValue() && this.timer.passedMs(((Integer)this.delay.getValue()).intValue())) || (((Boolean)this.seconds.getValue()).booleanValue() && this.timer.passedS(((Integer)this.secondDelay.getValue()).intValue())))) {
/* 71 */       double limit = MathUtil.getIncremental(Math.random() * 10.0D, 1.0D);
/* 72 */       this.receive = false;
/* 73 */       int i = 0;
/* 74 */       while (i < limit) {
/* 75 */         Packet<?> packet = this.packets.poll();
/* 76 */         if (packet != null) {
/* 77 */           mc.field_71439_g.field_71174_a.func_147297_a(packet);
/*    */         }
/* 79 */         i++;
/*    */       } 
/* 81 */       this.timer.reset();
/* 82 */       this.receive = true;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\PingSpoof.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */