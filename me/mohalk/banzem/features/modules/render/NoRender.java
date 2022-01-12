/*     */ package me.mohalk.banzem.features.modules.render;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.UUID;
/*     */ import me.mohalk.banzem.event.events.PacketEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.client.gui.BossInfoClient;
/*     */ import net.minecraft.client.gui.GuiBossOverlay;
/*     */ import net.minecraft.client.gui.ScaledResolution;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.item.EntityItem;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.init.SoundEvents;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.util.EnumParticleTypes;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.world.BossInfo;
/*     */ import net.minecraft.world.GameType;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.client.event.RenderGameOverlayEvent;
/*     */ import net.minecraftforge.client.event.RenderLivingEvent;
/*     */ import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ public class NoRender
/*     */   extends Module
/*     */ {
/*  35 */   private static NoRender INSTANCE = new NoRender();
/*     */   
/*     */   static {
/*  38 */     INSTANCE = new NoRender();
/*     */   }
/*     */   
/*  41 */   public Setting<Boolean> fire = register(new Setting("Fire", Boolean.valueOf(false), "Removes the portal overlay."));
/*  42 */   public Setting<Boolean> portal = register(new Setting("Portal", Boolean.valueOf(false), "Removes the portal overlay."));
/*  43 */   public Setting<Boolean> pumpkin = register(new Setting("Pumpkin", Boolean.valueOf(false), "Removes the pumpkin overlay."));
/*  44 */   public Setting<Boolean> totemPops = register(new Setting("TotemPop", Boolean.valueOf(false), "Removes the Totem overlay."));
/*  45 */   public Setting<Boolean> items = register(new Setting("Items", Boolean.valueOf(false), "Removes items on the ground."));
/*  46 */   public Setting<Boolean> nausea = register(new Setting("Nausea", Boolean.valueOf(false), "Removes Portal Nausea."));
/*  47 */   public Setting<Boolean> hurtcam = register(new Setting("HurtCam", Boolean.valueOf(false), "Removes shaking after taking damage."));
/*  48 */   public Setting<Fog> fog = register(new Setting("Fog", Fog.NONE, "Removes Fog."));
/*  49 */   public Setting<Boolean> noWeather = register(new Setting("Weather", Boolean.valueOf(false), "AntiWeather"));
/*  50 */   public Setting<Boss> boss = register(new Setting("BossBars", Boss.NONE, "Modifies the bossbars."));
/*  51 */   public Setting<Float> scale = register(new Setting("Scale", Float.valueOf(0.0F), Float.valueOf(0.5F), Float.valueOf(1.0F), v -> (this.boss.getValue() == Boss.MINIMIZE || this.boss.getValue() != Boss.STACK), "Scale of the bars."));
/*  52 */   public Setting<Boolean> bats = register(new Setting("Bats", Boolean.valueOf(false), "Removes bats."));
/*  53 */   public Setting<NoArmor> noArmor = register(new Setting("NoArmor", NoArmor.NONE, "Doesnt Render Armor on players."));
/*  54 */   public Setting<Boolean> glint = register(new Setting("Glint", Boolean.valueOf(false), v -> (this.noArmor.getValue() != NoArmor.NONE)));
/*  55 */   public Setting<Skylight> skylight = register(new Setting("Skylight", Skylight.NONE));
/*  56 */   public Setting<Boolean> barriers = register(new Setting("Barriers", Boolean.valueOf(false), "Barriers"));
/*  57 */   public Setting<Boolean> blocks = register(new Setting("Blocks", Boolean.valueOf(false), "Blocks"));
/*  58 */   public Setting<Boolean> advancements = register(new Setting("Advancements", Boolean.valueOf(false)));
/*  59 */   public Setting<Boolean> pigmen = register(new Setting("Pigmen", Boolean.valueOf(false)));
/*  60 */   public Setting<Boolean> timeChange = register(new Setting("TimeChange", Boolean.valueOf(false)));
/*  61 */   public Setting<Integer> time = register(new Setting("Time", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(23000), v -> ((Boolean)this.timeChange.getValue()).booleanValue()));
/*     */   
/*     */   public NoRender() {
/*  64 */     super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
/*  65 */     setInstance();
/*     */   }
/*     */   
/*     */   public static NoRender getInstance() {
/*  69 */     if (INSTANCE == null) {
/*  70 */       INSTANCE = new NoRender();
/*     */     }
/*  72 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private void setInstance() {
/*  76 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  81 */     if (((Boolean)this.items.getValue()).booleanValue()) {
/*  82 */       mc.field_71441_e.field_72996_f.stream().filter(EntityItem.class::isInstance).map(EntityItem.class::cast).forEach(Entity::func_70106_y);
/*     */     }
/*  84 */     if (((Boolean)this.noWeather.getValue()).booleanValue() && mc.field_71441_e.func_72896_J()) {
/*  85 */       mc.field_71441_e.func_72894_k(0.0F);
/*     */     }
/*  87 */     if (((Boolean)this.timeChange.getValue()).booleanValue()) {
/*  88 */       mc.field_71441_e.func_72877_b(((Integer)this.time.getValue()).intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPacketReceive(PacketEvent.Receive event) {
/*  94 */     if ((event.getPacket() instanceof net.minecraft.network.play.server.SPacketTimeUpdate & ((Boolean)this.timeChange.getValue()).booleanValue()) != 0) {
/*  95 */       event.setCanceled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void doVoidFogParticles(int posX, int posY, int posZ) {
/* 100 */     int i = 32;
/* 101 */     Random random = new Random();
/* 102 */     ItemStack itemstack = mc.field_71439_g.func_184614_ca();
/* 103 */     boolean flag = (!((Boolean)this.barriers.getValue()).booleanValue() || (mc.field_71442_b.func_178889_l() == GameType.CREATIVE && !itemstack.func_190926_b() && itemstack.func_77973_b() == Item.func_150898_a(Blocks.field_180401_cv)));
/* 104 */     BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
/* 105 */     for (int j = 0; j < 667; j++) {
/* 106 */       showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
/* 107 */       showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos) {
/* 112 */     int i = x + mc.field_71441_e.field_73012_v.nextInt(offset) - mc.field_71441_e.field_73012_v.nextInt(offset);
/* 113 */     int j = y + mc.field_71441_e.field_73012_v.nextInt(offset) - mc.field_71441_e.field_73012_v.nextInt(offset);
/* 114 */     int k = z + mc.field_71441_e.field_73012_v.nextInt(offset) - mc.field_71441_e.field_73012_v.nextInt(offset);
/* 115 */     pos.func_181079_c(i, j, k);
/* 116 */     IBlockState iblockstate = mc.field_71441_e.func_180495_p((BlockPos)pos);
/* 117 */     iblockstate.func_177230_c().func_180655_c(iblockstate, (World)mc.field_71441_e, (BlockPos)pos, random);
/* 118 */     if (!holdingBarrier && iblockstate.func_177230_c() == Blocks.field_180401_cv) {
/* 119 */       mc.field_71441_e.func_175688_a(EnumParticleTypes.BARRIER, (i + 0.5F), (j + 0.5F), (k + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onRenderPre(RenderGameOverlayEvent.Pre event) {
/* 125 */     if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && this.boss.getValue() != Boss.NONE) {
/* 126 */       event.setCanceled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onRenderPost(RenderGameOverlayEvent.Post event) {
/* 132 */     if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && this.boss.getValue() != Boss.NONE) {
/* 133 */       if (this.boss.getValue() == Boss.MINIMIZE) {
/* 134 */         Map<UUID, BossInfoClient> map = (mc.field_71456_v.func_184046_j()).field_184060_g;
/* 135 */         if (map == null) {
/*     */           return;
/*     */         }
/* 138 */         ScaledResolution scaledresolution = new ScaledResolution(mc);
/* 139 */         int i = scaledresolution.func_78326_a();
/* 140 */         int j = 12;
/* 141 */         for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
/* 142 */           BossInfoClient info = entry.getValue();
/* 143 */           String text = info.func_186744_e().func_150254_d();
/* 144 */           int k = (int)(i / ((Float)this.scale.getValue()).floatValue() / 2.0F - 91.0F);
/* 145 */           GL11.glScaled(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), 1.0D);
/* 146 */           if (!event.isCanceled()) {
/* 147 */             GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/* 148 */             mc.func_110434_K().func_110577_a(GuiBossOverlay.field_184058_a);
/* 149 */             mc.field_71456_v.func_184046_j().func_184052_a(k, j, (BossInfo)info);
/* 150 */             mc.field_71466_p.func_175063_a(text, i / ((Float)this.scale.getValue()).floatValue() / 2.0F - (mc.field_71466_p.func_78256_a(text) / 2), (j - 9), 16777215);
/*     */           } 
/* 152 */           GL11.glScaled(1.0D / ((Float)this.scale.getValue()).floatValue(), 1.0D / ((Float)this.scale.getValue()).floatValue(), 1.0D);
/* 153 */           j += 10 + mc.field_71466_p.field_78288_b;
/*     */         } 
/* 155 */       } else if (this.boss.getValue() == Boss.STACK) {
/* 156 */         Map<UUID, BossInfoClient> map = (mc.field_71456_v.func_184046_j()).field_184060_g;
/* 157 */         HashMap<String, Pair<BossInfoClient, Integer>> to = new HashMap<>();
/* 158 */         for (Map.Entry<UUID, BossInfoClient> entry2 : map.entrySet()) {
/* 159 */           String s = ((BossInfoClient)entry2.getValue()).func_186744_e().func_150254_d();
/* 160 */           if (to.containsKey(s)) {
/* 161 */             Pair<BossInfoClient, Integer> pair = to.get(s);
/* 162 */             pair = new Pair<>(pair.getKey(), Integer.valueOf(((Integer)pair.getValue()).intValue() + 1));
/* 163 */             to.put(s, pair); continue;
/*     */           } 
/* 165 */           Pair<BossInfoClient, Integer> p = new Pair<>(entry2.getValue(), Integer.valueOf(1));
/* 166 */           to.put(s, p);
/*     */         } 
/*     */         
/* 169 */         ScaledResolution scaledresolution2 = new ScaledResolution(mc);
/* 170 */         int l = scaledresolution2.func_78326_a();
/* 171 */         int m = 12;
/* 172 */         for (Map.Entry<String, Pair<BossInfoClient, Integer>> entry3 : to.entrySet()) {
/* 173 */           String text = entry3.getKey();
/* 174 */           BossInfoClient info2 = (BossInfoClient)((Pair)entry3.getValue()).getKey();
/* 175 */           int a = ((Integer)((Pair)entry3.getValue()).getValue()).intValue();
/* 176 */           text = text + " x" + a;
/* 177 */           int k2 = (int)(l / ((Float)this.scale.getValue()).floatValue() / 2.0F - 91.0F);
/* 178 */           GL11.glScaled(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), 1.0D);
/* 179 */           if (!event.isCanceled()) {
/* 180 */             GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/* 181 */             mc.func_110434_K().func_110577_a(GuiBossOverlay.field_184058_a);
/* 182 */             mc.field_71456_v.func_184046_j().func_184052_a(k2, m, (BossInfo)info2);
/* 183 */             mc.field_71466_p.func_175063_a(text, l / ((Float)this.scale.getValue()).floatValue() / 2.0F - (mc.field_71466_p.func_78256_a(text) / 2), (m - 9), 16777215);
/*     */           } 
/* 185 */           GL11.glScaled(1.0D / ((Float)this.scale.getValue()).floatValue(), 1.0D / ((Float)this.scale.getValue()).floatValue(), 1.0D);
/* 186 */           m += 10 + mc.field_71466_p.field_78288_b;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onRenderLiving(RenderLivingEvent.Pre<?> event) {
/* 194 */     if (((Boolean)this.bats.getValue()).booleanValue() && event.getEntity() instanceof net.minecraft.entity.passive.EntityBat) {
/* 195 */       event.setCanceled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPlaySound(PlaySoundAtEntityEvent event) {
/* 201 */     if ((((Boolean)this.bats.getValue()).booleanValue() && event.getSound().equals(SoundEvents.field_187740_w)) || event.getSound().equals(SoundEvents.field_187742_x) || event.getSound().equals(SoundEvents.field_187743_y) || event.getSound().equals(SoundEvents.field_189108_z) || event.getSound().equals(SoundEvents.field_187744_z)) {
/* 202 */       event.setVolume(0.0F);
/* 203 */       event.setPitch(0.0F);
/* 204 */       event.setCanceled(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   public enum Skylight {
/* 209 */     NONE,
/* 210 */     WORLD,
/* 211 */     ENTITY,
/* 212 */     ALL;
/*     */   }
/*     */   
/*     */   public enum Fog {
/* 216 */     NONE,
/* 217 */     AIR,
/* 218 */     NOFOG;
/*     */   }
/*     */   
/*     */   public enum Boss {
/* 222 */     NONE,
/* 223 */     REMOVE,
/* 224 */     STACK,
/* 225 */     MINIMIZE;
/*     */   }
/*     */   
/*     */   public enum NoArmor {
/* 229 */     NONE,
/* 230 */     ALL,
/* 231 */     HELMET;
/*     */   }
/*     */   
/*     */   public static class Pair<T, S> {
/*     */     private T key;
/*     */     private S value;
/*     */     
/*     */     public Pair(T key, S value) {
/* 239 */       this.key = key;
/* 240 */       this.value = value;
/*     */     }
/*     */     
/*     */     public T getKey() {
/* 244 */       return this.key;
/*     */     }
/*     */     
/*     */     public void setKey(T key) {
/* 248 */       this.key = key;
/*     */     }
/*     */     
/*     */     public S getValue() {
/* 252 */       return this.value;
/*     */     }
/*     */     
/*     */     public void setValue(S value) {
/* 256 */       this.value = value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\render\NoRender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */