/*     */ package me.mohalk.banzem.features.modules.movement;
/*     */ 
/*     */ import com.google.common.eventbus.Subscribe;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.Vec3d;
/*     */ 
/*     */ 
/*     */ public class Anchor
/*     */   extends Module
/*     */ {
/*     */   public static boolean Anchoring;
/*  14 */   private final Setting<Integer> pitch = register(new Setting("Pitch", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(90)));
/*  15 */   private final Setting<Boolean> pull = register(new Setting("Pull", Boolean.valueOf(true)));
/*     */   
/*     */   int holeblocks;
/*     */   
/*     */   public Anchor() {
/*  20 */     super("Anchor", "mohalks favorite module.", Module.Category.MOVEMENT, false, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBlockHole(BlockPos blockPos) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: iconst_0
/*     */     //   2: putfield holeblocks : I
/*     */     //   5: aload_0
/*     */     //   6: pop
/*     */     //   7: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   10: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   13: aload_1
/*     */     //   14: iconst_0
/*     */     //   15: iconst_3
/*     */     //   16: iconst_0
/*     */     //   17: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   20: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   23: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   28: getstatic net/minecraft/init/Blocks.field_150350_a : Lnet/minecraft/block/Block;
/*     */     //   31: if_acmpne -> 44
/*     */     //   34: aload_0
/*     */     //   35: dup
/*     */     //   36: getfield holeblocks : I
/*     */     //   39: iconst_1
/*     */     //   40: iadd
/*     */     //   41: putfield holeblocks : I
/*     */     //   44: aload_0
/*     */     //   45: pop
/*     */     //   46: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   49: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   52: aload_1
/*     */     //   53: iconst_0
/*     */     //   54: iconst_2
/*     */     //   55: iconst_0
/*     */     //   56: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   59: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   62: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   67: getstatic net/minecraft/init/Blocks.field_150350_a : Lnet/minecraft/block/Block;
/*     */     //   70: if_acmpne -> 83
/*     */     //   73: aload_0
/*     */     //   74: dup
/*     */     //   75: getfield holeblocks : I
/*     */     //   78: iconst_1
/*     */     //   79: iadd
/*     */     //   80: putfield holeblocks : I
/*     */     //   83: aload_0
/*     */     //   84: pop
/*     */     //   85: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   88: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   91: aload_1
/*     */     //   92: iconst_0
/*     */     //   93: iconst_1
/*     */     //   94: iconst_0
/*     */     //   95: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   98: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   101: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   106: getstatic net/minecraft/init/Blocks.field_150350_a : Lnet/minecraft/block/Block;
/*     */     //   109: if_acmpne -> 122
/*     */     //   112: aload_0
/*     */     //   113: dup
/*     */     //   114: getfield holeblocks : I
/*     */     //   117: iconst_1
/*     */     //   118: iadd
/*     */     //   119: putfield holeblocks : I
/*     */     //   122: aload_0
/*     */     //   123: pop
/*     */     //   124: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   127: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   130: aload_1
/*     */     //   131: iconst_0
/*     */     //   132: iconst_0
/*     */     //   133: iconst_0
/*     */     //   134: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   137: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   140: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   145: getstatic net/minecraft/init/Blocks.field_150350_a : Lnet/minecraft/block/Block;
/*     */     //   148: if_acmpne -> 161
/*     */     //   151: aload_0
/*     */     //   152: dup
/*     */     //   153: getfield holeblocks : I
/*     */     //   156: iconst_1
/*     */     //   157: iadd
/*     */     //   158: putfield holeblocks : I
/*     */     //   161: aload_0
/*     */     //   162: pop
/*     */     //   163: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   166: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   169: aload_1
/*     */     //   170: iconst_0
/*     */     //   171: iconst_m1
/*     */     //   172: iconst_0
/*     */     //   173: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   176: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   179: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   184: getstatic net/minecraft/init/Blocks.field_150343_Z : Lnet/minecraft/block/Block;
/*     */     //   187: if_acmpeq -> 219
/*     */     //   190: aload_0
/*     */     //   191: pop
/*     */     //   192: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   195: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   198: aload_1
/*     */     //   199: iconst_0
/*     */     //   200: iconst_m1
/*     */     //   201: iconst_0
/*     */     //   202: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   205: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   208: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   213: getstatic net/minecraft/init/Blocks.field_150357_h : Lnet/minecraft/block/Block;
/*     */     //   216: if_acmpne -> 229
/*     */     //   219: aload_0
/*     */     //   220: dup
/*     */     //   221: getfield holeblocks : I
/*     */     //   224: iconst_1
/*     */     //   225: iadd
/*     */     //   226: putfield holeblocks : I
/*     */     //   229: aload_0
/*     */     //   230: pop
/*     */     //   231: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   234: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   237: aload_1
/*     */     //   238: iconst_1
/*     */     //   239: iconst_0
/*     */     //   240: iconst_0
/*     */     //   241: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   244: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   247: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   252: getstatic net/minecraft/init/Blocks.field_150343_Z : Lnet/minecraft/block/Block;
/*     */     //   255: if_acmpeq -> 287
/*     */     //   258: aload_0
/*     */     //   259: pop
/*     */     //   260: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   263: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   266: aload_1
/*     */     //   267: iconst_1
/*     */     //   268: iconst_0
/*     */     //   269: iconst_0
/*     */     //   270: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   273: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   276: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   281: getstatic net/minecraft/init/Blocks.field_150357_h : Lnet/minecraft/block/Block;
/*     */     //   284: if_acmpne -> 297
/*     */     //   287: aload_0
/*     */     //   288: dup
/*     */     //   289: getfield holeblocks : I
/*     */     //   292: iconst_1
/*     */     //   293: iadd
/*     */     //   294: putfield holeblocks : I
/*     */     //   297: aload_0
/*     */     //   298: pop
/*     */     //   299: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   302: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   305: aload_1
/*     */     //   306: iconst_m1
/*     */     //   307: iconst_0
/*     */     //   308: iconst_0
/*     */     //   309: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   312: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   315: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   320: getstatic net/minecraft/init/Blocks.field_150343_Z : Lnet/minecraft/block/Block;
/*     */     //   323: if_acmpeq -> 355
/*     */     //   326: aload_0
/*     */     //   327: pop
/*     */     //   328: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   331: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   334: aload_1
/*     */     //   335: iconst_m1
/*     */     //   336: iconst_0
/*     */     //   337: iconst_0
/*     */     //   338: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   341: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   344: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   349: getstatic net/minecraft/init/Blocks.field_150357_h : Lnet/minecraft/block/Block;
/*     */     //   352: if_acmpne -> 365
/*     */     //   355: aload_0
/*     */     //   356: dup
/*     */     //   357: getfield holeblocks : I
/*     */     //   360: iconst_1
/*     */     //   361: iadd
/*     */     //   362: putfield holeblocks : I
/*     */     //   365: aload_0
/*     */     //   366: pop
/*     */     //   367: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   370: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   373: aload_1
/*     */     //   374: iconst_0
/*     */     //   375: iconst_0
/*     */     //   376: iconst_1
/*     */     //   377: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   380: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   383: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   388: getstatic net/minecraft/init/Blocks.field_150343_Z : Lnet/minecraft/block/Block;
/*     */     //   391: if_acmpeq -> 423
/*     */     //   394: aload_0
/*     */     //   395: pop
/*     */     //   396: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   399: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   402: aload_1
/*     */     //   403: iconst_0
/*     */     //   404: iconst_0
/*     */     //   405: iconst_1
/*     */     //   406: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   409: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   412: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   417: getstatic net/minecraft/init/Blocks.field_150357_h : Lnet/minecraft/block/Block;
/*     */     //   420: if_acmpne -> 433
/*     */     //   423: aload_0
/*     */     //   424: dup
/*     */     //   425: getfield holeblocks : I
/*     */     //   428: iconst_1
/*     */     //   429: iadd
/*     */     //   430: putfield holeblocks : I
/*     */     //   433: aload_0
/*     */     //   434: pop
/*     */     //   435: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   438: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   441: aload_1
/*     */     //   442: iconst_0
/*     */     //   443: iconst_0
/*     */     //   444: iconst_m1
/*     */     //   445: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   448: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   451: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   456: getstatic net/minecraft/init/Blocks.field_150343_Z : Lnet/minecraft/block/Block;
/*     */     //   459: if_acmpeq -> 491
/*     */     //   462: aload_0
/*     */     //   463: pop
/*     */     //   464: getstatic me/mohalk/banzem/features/modules/movement/Anchor.mc : Lnet/minecraft/client/Minecraft;
/*     */     //   467: getfield field_71441_e : Lnet/minecraft/client/multiplayer/WorldClient;
/*     */     //   470: aload_1
/*     */     //   471: iconst_0
/*     */     //   472: iconst_0
/*     */     //   473: iconst_m1
/*     */     //   474: invokevirtual func_177982_a : (III)Lnet/minecraft/util/math/BlockPos;
/*     */     //   477: invokevirtual func_180495_p : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
/*     */     //   480: invokeinterface func_177230_c : ()Lnet/minecraft/block/Block;
/*     */     //   485: getstatic net/minecraft/init/Blocks.field_150357_h : Lnet/minecraft/block/Block;
/*     */     //   488: if_acmpne -> 501
/*     */     //   491: aload_0
/*     */     //   492: dup
/*     */     //   493: getfield holeblocks : I
/*     */     //   496: iconst_1
/*     */     //   497: iadd
/*     */     //   498: putfield holeblocks : I
/*     */     //   501: aload_0
/*     */     //   502: getfield holeblocks : I
/*     */     //   505: bipush #9
/*     */     //   507: if_icmplt -> 514
/*     */     //   510: iconst_1
/*     */     //   511: goto -> 515
/*     */     //   514: iconst_0
/*     */     //   515: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #25	-> 0
/*     */     //   #26	-> 5
/*     */     //   #27	-> 34
/*     */     //   #29	-> 44
/*     */     //   #30	-> 73
/*     */     //   #32	-> 83
/*     */     //   #33	-> 112
/*     */     //   #35	-> 122
/*     */     //   #36	-> 151
/*     */     //   #38	-> 161
/*     */     //   #39	-> 219
/*     */     //   #41	-> 229
/*     */     //   #42	-> 287
/*     */     //   #44	-> 297
/*     */     //   #45	-> 355
/*     */     //   #47	-> 365
/*     */     //   #48	-> 423
/*     */     //   #50	-> 433
/*     */     //   #51	-> 491
/*     */     //   #53	-> 501
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	516	0	this	Lme/mohalk/banzem/features/modules/movement/Anchor;
/*     */     //   0	516	1	blockPos	Lnet/minecraft/util/math/BlockPos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vec3d GetCenter(double d, double d2, double d3) {
/*  58 */     double d4 = Math.floor(d) + 0.5D;
/*  59 */     double d5 = Math.floor(d2);
/*  60 */     double d6 = Math.floor(d3) + 0.5D;
/*  61 */     return new Vec3d(d4, d5, d6);
/*     */   }
/*     */ 
/*     */   
/*     */   @Subscribe
/*     */   public void onUpdate() {
/*  67 */     this; if (mc.field_71441_e == null) {
/*     */       return;
/*     */     }
/*  70 */     this; if (mc.field_71439_g.field_70125_A >= ((Integer)this.pitch.getValue()).intValue()) {
/*  71 */       if (isBlockHole(getPlayerPos().func_177979_c(1)) || isBlockHole(getPlayerPos().func_177979_c(2)) || isBlockHole(getPlayerPos().func_177979_c(3)) || isBlockHole(getPlayerPos().func_177979_c(4))) {
/*  72 */         Anchoring = true;
/*  73 */         if (!((Boolean)this.pull.getValue()).booleanValue()) {
/*  74 */           this; mc.field_71439_g.field_70159_w = 0.0D;
/*  75 */           this; mc.field_71439_g.field_70179_y = 0.0D;
/*     */         } else {
/*  77 */           this; this; this; Vec3d center = GetCenter(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
/*  78 */           this; double d = Math.abs(center.field_72450_a - mc.field_71439_g.field_70165_t);
/*  79 */           this; double d2 = Math.abs(center.field_72449_c - mc.field_71439_g.field_70161_v);
/*  80 */           if (d > 0.1D || d2 > 0.1D) {
/*  81 */             this; double d3 = center.field_72450_a - mc.field_71439_g.field_70165_t;
/*  82 */             this; double d4 = center.field_72449_c - mc.field_71439_g.field_70161_v;
/*  83 */             this; mc.field_71439_g.field_70159_w = d3 / 2.0D;
/*  84 */             this; mc.field_71439_g.field_70179_y = d4 / 2.0D;
/*     */           } 
/*     */         } 
/*     */       } else {
/*  88 */         Anchoring = false;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  96 */     Anchoring = false;
/*  97 */     this.holeblocks = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public BlockPos getPlayerPos() {
/* 102 */     this; this; this; return new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\movement\Anchor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */