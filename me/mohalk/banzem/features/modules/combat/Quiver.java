/*     */ package me.mohalk.banzem.features.modules.combat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.InventoryUtil;
/*     */ import me.mohalk.banzem.util.RotationUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.init.PotionTypes;
/*     */ import net.minecraft.inventory.ClickType;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemBow;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.CPacketPlayerDigging;
/*     */ import net.minecraft.potion.PotionUtils;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ 
/*     */ public class Quiver extends Module {
/*  25 */   private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(500)));
/*  26 */   private final Setting<Integer> holdLength = register(new Setting("Hold Length", Integer.valueOf(350), Integer.valueOf(100), Integer.valueOf(1000)));
/*  27 */   private final Setting<mainEnum> main = register(new Setting("Main", mainEnum.SPEED));
/*  28 */   private final Setting<mainEnum> secondary = register(new Setting("Secondary", mainEnum.STRENGTH));
/*  29 */   private final Timer delayTimer = new Timer();
/*  30 */   private final Timer holdTimer = new Timer();
/*     */   private int stage;
/*     */   private ArrayList<Integer> map;
/*  33 */   private int strSlot = -1;
/*  34 */   private int speedSlot = -1;
/*  35 */   private int oldSlot = 1;
/*     */ 
/*     */   
/*     */   public Quiver() {
/*  39 */     super("Quiver", "Automatically shoots yourself with good effects.", Module.Category.COMBAT, true, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  45 */     if (nullCheck())
/*  46 */       return;  InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
/*  47 */     clean();
/*  48 */     this.oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
/*  49 */     mc.field_71474_y.field_74313_G.field_74513_e = false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  55 */     if (nullCheck())
/*  56 */       return;  InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
/*  57 */     mc.field_71474_y.field_74313_G.field_74513_e = false;
/*  58 */     clean();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/*  64 */     if (nullCheck())
/*  65 */       return;  if (mc.field_71462_r != null)
/*     */       return; 
/*  67 */     if (InventoryUtil.findItemInventorySlot((Item)Items.field_151031_f, true) == -1) {
/*  68 */       Command.sendMessage("Couldn't find bow in inventory! Toggling!");
/*  69 */       toggle();
/*     */     } 
/*     */     
/*  72 */     RotationUtil.faceVector(EntityUtil.getInterpolatedPos((Entity)mc.field_71439_g, mc.field_71428_T.field_194148_c).func_72441_c(0.0D, 3.0D, 0.0D), false);
/*     */     
/*  74 */     if (this.stage == 0) {
/*  75 */       this.map = mapArrows();
/*  76 */       for (Iterator<Integer> iterator = this.map.iterator(); iterator.hasNext(); ) { int a = ((Integer)iterator.next()).intValue();
/*  77 */         ItemStack arrow = (ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(a);
/*  78 */         if ((PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185223_F) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185225_H) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185224_G)) && 
/*  79 */           this.strSlot == -1) {
/*  80 */           this.strSlot = a;
/*     */         }
/*     */         
/*  83 */         if ((PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185243_o) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185244_p) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185245_q)) && 
/*  84 */           this.speedSlot == -1) {
/*  85 */           this.speedSlot = a;
/*     */         } }
/*     */ 
/*     */       
/*  89 */       this.stage++;
/*  90 */     } else if (this.stage == 1) {
/*  91 */       if (!this.delayTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
/*  92 */         return;  this.delayTimer.reset();
/*  93 */       this.stage++;
/*  94 */     } else if (this.stage == 2) {
/*  95 */       switchTo((Enum<mainEnum>)this.main.getValue());
/*  96 */       this.stage++;
/*  97 */     } else if (this.stage == 3) {
/*  98 */       if (!this.delayTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
/*  99 */         return;  this.delayTimer.reset();
/* 100 */       this.stage++;
/* 101 */     } else if (this.stage == 4) {
/* 102 */       mc.field_71474_y.field_74313_G.field_74513_e = true;
/* 103 */       this.holdTimer.reset();
/* 104 */       this.stage++;
/* 105 */     } else if (this.stage == 5) {
/* 106 */       if (!this.holdTimer.passedMs(((Integer)this.holdLength.getValue()).intValue()))
/* 107 */         return;  this.holdTimer.reset();
/* 108 */       this.stage++;
/* 109 */     } else if (this.stage == 6) {
/* 110 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, mc.field_71439_g.func_174811_aO()));
/* 111 */       mc.field_71439_g.func_184602_cy();
/* 112 */       mc.field_71474_y.field_74313_G.field_74513_e = false;
/* 113 */       this.stage++;
/* 114 */     } else if (this.stage == 7) {
/* 115 */       if (!this.delayTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
/* 116 */         return;  this.delayTimer.reset();
/* 117 */       this.stage++;
/* 118 */     } else if (this.stage == 8) {
/* 119 */       this.map = mapArrows();
/* 120 */       this.strSlot = -1;
/* 121 */       this.speedSlot = -1;
/* 122 */       for (Iterator<Integer> iterator = this.map.iterator(); iterator.hasNext(); ) { int a = ((Integer)iterator.next()).intValue();
/* 123 */         ItemStack arrow = (ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(a);
/* 124 */         if ((PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185223_F) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185225_H) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185224_G)) && 
/* 125 */           this.strSlot == -1) {
/* 126 */           this.strSlot = a;
/*     */         }
/* 128 */         if ((PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185243_o) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185244_p) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185245_q)) && 
/* 129 */           this.speedSlot == -1) {
/* 130 */           this.speedSlot = a;
/*     */         } }
/*     */       
/* 133 */       this.stage++;
/*     */     } 
/* 135 */     if (this.stage == 9) {
/* 136 */       switchTo((Enum<mainEnum>)this.secondary.getValue());
/* 137 */       this.stage++;
/* 138 */     } else if (this.stage == 10) {
/* 139 */       if (!this.delayTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
/* 140 */         return;  this.stage++;
/* 141 */     } else if (this.stage == 11) {
/* 142 */       mc.field_71474_y.field_74313_G.field_74513_e = true;
/* 143 */       this.holdTimer.reset();
/* 144 */       this.stage++;
/* 145 */     } else if (this.stage == 12) {
/* 146 */       if (!this.holdTimer.passedMs(((Integer)this.holdLength.getValue()).intValue()))
/* 147 */         return;  this.holdTimer.reset();
/* 148 */       this.stage++;
/* 149 */     } else if (this.stage == 13) {
/* 150 */       mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, mc.field_71439_g.func_174811_aO()));
/* 151 */       mc.field_71439_g.func_184602_cy();
/* 152 */       mc.field_71474_y.field_74313_G.field_74513_e = false;
/* 153 */       this.stage++;
/* 154 */     } else if (this.stage == 14) {
/* 155 */       ArrayList<Integer> map = mapEmpty();
/* 156 */       if (!map.isEmpty()) {
/* 157 */         int a = ((Integer)map.get(0)).intValue();
/* 158 */         mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, a, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/*     */       } 
/* 160 */       this.stage++;
/* 161 */     } else if (this.stage == 15) {
/* 162 */       setEnabled(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void switchTo(Enum<mainEnum> mode) {
/* 168 */     if (mode.toString().equalsIgnoreCase("STRENGTH") && 
/* 169 */       this.strSlot != -1) {
/* 170 */       switchTo(this.strSlot);
/*     */     }
/*     */     
/* 173 */     if (mode.toString().equalsIgnoreCase("SPEED") && 
/* 174 */       this.speedSlot != -1) {
/* 175 */       switchTo(this.speedSlot);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ArrayList<Integer> mapArrows() {
/* 182 */     ArrayList<Integer> map = new ArrayList<>();
/* 183 */     for (int a = 9; a < 45; a++) {
/* 184 */       if (((ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(a)).func_77973_b() instanceof net.minecraft.item.ItemTippedArrow) {
/* 185 */         ItemStack arrow = (ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(a);
/* 186 */         if (PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185223_F) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185225_H) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185224_G)) {
/* 187 */           map.add(Integer.valueOf(a));
/*     */         }
/* 189 */         if (PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185243_o) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185244_p) || PotionUtils.func_185191_c(arrow).equals(PotionTypes.field_185245_q)) {
/* 190 */           map.add(Integer.valueOf(a));
/*     */         }
/*     */       } 
/*     */     } 
/* 194 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   private ArrayList<Integer> mapEmpty() {
/* 199 */     ArrayList<Integer> map = new ArrayList<>();
/* 200 */     for (int a = 9; a < 45; a++) {
/* 201 */       if (((ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(a)).func_77973_b() instanceof net.minecraft.item.ItemAir || mc.field_71439_g.field_71069_bz.func_75138_a().get(a) == ItemStack.field_190927_a) {
/* 202 */         map.add(Integer.valueOf(a));
/*     */       }
/*     */     } 
/* 205 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   private void switchTo(int from) {
/* 210 */     if (from == 9)
/* 211 */       return;  mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, from, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 212 */     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 9, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 213 */     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, from, 0, ClickType.PICKUP, (EntityPlayer)mc.field_71439_g);
/* 214 */     mc.field_71442_b.func_78765_e();
/*     */   }
/*     */ 
/*     */   
/*     */   private void clean() {
/* 219 */     this.holdTimer.reset();
/* 220 */     this.delayTimer.reset();
/* 221 */     this.map = null;
/* 222 */     this.speedSlot = -1;
/* 223 */     this.strSlot = -1;
/* 224 */     this.stage = 0;
/*     */   }
/*     */   
/*     */   private enum mainEnum
/*     */   {
/* 229 */     STRENGTH, SPEED;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\combat\Quiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */