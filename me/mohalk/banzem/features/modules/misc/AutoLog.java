/*    */ package me.mohalk.banzem.features.modules.misc;
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.event.events.PacketEvent;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import me.mohalk.banzem.util.MathUtil;
/*    */ import net.minecraft.init.Blocks;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraft.network.play.server.SPacketBlockChange;
/*    */ import net.minecraft.network.play.server.SPacketDisconnect;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.TextComponentString;
/*    */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*    */ 
/*    */ public class AutoLog extends Module {
/* 16 */   private static AutoLog INSTANCE = new AutoLog();
/* 17 */   private final Setting<Float> health = register(new Setting("Health", Float.valueOf(16.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
/* 18 */   private final Setting<Boolean> bed = register(new Setting("Beds", Boolean.valueOf(true)));
/* 19 */   private final Setting<Float> range = register(new Setting("BedRange", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> ((Boolean)this.bed.getValue()).booleanValue()));
/* 20 */   private final Setting<Boolean> logout = register(new Setting("LogoutOff", Boolean.valueOf(true)));
/*    */   
/*    */   public AutoLog() {
/* 23 */     super("AutoLog", "Logs when in danger.", Module.Category.MISC, false, false, false);
/* 24 */     setInstance();
/*    */   }
/*    */   
/*    */   public static AutoLog getInstance() {
/* 28 */     if (INSTANCE == null) {
/* 29 */       INSTANCE = new AutoLog();
/*    */     }
/* 31 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   private void setInstance() {
/* 35 */     INSTANCE = this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onTick() {
/* 40 */     if (!nullCheck() && mc.field_71439_g.func_110143_aJ() <= ((Float)this.health.getValue()).floatValue()) {
/* 41 */       Banzem.moduleManager.disableModule("AutoReconnect");
/* 42 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
/* 43 */       if (((Boolean)this.logout.getValue()).booleanValue()) {
/* 44 */         disable();
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   @SubscribeEvent
/*    */   public void onReceivePacket(PacketEvent.Receive event) {
/*    */     SPacketBlockChange packet;
/* 52 */     if (event.getPacket() instanceof SPacketBlockChange && ((Boolean)this.bed.getValue()).booleanValue() && (packet = (SPacketBlockChange)event.getPacket()).func_180728_a().func_177230_c() == Blocks.field_150324_C && mc.field_71439_g.func_174831_c(packet.func_179827_b()) <= MathUtil.square(((Float)this.range.getValue()).floatValue())) {
/* 53 */       Banzem.moduleManager.disableModule("AutoReconnect");
/* 54 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
/* 55 */       if (((Boolean)this.logout.getValue()).booleanValue())
/* 56 */         disable(); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\AutoLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */