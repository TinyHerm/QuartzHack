/*     */ package me.mohalk.banzem.features.modules.player;
/*     */ import com.mojang.authlib.GameProfile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Random;
/*     */ import java.util.UUID;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import net.minecraft.client.entity.EntityOtherPlayerMP;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class FakePlayer extends Module {
/*  16 */   public List<Integer> fakePlayerIdList = new ArrayList<>();
/*  17 */   public Setting<Boolean> moving = register(new Setting("Moving", Boolean.valueOf(false)));
/*  18 */   private static FakePlayer INSTANCE = new FakePlayer();
/*     */   private EntityOtherPlayerMP otherPlayer;
/*     */   
/*     */   public FakePlayer() {
/*  22 */     super("FakePlayer", "Spawns fake player", Module.Category.PLAYER, false, false, false);
/*  23 */     setInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FakePlayer getInstance() {
/*  29 */     if (INSTANCE == null) {
/*  30 */       INSTANCE = new FakePlayer();
/*     */     }
/*  32 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  36 */     INSTANCE = this;
/*     */   }
/*     */   
/*     */   public void onTick() {
/*  40 */     if (this.otherPlayer != null) {
/*  41 */       Random random = new Random();
/*  42 */       this.otherPlayer.field_191988_bg = mc.field_71439_g.field_191988_bg + random.nextInt(5) / 10.0F;
/*  43 */       this.otherPlayer.field_70702_br = mc.field_71439_g.field_70702_br + random.nextInt(5) / 10.0F;
/*  44 */       if (((Boolean)this.moving.getValue()).booleanValue()) travel(this.otherPlayer.field_70702_br, this.otherPlayer.field_70701_bs, this.otherPlayer.field_191988_bg); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void travel(float strafe, float vertical, float forward) {
/*  49 */     double d0 = this.otherPlayer.field_70163_u;
/*  50 */     float f1 = 0.8F;
/*  51 */     float f2 = 0.02F;
/*  52 */     float f3 = EnchantmentHelper.func_185294_d((EntityLivingBase)this.otherPlayer);
/*     */     
/*  54 */     if (f3 > 3.0F) {
/*  55 */       f3 = 3.0F;
/*     */     }
/*     */     
/*  58 */     if (!this.otherPlayer.field_70122_E) {
/*  59 */       f3 *= 0.5F;
/*     */     }
/*     */     
/*  62 */     if (f3 > 0.0F) {
/*  63 */       f1 += (0.54600006F - f1) * f3 / 3.0F;
/*  64 */       f2 += (this.otherPlayer.func_70689_ay() - f2) * f3 / 4.0F;
/*     */     } 
/*     */     
/*  67 */     this.otherPlayer.func_191958_b(strafe, vertical, forward, f2);
/*  68 */     this.otherPlayer.func_70091_d(MoverType.SELF, this.otherPlayer.field_70159_w, this.otherPlayer.field_70181_x, this.otherPlayer.field_70179_y);
/*  69 */     this.otherPlayer.field_70159_w *= f1;
/*  70 */     this.otherPlayer.field_70181_x *= 0.800000011920929D;
/*  71 */     this.otherPlayer.field_70179_y *= f1;
/*     */     
/*  73 */     if (!this.otherPlayer.func_189652_ae()) {
/*  74 */       this.otherPlayer.field_70181_x -= 0.02D;
/*     */     }
/*     */     
/*  77 */     if (this.otherPlayer.field_70123_F && this.otherPlayer.func_70038_c(this.otherPlayer.field_70159_w, this.otherPlayer.field_70181_x + 0.6000000238418579D - this.otherPlayer.field_70163_u + d0, this.otherPlayer.field_70179_y)) {
/*  78 */       this.otherPlayer.field_70181_x = 0.30000001192092896D;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  84 */     if (mc.field_71441_e == null || mc.field_71439_g == null) {
/*  85 */       toggle();
/*     */       return;
/*     */     } 
/*  88 */     this.fakePlayerIdList = new ArrayList<>();
/*     */     
/*  90 */     addFakePlayer(-100);
/*     */   }
/*     */   
/*     */   public void addFakePlayer(int entityId) {
/*  94 */     if (this.otherPlayer == null) {
/*  95 */       this.otherPlayer = new EntityOtherPlayerMP((World)mc.field_71441_e, new GameProfile(UUID.randomUUID(), "Eralp232"));
/*  96 */       this.otherPlayer.func_82149_j((Entity)mc.field_71439_g);
/*  97 */       this.otherPlayer.field_71071_by.func_70455_b(mc.field_71439_g.field_71071_by);
/*     */     } 
/*  99 */     mc.field_71441_e.func_73027_a(entityId, (Entity)this.otherPlayer);
/* 100 */     this.fakePlayerIdList.add(Integer.valueOf(entityId));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 106 */     for (Iterator<Integer> iterator = this.fakePlayerIdList.iterator(); iterator.hasNext(); ) { int id = ((Integer)iterator.next()).intValue();
/* 107 */       mc.field_71441_e.func_73028_b(id); }
/*     */     
/* 109 */     if (this.otherPlayer != null) {
/* 110 */       mc.field_71441_e.func_72900_e((Entity)this.otherPlayer);
/* 111 */       this.otherPlayer = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\player\FakePlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */