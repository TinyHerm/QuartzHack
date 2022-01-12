/*     */ package me.mohalk.banzem.manager;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.modules.client.Managers;
/*     */ import me.mohalk.banzem.features.modules.combat.HoleFiller;
/*     */ import me.mohalk.banzem.features.modules.render.HoleESP;
/*     */ import me.mohalk.banzem.util.BlockUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Blocks;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3i;
/*     */ 
/*     */ public class HoleManager
/*     */   extends Feature
/*     */   implements Runnable {
/*  26 */   private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true, true));
/*  27 */   private final List<BlockPos> midSafety = new ArrayList<>();
/*  28 */   private final Timer syncTimer = new Timer();
/*  29 */   private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
/*  30 */   private final Timer holeTimer = new Timer();
/*  31 */   private List<BlockPos> holes = new ArrayList<>();
/*     */   private ScheduledExecutorService executorService;
/*  33 */   private int lastUpdates = 0;
/*     */   private Thread thread;
/*     */   
/*     */   public void update() {
/*  37 */     if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.WHILE) {
/*  38 */       if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncTimer.passedMs(((Integer)(Managers.getInstance()).holeSync.getValue()).intValue())) {
/*  39 */         if (this.thread == null) {
/*  40 */           this.thread = new Thread(this);
/*  41 */         } else if (this.syncTimer.passedMs(((Integer)(Managers.getInstance()).holeSync.getValue()).intValue()) && !this.shouldInterrupt.get()) {
/*  42 */           this.shouldInterrupt.set(true);
/*  43 */           this.syncTimer.reset();
/*     */           return;
/*     */         } 
/*  46 */         if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
/*  47 */           this.thread = new Thread(this);
/*     */         }
/*  49 */         if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
/*     */           try {
/*  51 */             this.thread.start();
/*     */           }
/*  53 */           catch (Exception e) {
/*  54 */             e.printStackTrace();
/*     */           } 
/*  56 */           this.syncTimer.reset();
/*     */         } 
/*     */       } 
/*  59 */     } else if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.WHILE) {
/*  60 */       if (this.executorService == null || this.executorService.isTerminated() || this.executorService.isShutdown() || this.syncTimer.passedMs(10000L) || this.lastUpdates != ((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue()) {
/*  61 */         this.lastUpdates = ((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue();
/*  62 */         if (this.executorService != null) {
/*  63 */           this.executorService.shutdown();
/*     */         }
/*  65 */         this.executorService = getExecutor();
/*     */       } 
/*  67 */     } else if (this.holeTimer.passedMs(((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue()) && !fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn())) {
/*  68 */       this.holes = calcHoles();
/*  69 */       this.holeTimer.reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void settingChanged() {
/*  74 */     if (this.executorService != null) {
/*  75 */       this.executorService.shutdown();
/*     */     }
/*  77 */     if (this.thread != null) {
/*  78 */       this.shouldInterrupt.set(true);
/*     */     }
/*     */   }
/*     */   
/*     */   private ScheduledExecutorService getExecutor() {
/*  83 */     ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
/*  84 */     service.scheduleAtFixedRate(this, 0L, ((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue(), TimeUnit.MILLISECONDS);
/*  85 */     return service;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*  90 */     if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.WHILE) {
/*     */       while (true) {
/*  92 */         if (this.shouldInterrupt.get()) {
/*  93 */           this.shouldInterrupt.set(false);
/*  94 */           this.syncTimer.reset();
/*  95 */           Thread.currentThread().interrupt();
/*     */           return;
/*     */         } 
/*  98 */         if (!fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn())) {
/*  99 */           this.holes = calcHoles();
/*     */         }
/*     */         try {
/* 102 */           Thread.sleep(((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue());
/*     */         }
/* 104 */         catch (InterruptedException e) {
/* 105 */           this.thread.interrupt();
/* 106 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/*     */     }
/* 110 */     if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.POOL && !fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn())) {
/* 111 */       this.holes = calcHoles();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<BlockPos> getHoles() {
/* 116 */     return this.holes;
/*     */   }
/*     */   
/*     */   public List<BlockPos> getMidSafety() {
/* 120 */     return this.midSafety;
/*     */   }
/*     */   
/*     */   public List<BlockPos> getSortedHoles() {
/* 124 */     this.holes.sort(Comparator.comparingDouble(hole -> mc.field_71439_g.func_174818_b(hole)));
/* 125 */     return getHoles();
/*     */   }
/*     */   
/*     */   public List<BlockPos> calcHoles() {
/* 129 */     ArrayList<BlockPos> safeSpots = new ArrayList<>();
/* 130 */     this.midSafety.clear();
/* 131 */     List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.field_71439_g), ((Float)(Managers.getInstance()).holeRange.getValue()).floatValue(), ((Float)(Managers.getInstance()).holeRange.getValue()).intValue(), false, true, 0);
/* 132 */     for (BlockPos pos : positions) {
/* 133 */       if (!mc.field_71441_e.func_180495_p(pos).func_177230_c().equals(Blocks.field_150350_a) || !mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c().equals(Blocks.field_150350_a) || !mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c().equals(Blocks.field_150350_a))
/* 134 */         continue;  boolean isSafe = true;
/* 135 */       boolean midSafe = true;
/* 136 */       for (BlockPos offset : surroundOffset) {
/* 137 */         Block block = mc.field_71441_e.func_180495_p(pos.func_177971_a((Vec3i)offset)).func_177230_c();
/* 138 */         if (BlockUtil.isBlockUnSolid(block)) {
/* 139 */           midSafe = false;
/*     */         }
/* 141 */         if (block != Blocks.field_150357_h && block != Blocks.field_150343_Z && block != Blocks.field_150477_bB && block != Blocks.field_150467_bQ)
/* 142 */           isSafe = false; 
/*     */       } 
/* 144 */       if (isSafe) {
/* 145 */         safeSpots.add(pos);
/*     */       }
/* 147 */       if (!midSafe)
/* 148 */         continue;  this.midSafety.add(pos);
/*     */     } 
/* 150 */     return safeSpots;
/*     */   }
/*     */   
/*     */   public boolean isSafe(BlockPos pos) {
/* 154 */     boolean isSafe = true; BlockPos[] arrayOfBlockPos; int i; byte b;
/* 155 */     for (arrayOfBlockPos = surroundOffset, i = arrayOfBlockPos.length, b = 0; b < i; ) { BlockPos offset = arrayOfBlockPos[b];
/* 156 */       Block block = mc.field_71441_e.func_180495_p(pos.func_177971_a((Vec3i)offset)).func_177230_c();
/* 157 */       if (block == Blocks.field_150357_h) { b++; continue; }
/* 158 */        isSafe = false; }
/*     */ 
/*     */     
/* 161 */     return isSafe;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\HoleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */