/*    */ package me.mohalk.banzem.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.event.events.ValueChangeEvent;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraft.network.INetHandler;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.network.play.INetHandlerPlayServer;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.fml.common.eventhandler.Event;
/*    */ 
/*    */ public class CPacketChangeSetting implements Packet<INetHandlerPlayServer> {
/*    */   public String setting;
/*    */   
/*    */   public CPacketChangeSetting(String module, String setting, String value) {
/* 19 */     this.setting = setting + "-" + module + "-" + value;
/*    */   }
/*    */   
/*    */   public CPacketChangeSetting(Module module, Setting setting, String value) {
/* 23 */     this.setting = setting.getName() + "-" + module.getName() + "-" + value;
/*    */   }
/*    */   
/*    */   public void func_148837_a(PacketBuffer buf) throws IOException {
/* 27 */     this.setting = buf.func_150789_c(256);
/*    */   }
/*    */   
/*    */   public void func_148840_b(PacketBuffer buf) throws IOException {
/* 31 */     buf.func_180714_a(this.setting);
/*    */   }
/*    */   
/*    */   public void processPacket(INetHandlerPlayServer handler) {
/* 35 */     Module module = Banzem.moduleManager.getModuleByName(this.setting.split("-")[1]);
/* 36 */     Setting setting1 = module.getSettingByName(this.setting.split("-")[0]);
/* 37 */     MinecraftForge.EVENT_BUS.post((Event)new ValueChangeEvent(setting1, this.setting.split("-")[2]));
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\CPacketChangeSetting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */