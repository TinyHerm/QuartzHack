/*    */ package me.mohalk.banzem.manager;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.event.events.PacketEvent;
/*    */ import me.mohalk.banzem.features.Feature;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ import net.minecraft.network.play.client.CPacketChatMessage;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*    */ 
/*    */ public class ReloadManager
/*    */   extends Feature {
/*    */   public String prefix;
/*    */   
/*    */   public void init(String prefix) {
/* 16 */     this.prefix = prefix;
/* 17 */     MinecraftForge.EVENT_BUS.register(this);
/* 18 */     if (!fullNullCheck()) {
/* 19 */       Command.sendMessage("§cQuartzHack has been unloaded. Type " + prefix + "reload to reload.");
/*    */     }
/*    */   }
/*    */   
/*    */   public void unload() {
/* 24 */     MinecraftForge.EVENT_BUS.unregister(this);
/*    */   }
/*    */   
/*    */   @SubscribeEvent
/*    */   public void onPacketSend(PacketEvent.Send event) {
/*    */     CPacketChatMessage packet;
/* 30 */     if (event.getPacket() instanceof CPacketChatMessage && (packet = (CPacketChatMessage)event.getPacket()).func_149439_c().startsWith(this.prefix) && packet.func_149439_c().contains("reload")) {
/* 31 */       Banzem.load();
/* 32 */       event.setCanceled(true);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\ReloadManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */