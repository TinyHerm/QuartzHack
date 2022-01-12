/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketAnimation;
/*     */ import net.minecraft.network.play.client.CPacketPlayer;
/*     */ import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
/*     */ import net.minecraft.network.play.client.CPacketUseEntity;
/*     */ import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
/*     */ import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
/*     */ import net.minecraft.network.play.server.SPacketSpawnMob;
/*     */ import net.minecraft.network.play.server.SPacketSpawnPainting;
/*     */ import net.minecraft.network.play.server.SPacketSpawnPlayer;
/*     */ import net.minecraft.util.EnumHand;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ import net.minecraftforge.fml.common.eventhandler.EventPriority;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class GodModule extends Module {
/*  30 */   public Setting<Integer> rotations = register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20)));
/*  31 */   public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
/*  32 */   public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(false)));
/*  33 */   public Setting<Boolean> antiIllegal = register(new Setting("AntiIllegal", Boolean.valueOf(true)));
/*  34 */   public Setting<Boolean> checkPos = register(new Setting("CheckPos", Boolean.valueOf(true)));
/*  35 */   public Setting<Boolean> oneDot15 = register(new Setting("1.15", Boolean.valueOf(false)));
/*  36 */   public Setting<Boolean> entitycheck = register(new Setting("EntityCheck", Boolean.valueOf(false)));
/*  37 */   public Setting<Integer> attacks = register(new Setting("Attacks", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(10)));
/*  38 */   public Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(50)));
/*  39 */   private float yaw = 0.0F;
/*  40 */   private float pitch = 0.0F;
/*     */   private boolean rotating;
/*     */   private int rotationPacketsSpoofed;
/*  43 */   private int highestID = -100000;
/*     */   
/*     */   public GodModule() {
/*  46 */     super("GodModule", "Wow", Module.Category.COMBAT, true, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onToggle() {
/*  51 */     resetFields();
/*  52 */     if (mc.field_71441_e != null) {
/*  53 */       updateEntityID();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  59 */     if (((Boolean)this.render.getValue()).booleanValue()) {
/*  60 */       for (Entity entity : mc.field_71441_e.field_72996_f) {
/*  61 */         if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
/*  62 */           continue;  entity.func_96094_a(String.valueOf(entity.field_145783_c));
/*  63 */         entity.func_174805_g(true);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLogout() {
/*  70 */     resetFields();
/*     */   }
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.HIGHEST)
/*     */   public void onSendPacket(PacketEvent.Send event) {
/*  75 */     if (event.getStage() == 0 && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
/*  76 */       CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
/*  77 */       if (mc.field_71439_g.func_184586_b(packet.field_187027_c).func_77973_b() instanceof net.minecraft.item.ItemEndCrystal) {
/*  78 */         if ((((Boolean)this.checkPos.getValue()).booleanValue() && !BlockUtil.canPlaceCrystal(packet.field_179725_b, ((Boolean)this.entitycheck.getValue()).booleanValue(), ((Boolean)this.oneDot15.getValue()).booleanValue())) || checkPlayers()) {
/*     */           return;
/*     */         }
/*  81 */         updateEntityID();
/*  82 */         for (int i = 1; i < ((Integer)this.attacks.getValue()).intValue(); i++) {
/*  83 */           attackID(packet.field_179725_b, this.highestID + i);
/*     */         }
/*     */       } 
/*     */     } 
/*  87 */     if (event.getStage() == 0 && this.rotating && ((Boolean)this.rotate.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayer) {
/*  88 */       CPacketPlayer packet = (CPacketPlayer)event.getPacket();
/*  89 */       packet.field_149476_e = this.yaw;
/*  90 */       packet.field_149473_f = this.pitch;
/*  91 */       this.rotationPacketsSpoofed++;
/*  92 */       if (this.rotationPacketsSpoofed >= ((Integer)this.rotations.getValue()).intValue()) {
/*  93 */         this.rotating = false;
/*  94 */         this.rotationPacketsSpoofed = 0;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void attackID(BlockPos pos, int id) {
/* 100 */     Entity entity = mc.field_71441_e.func_73045_a(id);
/* 101 */     if (entity == null || entity instanceof net.minecraft.entity.item.EntityEnderCrystal) {
/* 102 */       AttackThread attackThread = new AttackThread(id, pos, ((Integer)this.delay.getValue()).intValue(), this);
/* 103 */       attackThread.start();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceive(PacketEvent.Receive event) {
/* 109 */     if (event.getPacket() instanceof SPacketSpawnObject) {
/* 110 */       checkID(((SPacketSpawnObject)event.getPacket()).func_149001_c());
/* 111 */     } else if (event.getPacket() instanceof SPacketSpawnExperienceOrb) {
/* 112 */       checkID(((SPacketSpawnExperienceOrb)event.getPacket()).func_148985_c());
/* 113 */     } else if (event.getPacket() instanceof SPacketSpawnPlayer) {
/* 114 */       checkID(((SPacketSpawnPlayer)event.getPacket()).func_148943_d());
/* 115 */     } else if (event.getPacket() instanceof SPacketSpawnGlobalEntity) {
/* 116 */       checkID(((SPacketSpawnGlobalEntity)event.getPacket()).func_149052_c());
/* 117 */     } else if (event.getPacket() instanceof SPacketSpawnPainting) {
/* 118 */       checkID(((SPacketSpawnPainting)event.getPacket()).func_148965_c());
/* 119 */     } else if (event.getPacket() instanceof SPacketSpawnMob) {
/* 120 */       checkID(((SPacketSpawnMob)event.getPacket()).func_149024_d());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkID(int id) {
/* 125 */     if (id > this.highestID) {
/* 126 */       this.highestID = id;
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateEntityID() {
/* 131 */     for (Entity entity : mc.field_71441_e.field_72996_f) {
/* 132 */       if (entity.func_145782_y() <= this.highestID)
/* 133 */         continue;  this.highestID = entity.func_145782_y();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean checkPlayers() {
/* 138 */     if (((Boolean)this.antiIllegal.getValue()).booleanValue()) {
/* 139 */       for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 140 */         if (!checkItem(player.func_184614_ca()) && !checkItem(player.func_184592_cb()))
/*     */           continue; 
/* 142 */         return false;
/*     */       } 
/*     */     }
/* 145 */     return true;
/*     */   }
/*     */   
/*     */   private boolean checkItem(ItemStack stack) {
/* 149 */     return (stack.func_77973_b() instanceof net.minecraft.item.ItemBow || stack.func_77973_b() instanceof net.minecraft.item.ItemExpBottle || stack.func_77973_b() == Items.field_151007_F);
/*     */   }
/*     */   
/*     */   public void rotateTo(BlockPos pos) {
/* 153 */     float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(Util.mc.func_184121_ak()), new Vec3d((Vec3i)pos));
/* 154 */     this.yaw = angle[0];
/* 155 */     this.pitch = angle[1];
/* 156 */     this.rotating = true;
/*     */   }
/*     */   
/*     */   private void resetFields() {
/* 160 */     this.rotating = false;
/* 161 */     this.highestID = -1000000;
/*     */   }
/*     */   
/*     */   public static class AttackThread
/*     */     extends Thread {
/*     */     private final BlockPos pos;
/*     */     private final int id;
/*     */     private final int delay;
/*     */     private final GodModule godModule;
/*     */     
/*     */     public AttackThread(int idIn, BlockPos posIn, int delayIn, GodModule godModuleIn) {
/* 172 */       this.id = idIn;
/* 173 */       this.pos = posIn;
/* 174 */       this.delay = delayIn;
/* 175 */       this.godModule = godModuleIn;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/* 181 */         wait(this.delay);
/* 182 */         CPacketUseEntity attack = new CPacketUseEntity();
/* 183 */         attack.field_149567_a = this.id;
/* 184 */         attack.field_149566_b = CPacketUseEntity.Action.ATTACK;
/* 185 */         this.godModule.rotateTo(this.pos.func_177984_a());
/* 186 */         Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)attack);
/* 187 */         Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
/* 188 */       } catch (InterruptedException e) {
/* 189 */         e.printStackTrace();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\GodModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */