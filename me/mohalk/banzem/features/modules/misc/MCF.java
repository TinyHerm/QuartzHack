/*    */ package me.mohalk.banzem.features.modules.misc;
/*    */ 
/*    */ import me.mohalk.banzem.Banzem;
/*    */ import me.mohalk.banzem.features.command.Command;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import me.mohalk.banzem.features.modules.client.ClickGui;
/*    */ import me.mohalk.banzem.features.modules.client.ServerModule;
/*    */ import me.mohalk.banzem.features.setting.Bind;
/*    */ import me.mohalk.banzem.features.setting.Setting;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.network.Packet;
/*    */ import net.minecraft.network.play.client.CPacketChatMessage;
/*    */ import net.minecraft.util.math.RayTraceResult;
/*    */ import net.minecraftforge.fml.common.eventhandler.EventPriority;
/*    */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*    */ import net.minecraftforge.fml.common.gameevent.InputEvent;
/*    */ import org.lwjgl.input.Keyboard;
/*    */ import org.lwjgl.input.Mouse;
/*    */ 
/*    */ public class MCF
/*    */   extends Module
/*    */ {
/* 23 */   private final Setting<Boolean> middleClick = register(new Setting("MiddleClick", Boolean.valueOf(true)));
/* 24 */   private final Setting<Boolean> keyboard = register(new Setting("Keyboard", Boolean.valueOf(false)));
/* 25 */   private final Setting<Boolean> server = register(new Setting("Server", Boolean.valueOf(true)));
/* 26 */   private final Setting<Bind> key = register(new Setting("KeyBind", new Bind(-1), v -> ((Boolean)this.keyboard.getValue()).booleanValue()));
/*    */   private boolean clicked = false;
/*    */   
/*    */   public MCF() {
/* 30 */     super("MCF", "Middleclick Friends.", Module.Category.MISC, true, false, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUpdate() {
/* 35 */     if (Mouse.isButtonDown(2)) {
/* 36 */       if (!this.clicked && ((Boolean)this.middleClick.getValue()).booleanValue() && mc.field_71462_r == null) {
/* 37 */         onClick();
/*    */       }
/* 39 */       this.clicked = true;
/*    */     } else {
/* 41 */       this.clicked = false;
/*    */     } 
/*    */   }
/*    */   
/*    */   @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
/*    */   public void onKeyInput(InputEvent.KeyInputEvent event) {
/* 47 */     if (((Boolean)this.keyboard.getValue()).booleanValue() && Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof me.mohalk.banzem.features.gui.PhobosGui) && ((Bind)this.key.getValue()).getKey() == Keyboard.getEventKey()) {
/* 48 */       onClick();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   private void onClick() {
/* 54 */     RayTraceResult result = mc.field_71476_x; Entity entity;
/* 55 */     if (result != null && result.field_72313_a == RayTraceResult.Type.ENTITY && entity = result.field_72308_g instanceof net.minecraft.entity.player.EntityPlayer) {
/* 56 */       if (Banzem.friendManager.isFriend(entity.func_70005_c_())) {
/* 57 */         Banzem.friendManager.removeFriend(entity.func_70005_c_());
/* 58 */         Command.sendMessage("§c" + entity.func_70005_c_() + "§r unfriended.");
/* 59 */         if (((Boolean)this.server.getValue()).booleanValue() && ServerModule.getInstance().isConnected()) {
/* 60 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Serverprefix" + (String)(ClickGui.getInstance()).prefix.getValue()));
/* 61 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Server" + (String)(ClickGui.getInstance()).prefix.getValue() + "friend del " + entity.func_70005_c_()));
/*    */         } 
/*    */       } else {
/* 64 */         Banzem.friendManager.addFriend(entity.func_70005_c_());
/* 65 */         Command.sendMessage("§b" + entity.func_70005_c_() + "§r friended.");
/* 66 */         if (((Boolean)this.server.getValue()).booleanValue() && ServerModule.getInstance().isConnected()) {
/* 67 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Serverprefix" + (String)(ClickGui.getInstance()).prefix.getValue()));
/* 68 */           mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage("@Server" + (String)(ClickGui.getInstance()).prefix.getValue() + "friend add " + entity.func_70005_c_()));
/*    */         } 
/*    */       } 
/*    */     }
/* 72 */     this.clicked = true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\MCF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */