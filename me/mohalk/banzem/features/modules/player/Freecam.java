/*     */ package me.mohalk.banzem.features.modules.player;
/*     */ 
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.event.events.PushEvent;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.MathUtil;
/*     */ import net.minecraft.client.entity.EntityOtherPlayerMP;
/*     */ import net.minecraft.client.entity.EntityPlayerSP;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketConfirmTeleport;
/*     */ import net.minecraft.network.play.client.CPacketPlayer;
/*     */ import net.minecraft.network.play.server.SPacketPlayerPosLook;
/*     */ import net.minecraft.network.play.server.SPacketSetPassengers;
/*     */ import net.minecraft.util.math.AxisAlignedBB;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ 
/*     */ public class Freecam extends Module {
/*  23 */   private static Freecam INSTANCE = new Freecam();
/*     */   
/*     */   public Setting<Double> speed;
/*     */   
/*     */   public Setting<Boolean> view;
/*     */   public Setting<Boolean> packet;
/*     */   public Setting<Boolean> disable;
/*     */   public Setting<Boolean> legit;
/*     */   private AxisAlignedBB oldBoundingBox;
/*     */   private EntityOtherPlayerMP entity;
/*     */   private Vec3d position;
/*     */   private Entity riding;
/*     */   private float yaw;
/*     */   private float pitch;
/*     */   
/*     */   public Freecam() {
/*  39 */     super("Freecam", "Look around freely.", Module.Category.PLAYER, true, false, false);
/*  40 */     this.speed = register(new Setting("Speed", Double.valueOf(0.5D), Double.valueOf(0.1D), Double.valueOf(5.0D)));
/*  41 */     this.view = register(new Setting("3D", Boolean.valueOf(false)));
/*  42 */     this.packet = register(new Setting("Packet", Boolean.valueOf(true)));
/*  43 */     this.disable = register(new Setting("Logout/Off", Boolean.valueOf(true)));
/*  44 */     this.legit = register(new Setting("Legit", Boolean.valueOf(false)));
/*  45 */     setInstance();
/*     */   }
/*     */   
/*     */   public static Freecam getInstance() {
/*  49 */     if (INSTANCE == null) {
/*  50 */       INSTANCE = new Freecam();
/*     */     }
/*  52 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  56 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  61 */     if (!Feature.fullNullCheck()) {
/*  62 */       this.oldBoundingBox = mc.field_71439_g.func_174813_aQ();
/*  63 */       mc.field_71439_g.func_174826_a(new AxisAlignedBB(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v));
/*  64 */       if (mc.field_71439_g.func_184187_bx() != null) {
/*  65 */         this.riding = mc.field_71439_g.func_184187_bx();
/*  66 */         mc.field_71439_g.func_184210_p();
/*     */       } 
/*  68 */       (this.entity = new EntityOtherPlayerMP((World)mc.field_71441_e, mc.field_71449_j.func_148256_e())).func_82149_j((Entity)mc.field_71439_g);
/*  69 */       this.entity.field_70177_z = mc.field_71439_g.field_70177_z;
/*  70 */       this.entity.field_70759_as = mc.field_71439_g.field_70759_as;
/*  71 */       this.entity.field_71071_by.func_70455_b(mc.field_71439_g.field_71071_by);
/*  72 */       mc.field_71441_e.func_73027_a(69420, (Entity)this.entity);
/*  73 */       this.position = mc.field_71439_g.func_174791_d();
/*  74 */       this.yaw = mc.field_71439_g.field_70177_z;
/*  75 */       this.pitch = mc.field_71439_g.field_70125_A;
/*  76 */       mc.field_71439_g.field_70145_X = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  82 */     if (!Feature.fullNullCheck()) {
/*  83 */       mc.field_71439_g.func_174826_a(this.oldBoundingBox);
/*  84 */       if (this.riding != null) {
/*  85 */         mc.field_71439_g.func_184205_a(this.riding, true);
/*     */       }
/*  87 */       if (this.entity != null) {
/*  88 */         mc.field_71441_e.func_72900_e((Entity)this.entity);
/*     */       }
/*  90 */       if (this.position != null) {
/*  91 */         mc.field_71439_g.func_70107_b(this.position.field_72450_a, this.position.field_72448_b, this.position.field_72449_c);
/*     */       }
/*  93 */       mc.field_71439_g.field_70177_z = this.yaw;
/*  94 */       mc.field_71439_g.field_70125_A = this.pitch;
/*  95 */       mc.field_71439_g.field_70145_X = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/* 101 */     mc.field_71439_g.field_70145_X = true;
/* 102 */     mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
/* 103 */     mc.field_71439_g.field_70747_aH = ((Double)this.speed.getValue()).floatValue();
/* 104 */     double[] dir = MathUtil.directionSpeed(((Double)this.speed.getValue()).doubleValue());
/* 105 */     if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
/* 106 */       mc.field_71439_g.field_70159_w = dir[0];
/* 107 */       mc.field_71439_g.field_70179_y = dir[1];
/*     */     } else {
/* 109 */       mc.field_71439_g.field_70159_w = 0.0D;
/* 110 */       mc.field_71439_g.field_70179_y = 0.0D;
/*     */     } 
/* 112 */     mc.field_71439_g.func_70031_b(false);
/* 113 */     if (((Boolean)this.view.getValue()).booleanValue() && !mc.field_71474_y.field_74311_E.func_151470_d() && !mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 114 */       mc.field_71439_g.field_70181_x = ((Double)this.speed.getValue()).doubleValue() * -MathUtil.degToRad(mc.field_71439_g.field_70125_A) * mc.field_71439_g.field_71158_b.field_192832_b;
/*     */     }
/* 116 */     if (mc.field_71474_y.field_74314_A.func_151470_d()) {
/* 117 */       EntityPlayerSP player = mc.field_71439_g;
/* 118 */       player.field_70181_x += ((Double)this.speed.getValue()).doubleValue();
/*     */     } 
/* 120 */     if (mc.field_71474_y.field_74311_E.func_151470_d()) {
/* 121 */       EntityPlayerSP player2 = mc.field_71439_g;
/* 122 */       player2.field_70181_x -= ((Double)this.speed.getValue()).doubleValue();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLogout() {
/* 128 */     if (((Boolean)this.disable.getValue()).booleanValue()) {
/* 129 */       disable();
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketSend(PacketEvent.Send event) {
/* 135 */     if (((Boolean)this.legit.getValue()).booleanValue() && this.entity != null && event.getPacket() instanceof CPacketPlayer) {
/* 136 */       CPacketPlayer packetPlayer = (CPacketPlayer)event.getPacket();
/* 137 */       packetPlayer.field_149479_a = this.entity.field_70165_t;
/* 138 */       packetPlayer.field_149477_b = this.entity.field_70163_u;
/* 139 */       packetPlayer.field_149478_c = this.entity.field_70161_v;
/*     */       return;
/*     */     } 
/* 142 */     if (((Boolean)this.packet.getValue()).booleanValue()) {
/* 143 */       if (event.getPacket() instanceof CPacketPlayer) {
/* 144 */         event.setCanceled(true);
/*     */       }
/* 146 */     } else if (!(event.getPacket() instanceof net.minecraft.network.play.client.CPacketUseEntity) && !(event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItem) && !(event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock) && !(event.getPacket() instanceof CPacketPlayer) && !(event.getPacket() instanceof net.minecraft.network.play.client.CPacketVehicleMove) && !(event.getPacket() instanceof net.minecraft.network.play.client.CPacketChatMessage) && !(event.getPacket() instanceof net.minecraft.network.play.client.CPacketKeepAlive)) {
/* 147 */       event.setCanceled(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceive(PacketEvent.Receive event) {
/* 153 */     if (event.getPacket() instanceof SPacketSetPassengers) {
/* 154 */       SPacketSetPassengers packet = (SPacketSetPassengers)event.getPacket();
/* 155 */       Entity riding = mc.field_71441_e.func_73045_a(packet.func_186972_b());
/* 156 */       if (riding != null && riding == this.riding) {
/* 157 */         this.riding = null;
/*     */       }
/*     */     } 
/* 160 */     if (event.getPacket() instanceof SPacketPlayerPosLook) {
/* 161 */       SPacketPlayerPosLook packet2 = (SPacketPlayerPosLook)event.getPacket();
/* 162 */       if (((Boolean)this.packet.getValue()).booleanValue()) {
/* 163 */         if (this.entity != null) {
/* 164 */           this.entity.func_70080_a(packet2.func_148932_c(), packet2.func_148928_d(), packet2.func_148933_e(), packet2.func_148931_f(), packet2.func_148930_g());
/*     */         }
/* 166 */         this.position = new Vec3d(packet2.func_148932_c(), packet2.func_148928_d(), packet2.func_148933_e());
/* 167 */         mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(packet2.func_186965_f()));
/* 168 */         event.setCanceled(true);
/*     */       } else {
/* 170 */         event.setCanceled(true);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPush(PushEvent event) {
/* 177 */     if (event.getStage() == 1)
/* 178 */       event.setCanceled(true); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\Freecam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */