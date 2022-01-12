/*     */ package me.mohalk.banzem.features.modules.misc;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import me.mohalk.banzem.event.events.Render2DEvent;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Bind;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.util.ColorUtil;
/*     */ import me.mohalk.banzem.util.EntityUtil;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.gui.inventory.GuiContainer;
/*     */ import net.minecraft.client.renderer.BufferBuilder;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.client.renderer.RenderHelper;
/*     */ import net.minecraft.client.renderer.Tessellator;
/*     */ import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.init.Items;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.ItemStackHelper;
/*     */ import net.minecraft.inventory.Slot;
/*     */ import net.minecraft.item.Item;
/*     */ import net.minecraft.item.ItemShulkerBox;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.NBTTagCompound;
/*     */ import net.minecraft.tileentity.TileEntityShulkerBox;
/*     */ import net.minecraft.util.NonNullList;
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraft.world.storage.MapData;
/*     */ import net.minecraftforge.client.event.RenderTooltipEvent;
/*     */ import net.minecraftforge.event.entity.player.ItemTooltipEvent;
/*     */ import net.minecraftforge.fml.common.eventhandler.EventPriority;
/*     */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ public class ToolTips
/*     */   extends Module {
/*  43 */   private static final ResourceLocation MAP = new ResourceLocation("eralp/transparent.png");
/*  44 */   private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("eralp232/transparent.png");
/*  45 */   private static ToolTips INSTANCE = new ToolTips();
/*  46 */   public Setting<Boolean> maps = register(new Setting("Maps", Boolean.valueOf(true)));
/*  47 */   public Setting<Boolean> shulkers = register(new Setting("ShulkerViewer", Boolean.valueOf(true)));
/*  48 */   public Setting<Bind> peek = register(new Setting("Peek", new Bind(-1)));
/*  49 */   public Setting<Boolean> shulkerSpy = register(new Setting("ShulkerSpy", Boolean.valueOf(true)));
/*  50 */   public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> ((Boolean)this.shulkerSpy.getValue()).booleanValue()));
/*  51 */   public Setting<Boolean> own = register(new Setting("OwnShulker", Boolean.valueOf(true), v -> ((Boolean)this.shulkerSpy.getValue()).booleanValue()));
/*  52 */   public Setting<Integer> cooldown = register(new Setting("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> ((Boolean)this.shulkerSpy.getValue()).booleanValue()));
/*  53 */   public Setting<Boolean> textColor = register(new Setting("TextColor", Boolean.valueOf(false), v -> ((Boolean)this.shulkers.getValue()).booleanValue()));
/*  54 */   private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
/*  55 */   private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
/*  56 */   private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
/*  57 */   private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
/*  58 */   public Setting<Boolean> offsets = register(new Setting("Offsets", Boolean.valueOf(false)));
/*  59 */   private final Setting<Integer> yPerPlayer = register(new Setting("Y/Player", Integer.valueOf(18), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  60 */   private final Setting<Integer> xOffset = register(new Setting("XOffset", Integer.valueOf(4), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  61 */   private final Setting<Integer> yOffset = register(new Setting("YOffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  62 */   private final Setting<Integer> trOffset = register(new Setting("TROffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  63 */   public Setting<Integer> invH = register(new Setting("InvH", Integer.valueOf(3), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  64 */   public Map<EntityPlayer, ItemStack> spiedPlayers = new ConcurrentHashMap<>();
/*  65 */   public Map<EntityPlayer, Timer> playerTimers = new ConcurrentHashMap<>();
/*  66 */   private int textRadarY = 0;
/*     */   
/*     */   public ToolTips() {
/*  69 */     super("ShulkerPreview", "Several tweaks for tooltips.", Module.Category.MISC, true, false, false);
/*  70 */     setInstance();
/*     */   }
/*     */   
/*     */   public static ToolTips getInstance() {
/*  74 */     if (INSTANCE == null) {
/*  75 */       INSTANCE = new ToolTips();
/*     */     }
/*  77 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   public static void displayInv(ItemStack stack, String name) {
/*     */     try {
/*  82 */       Item item = stack.func_77973_b();
/*  83 */       TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
/*  84 */       ItemShulkerBox shulker = (ItemShulkerBox)item;
/*  85 */       entityBox.field_145854_h = shulker.func_179223_d();
/*  86 */       entityBox.func_145834_a((World)mc.field_71441_e);
/*  87 */       ItemStackHelper.func_191283_b(stack.func_77978_p().func_74775_l("BlockEntityTag"), entityBox.field_190596_f);
/*  88 */       entityBox.func_145839_a(stack.func_77978_p().func_74775_l("BlockEntityTag"));
/*  89 */       entityBox.func_190575_a((name == null) ? stack.func_82833_r() : name);
/*  90 */       (new Thread(() -> {
/*     */             try {
/*     */               Thread.sleep(200L);
/*  93 */             } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */             
/*     */             mc.field_71439_g.func_71007_a((IInventory)entityBox);
/*  97 */           })).start();
/*  98 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void setInstance() {
/* 104 */     INSTANCE = this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/* 111 */     if (fullNullCheck() || !((Boolean)this.shulkerSpy.getValue()).booleanValue())
/*     */       return;  ItemStack stack;
/*     */     Slot slot;
/* 114 */     if (((Bind)this.peek.getValue()).getKey() != -1 && mc.field_71462_r instanceof GuiContainer && Keyboard.isKeyDown(((Bind)this.peek.getValue()).getKey()) && (slot = ((GuiContainer)mc.field_71462_r).getSlotUnderMouse()) != null && (stack = slot.func_75211_c()) != null && stack.func_77973_b() instanceof ItemShulkerBox) {
/* 115 */       displayInv(stack, (String)null);
/*     */     }
/* 117 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/* 118 */       if (player == null || player.func_184614_ca() == null || !(player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox) || EntityUtil.isFakePlayer(player) || (!((Boolean)this.own.getValue()).booleanValue() && mc.field_71439_g.equals(player)))
/*     */         continue; 
/* 120 */       ItemStack stack2 = player.func_184614_ca();
/* 121 */       this.spiedPlayers.put(player, stack2);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRender2D(Render2DEvent event) {
/* 127 */     if (fullNullCheck() || !((Boolean)this.shulkerSpy.getValue()).booleanValue() || !((Boolean)this.render.getValue()).booleanValue()) {
/*     */       return;
/*     */     }
/* 130 */     int x = -4 + ((Integer)this.xOffset.getValue()).intValue();
/* 131 */     int y = 10 + ((Integer)this.yOffset.getValue()).intValue();
/* 132 */     this.textRadarY = 0;
/* 133 */     for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
/*     */       
/* 135 */       if (this.spiedPlayers.get(player) == null)
/* 136 */         continue;  if (player.func_184614_ca() == null || !(player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox)) {
/* 137 */         Timer playerTimer = this.playerTimers.get(player);
/* 138 */         if (playerTimer == null)
/* 139 */         { Timer timer = new Timer();
/* 140 */           timer.reset();
/* 141 */           this.playerTimers.put(player, timer); }
/* 142 */         else if (playerTimer.passedS(((Integer)this.cooldown.getValue()).intValue())) { continue; }
/*     */       
/*     */       } else {
/* 145 */         Timer playerTimer; if (player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox && (playerTimer = this.playerTimers.get(player)) != null) {
/* 146 */           playerTimer.reset();
/* 147 */           this.playerTimers.put(player, playerTimer);
/*     */         } 
/* 149 */       }  ItemStack stack = this.spiedPlayers.get(player);
/* 150 */       renderShulkerToolTip(stack, x, y, player.func_70005_c_());
/* 151 */       this.textRadarY = (y += ((Integer)this.yPerPlayer.getValue()).intValue() + 60) - 10 - ((Integer)this.yOffset.getValue()).intValue() + ((Integer)this.trOffset.getValue()).intValue();
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getTextRadarY() {
/* 156 */     return this.textRadarY;
/*     */   }
/*     */ 
/*     */   
/*     */   @SubscribeEvent(priority = EventPriority.HIGHEST)
/*     */   public void makeTooltip(ItemTooltipEvent event) {}
/*     */   
/*     */   @SubscribeEvent
/*     */   public void renderTooltip(RenderTooltipEvent.PostText event) {
/*     */     MapData mapData;
/* 166 */     if (((Boolean)this.maps.getValue()).booleanValue() && !event.getStack().func_190926_b() && event.getStack().func_77973_b() instanceof net.minecraft.item.ItemMap && (mapData = Items.field_151098_aY.func_77873_a(event.getStack(), (World)mc.field_71441_e)) != null) {
/* 167 */       GlStateManager.func_179094_E();
/* 168 */       GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
/* 169 */       RenderHelper.func_74518_a();
/* 170 */       Util.mc.func_110434_K().func_110577_a(MAP);
/* 171 */       Tessellator instance = Tessellator.func_178181_a();
/* 172 */       BufferBuilder buffer = instance.func_178180_c();
/* 173 */       int n = 7;
/* 174 */       float n2 = 135.0F;
/* 175 */       float n3 = 0.5F;
/* 176 */       GlStateManager.func_179109_b(event.getX(), event.getY() - n2 * n3 - 5.0F, 0.0F);
/* 177 */       GlStateManager.func_179152_a(n3, n3, n3);
/* 178 */       buffer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
/* 179 */       buffer.func_181662_b(-n, n2, 0.0D).func_187315_a(0.0D, 1.0D).func_181675_d();
/* 180 */       buffer.func_181662_b(n2, n2, 0.0D).func_187315_a(1.0D, 1.0D).func_181675_d();
/* 181 */       buffer.func_181662_b(n2, -n, 0.0D).func_187315_a(1.0D, 0.0D).func_181675_d();
/* 182 */       buffer.func_181662_b(-n, -n, 0.0D).func_187315_a(0.0D, 0.0D).func_181675_d();
/* 183 */       instance.func_78381_a();
/* 184 */       mc.field_71460_t.func_147701_i().func_148250_a(mapData, false);
/* 185 */       GlStateManager.func_179145_e();
/* 186 */       GlStateManager.func_179121_F();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
/* 192 */     NBTTagCompound tagCompound = stack.func_77978_p(); NBTTagCompound blockEntityTag;
/* 193 */     if (tagCompound != null && tagCompound.func_150297_b("BlockEntityTag", 10) && (blockEntityTag = tagCompound.func_74775_l("BlockEntityTag")).func_150297_b("Items", 9)) {
/* 194 */       GlStateManager.func_179098_w();
/* 195 */       GlStateManager.func_179140_f();
/* 196 */       GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/* 197 */       GlStateManager.func_179147_l();
/* 198 */       GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
/* 199 */       Util.mc.func_110434_K().func_110577_a(SHULKER_GUI_TEXTURE);
/* 200 */       RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
/* 201 */       RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + ((Integer)this.invH.getValue()).intValue(), 500);
/* 202 */       RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
/* 203 */       GlStateManager.func_179097_i();
/* 204 */       Color color = new Color(0, 0, 0, 255);
/* 205 */       if (((Boolean)this.textColor.getValue()).booleanValue()) {
/* 206 */         color = new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue());
/*     */       }
/* 208 */       this.renderer.drawStringWithShadow((name == null) ? stack.func_82833_r() : name, (x + 8), (y + 6), ColorUtil.toRGBA(color));
/* 209 */       GlStateManager.func_179126_j();
/* 210 */       RenderHelper.func_74520_c();
/* 211 */       GlStateManager.func_179091_B();
/* 212 */       GlStateManager.func_179142_g();
/* 213 */       GlStateManager.func_179145_e();
/* 214 */       NonNullList nonnulllist = NonNullList.func_191197_a(27, ItemStack.field_190927_a);
/* 215 */       ItemStackHelper.func_191283_b(blockEntityTag, nonnulllist);
/* 216 */       for (int i = 0; i < nonnulllist.size(); i++) {
/* 217 */         int iX = x + i % 9 * 18 + 8;
/* 218 */         int iY = y + i / 9 * 18 + 18;
/* 219 */         ItemStack itemStack = (ItemStack)nonnulllist.get(i);
/* 220 */         (mc.func_175599_af()).field_77023_b = 501.0F;
/* 221 */         RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
/* 222 */         RenderUtil.itemRender.func_180453_a(mc.field_71466_p, itemStack, iX, iY, null);
/* 223 */         (mc.func_175599_af()).field_77023_b = 0.0F;
/*     */       } 
/* 225 */       GlStateManager.func_179140_f();
/* 226 */       GlStateManager.func_179084_k();
/* 227 */       GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\misc\ToolTips.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */