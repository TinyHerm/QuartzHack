/*     */ package me.mohalk.banzem.util;
/*     */ import net.minecraft.enchantment.EnchantmentHelper;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.SharedMonsterAttributes;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.init.MobEffects;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.potion.PotionEffect;
/*     */ import net.minecraft.util.CombatRules;
/*     */ import net.minecraft.util.DamageSource;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.MathHelper;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ import net.minecraft.world.Explosion;
/*     */ import net.minecraft.world.World;
/*     */ 
/*     */ public class DamageUtil implements Util {
/*     */   public static boolean isArmorLow(EntityPlayer player, int durability) {
/*  22 */     for (ItemStack piece : player.field_71071_by.field_70460_b) {
/*  23 */       if (piece == null) {
/*  24 */         return true;
/*     */       }
/*  26 */       if (getItemDamage(piece) >= durability)
/*  27 */         continue;  return true;
/*     */     } 
/*  29 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isNaked(EntityPlayer player) {
/*  33 */     for (ItemStack piece : player.field_71071_by.field_70460_b) {
/*  34 */       if (piece == null || piece.func_190926_b())
/*  35 */         continue;  return false;
/*     */     } 
/*  37 */     return true;
/*     */   }
/*     */   
/*     */   public static int getItemDamage(ItemStack stack) {
/*  41 */     return stack.func_77958_k() - stack.func_77952_i();
/*     */   }
/*     */   
/*     */   public static float getDamageInPercent(ItemStack stack) {
/*  45 */     return getItemDamage(stack) / stack.func_77958_k() * 100.0F;
/*     */   }
/*     */   
/*     */   public static int getRoundedDamage(ItemStack stack) {
/*  49 */     return (int)getDamageInPercent(stack);
/*     */   }
/*     */   
/*     */   public static boolean hasDurability(ItemStack stack) {
/*  53 */     Item item = stack.func_77973_b();
/*  54 */     return (item instanceof net.minecraft.item.ItemArmor || item instanceof net.minecraft.item.ItemSword || item instanceof net.minecraft.item.ItemTool || item instanceof net.minecraft.item.ItemShield);
/*     */   }
/*     */   
/*     */   public static boolean canBreakWeakness(EntityPlayer player) {
/*  58 */     int strengthAmp = 0;
/*  59 */     PotionEffect effect = mc.field_71439_g.func_70660_b(MobEffects.field_76420_g);
/*  60 */     if (effect != null) {
/*  61 */       strengthAmp = effect.func_76458_c();
/*     */     }
/*  63 */     return (!mc.field_71439_g.func_70644_a(MobEffects.field_76437_t) || strengthAmp >= 1 || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemSword || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemPickaxe || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemAxe || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof net.minecraft.item.ItemSpade);
/*     */   }
/*     */   
/*     */   public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
/*  67 */     float doubleExplosionSize = 12.0F;
/*  68 */     double distancedsize = entity.func_70011_f(posX, posY, posZ) / doubleExplosionSize;
/*  69 */     Vec3d vec3d = new Vec3d(posX, posY, posZ);
/*  70 */     double blockDensity = 0.0D;
/*     */     try {
/*  72 */       blockDensity = entity.field_70170_p.func_72842_a(vec3d, entity.func_174813_aQ());
/*  73 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/*  76 */     double v = (1.0D - distancedsize) * blockDensity;
/*  77 */     float damage = (int)((v * v + v) / 2.0D * 7.0D * doubleExplosionSize + 1.0D);
/*  78 */     double finald = 1.0D;
/*  79 */     if (entity instanceof EntityLivingBase) {
/*  80 */       finald = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion((World)mc.field_71441_e, null, posX, posY, posZ, 6.0F, false, true));
/*     */     }
/*  82 */     return (float)finald;
/*     */   }
/*     */   
/*     */   public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
/*  86 */     float damage = damageI;
/*  87 */     if (entity instanceof EntityPlayer) {
/*  88 */       EntityPlayer ep = (EntityPlayer)entity;
/*  89 */       DamageSource ds = DamageSource.func_94539_a(explosion);
/*  90 */       damage = CombatRules.func_189427_a(damage, ep.func_70658_aO(), (float)ep.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
/*  91 */       int k = 0;
/*     */       try {
/*  93 */         k = EnchantmentHelper.func_77508_a(ep.func_184193_aE(), ds);
/*  94 */       } catch (Exception exception) {}
/*     */ 
/*     */       
/*  97 */       float f = MathHelper.func_76131_a(k, 0.0F, 20.0F);
/*  98 */       damage *= 1.0F - f / 25.0F;
/*  99 */       if (entity.func_70644_a(MobEffects.field_76429_m)) {
/* 100 */         damage -= damage / 4.0F;
/*     */       }
/* 102 */       damage = Math.max(damage, 0.0F);
/* 103 */       return damage;
/*     */     } 
/* 105 */     damage = CombatRules.func_189427_a(damage, entity.func_70658_aO(), (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
/* 106 */     return damage;
/*     */   }
/*     */   
/*     */   public static float getDamageMultiplied(float damage) {
/* 110 */     int diff = mc.field_71441_e.func_175659_aa().func_151525_a();
/* 111 */     return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
/*     */   }
/*     */   
/*     */   public static float calculateDamage(Entity crystal, Entity entity) {
/* 115 */     return calculateDamage(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, entity);
/*     */   }
/*     */   
/*     */   public static float calculateDamage(BlockPos pos, Entity entity) {
/* 119 */     return calculateDamage(pos.func_177958_n() + 0.5D, (pos.func_177956_o() + 1), pos.func_177952_p() + 0.5D, entity);
/*     */   }
/*     */   
/*     */   public static boolean canTakeDamage(boolean suicide) {
/* 123 */     return (!mc.field_71439_g.field_71075_bZ.field_75098_d && !suicide);
/*     */   }
/*     */   
/*     */   public static int getCooldownByWeapon(EntityPlayer player) {
/* 127 */     Item item = player.func_184614_ca().func_77973_b();
/* 128 */     if (item instanceof net.minecraft.item.ItemSword) {
/* 129 */       return 600;
/*     */     }
/* 131 */     if (item instanceof net.minecraft.item.ItemPickaxe) {
/* 132 */       return 850;
/*     */     }
/* 134 */     if (item == Items.field_151036_c) {
/* 135 */       return 1100;
/*     */     }
/* 137 */     if (item == Items.field_151018_J) {
/* 138 */       return 500;
/*     */     }
/* 140 */     if (item == Items.field_151019_K) {
/* 141 */       return 350;
/*     */     }
/* 143 */     if (item == Items.field_151053_p || item == Items.field_151049_t) {
/* 144 */       return 1250;
/*     */     }
/* 146 */     if (item instanceof net.minecraft.item.ItemSpade || item == Items.field_151006_E || item == Items.field_151056_x || item == Items.field_151017_I || item == Items.field_151013_M) {
/* 147 */       return 1000;
/*     */     }
/* 149 */     return 250;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banze\\util\DamageUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */