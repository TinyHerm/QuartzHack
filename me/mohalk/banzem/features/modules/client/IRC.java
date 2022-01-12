/*     */ package me.mohalk.banzem.features.modules.client;
/*     */ import java.awt.Color;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.event.events.Render3DEvent;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.command.Command;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ import me.mohalk.banzem.features.setting.Bind;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ import me.mohalk.banzem.manager.WaypointManager;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import me.mohalk.banzem.util.Timer;
/*     */ import me.mohalk.banzem.util.Util;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.client.renderer.RenderHelper;
/*     */ import net.minecraft.init.SoundEvents;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.util.SoundCategory;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.RayTraceResult;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ public class IRC extends Module {
/*  36 */   public static final Random avRandomizer = new Random();
/*  37 */   private static final ResourceLocation SHULKER_GUI_TEXTURE = null;
/*     */   public static IRC INSTANCE;
/*     */   public static IRCHandler handler;
/*     */   public static List<String> phobosUsers;
/*  41 */   public Setting<String> ip = register(new Setting("IP", "206.189.218.150"));
/*  42 */   public Setting<Boolean> waypoints = register(new Setting("Waypoints", Boolean.valueOf(false)));
/*  43 */   public Setting<Boolean> ding = register(new Setting("Ding", Boolean.valueOf(false), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
/*  44 */   public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
/*  45 */   public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
/*  46 */   public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
/*  47 */   public Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
/*  48 */   public Setting<Boolean> inventories = register(new Setting("Inventories", Boolean.valueOf(false)));
/*  49 */   public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
/*  50 */   public Setting<Boolean> own = register(new Setting("OwnShulker", Boolean.valueOf(true), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
/*  51 */   public Setting<Integer> cooldown = register(new Setting("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
/*  52 */   public Setting<Boolean> offsets = register(new Setting("Offsets", Boolean.valueOf(false)));
/*  53 */   private final Setting<Integer> yPerPlayer = register(new Setting("Y/Player", Integer.valueOf(18), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  54 */   private final Setting<Integer> xOffset = register(new Setting("XOffset", Integer.valueOf(4), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  55 */   private final Setting<Integer> yOffset = register(new Setting("YOffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  56 */   private final Setting<Integer> trOffset = register(new Setting("TROffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
/*  57 */   public Setting<Integer> invH = register(new Setting("InvH", Integer.valueOf(3), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
/*  58 */   public Setting<Bind> pingBind = register(new Setting("Ping", new Bind(-1)));
/*     */   public boolean status = false;
/*  60 */   public Timer updateTimer = new Timer();
/*  61 */   public Timer downTimer = new Timer();
/*     */   
/*     */   public BlockPos waypointTarget;
/*  64 */   private int textRadarY = 0;
/*     */   private boolean down = false;
/*     */   private boolean pressed = false;
/*     */   
/*     */   public IRC() {
/*  69 */     super("PhobosChat", "QuartzHack  chat server", Module.Category.CLIENT, true, false, true);
/*  70 */     INSTANCE = this;
/*     */   }
/*     */   
/*     */   public static void updateInventory() throws IOException {
/*  74 */     handler.outputStream.writeUTF("updateinventory");
/*  75 */     handler.outputStream.writeUTF(mc.field_71439_g.func_70005_c_());
/*  76 */     writeByteArray(serializeInventory(), handler.outputStream);
/*     */   }
/*     */   
/*     */   public static void updateInventories() {
/*  80 */     for (String player : phobosUsers) {
/*     */       try {
/*  82 */         send("inventory", player);
/*  83 */       } catch (IOException e) {
/*  84 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void updateWaypoint(BlockPos pos, String server, String dimension, Color color) throws IOException {
/*  90 */     send("waypoint", server + ":" + dimension + ":" + pos.func_177958_n() + ":" + pos.func_177956_o() + ":" + pos.func_177952_p(), color.getRed() + ":" + color.getGreen() + ":" + color.getBlue() + ":" + color.getAlpha());
/*     */   }
/*     */   
/*     */   public static void removeWaypoint() throws IOException {
/*  94 */     handler.outputStream.writeUTF("removewaypoint");
/*  95 */     handler.outputStream.writeUTF(mc.field_71439_g.func_70005_c_());
/*  96 */     handler.outputStream.flush();
/*     */   }
/*     */   
/*     */   public static void send(String command, String data, String data1) throws IOException {
/* 100 */     handler.outputStream.writeUTF(command);
/* 101 */     handler.outputStream.writeUTF(mc.field_71439_g.func_70005_c_());
/* 102 */     handler.outputStream.writeUTF(data);
/* 103 */     handler.outputStream.writeUTF(data1);
/* 104 */     handler.outputStream.flush();
/*     */   }
/*     */   
/*     */   public static void send(String command, String data) throws IOException {
/* 108 */     handler.outputStream.writeUTF(command);
/* 109 */     handler.outputStream.writeUTF(mc.field_71439_g.func_70005_c_());
/* 110 */     handler.outputStream.writeUTF(data);
/* 111 */     handler.outputStream.flush();
/*     */   }
/*     */   
/*     */   private static byte[] readByteArrayLWithLength(DataInputStream reader) throws IOException {
/* 115 */     int length = reader.readInt();
/* 116 */     if (length > 0) {
/* 117 */       byte[] cifrato = new byte[length];
/* 118 */       reader.readFully(cifrato, 0, cifrato.length);
/* 119 */       return cifrato;
/*     */     } 
/* 121 */     return null;
/*     */   }
/*     */   
/*     */   public static void writeByteArray(byte[] data, DataOutputStream writer) throws IOException {
/* 125 */     writer.writeInt(data.length);
/* 126 */     writer.write(data);
/* 127 */     writer.flush();
/*     */   }
/*     */   
/*     */   public static List<ItemStack> deserializeInventory(byte[] inventory) throws IOException, ClassNotFoundException {
/* 131 */     ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inventory));
/* 132 */     ArrayList<ItemStack> inventoryList = (ArrayList<ItemStack>)stream.readObject();
/* 133 */     return inventoryList;
/*     */   }
/*     */   
/*     */   public static byte[] serializeInventory() throws IOException {
/* 137 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 138 */     ObjectOutputStream oos = new ObjectOutputStream(bos);
/* 139 */     oos.writeObject(new ArrayList((Collection<?>)mc.field_71439_g.field_71071_by.field_70462_a));
/* 140 */     return bos.toByteArray();
/*     */   }
/*     */   
/*     */   public static void say(String message) throws IOException {
/* 144 */     handler.outputStream.writeUTF("message");
/* 145 */     handler.outputStream.writeUTF(mc.field_71439_g.func_70005_c_());
/* 146 */     handler.outputStream.writeUTF(message);
/* 147 */     handler.outputStream.flush();
/*     */   }
/*     */   
/*     */   public static void cockt(int id) throws IOException {
/* 151 */     handler.outputStream.writeUTF("cockt");
/* 152 */     handler.outputStream.writeInt(id);
/* 153 */     handler.outputStream.flush();
/*     */   }
/*     */   
/*     */   public static String getDimension(int dim) {
/* 157 */     switch (dim) {
/*     */       case 0:
/* 159 */         return "Overworld";
/*     */       
/*     */       case -1:
/* 162 */         return "Nether";
/*     */       
/*     */       case 1:
/* 165 */         return "End";
/*     */     } 
/*     */     
/* 168 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onUpdate() {
/* 175 */     if (handler != null && handler.isAlive() && !handler.isInterrupted()) {
/* 176 */       this.status = !handler.socket.isClosed();
/*     */     } else {
/* 178 */       this.status = false;
/*     */     } 
/* 180 */     if (this.updateTimer.passedMs(5000L) && handler != null && handler.isAlive() && !handler.socket.isClosed()) {
/*     */       try {
/* 182 */         handler.outputStream.writeUTF("update");
/* 183 */         handler.outputStream.writeUTF(mc.field_71439_g.func_70005_c_());
/* 184 */         handler.outputStream.flush();
/* 185 */       } catch (Exception e) {
/* 186 */         e.printStackTrace();
/*     */       } 
/* 188 */       this.updateTimer.reset();
/*     */     } 
/* 190 */     if (!mc.func_71356_B() && !(mc.field_71462_r instanceof me.mohalk.banzem.features.gui.PhobosGui) && handler != null && !handler.socket.isClosed() && this.status) {
/* 191 */       if (this.down) {
/* 192 */         if (this.downTimer.passedMs(2000L)) {
/*     */           try {
/* 194 */             removeWaypoint();
/* 195 */           } catch (IOException e2) {
/* 196 */             e2.printStackTrace();
/*     */           } 
/* 198 */           this.down = false;
/* 199 */           this.downTimer.reset();
/*     */         } 
/* 201 */         if (!Keyboard.isKeyDown(((Bind)this.pingBind.getValue()).getKey())) {
/*     */           try {
/* 203 */             updateWaypoint(this.waypointTarget, mc.field_71422_O.field_78845_b, String.valueOf(mc.field_71439_g.field_71093_bK), new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()));
/* 204 */           } catch (IOException e2) {
/* 205 */             e2.printStackTrace();
/*     */           } 
/*     */         }
/*     */       } 
/* 209 */       if (Keyboard.isKeyDown(((Bind)this.pingBind.getValue()).getKey())) {
/* 210 */         if (!this.pressed) {
/* 211 */           this.down = true;
/* 212 */           this.pressed = true;
/*     */         } 
/*     */       } else {
/* 215 */         this.down = false;
/* 216 */         this.pressed = false;
/* 217 */         this.downTimer.reset();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onRender3D(Render3DEvent event) {
/* 224 */     if (Feature.fullNullCheck() || mc.func_71356_B()) {
/*     */       return;
/*     */     }
/* 227 */     RayTraceResult result = mc.field_71439_g.func_174822_a(2000.0D, event.getPartialTicks());
/* 228 */     if (result != null) {
/* 229 */       this.waypointTarget = new BlockPos(result.field_72307_f);
/*     */     }
/* 231 */     if (((Boolean)this.waypoints.getValue()).booleanValue()) {
/* 232 */       for (WaypointManager.Waypoint waypoint : Banzem.waypointManager.waypoints.values()) {
/* 233 */         if (mc.field_71439_g.field_71093_bK != waypoint.dimension || 
/* 234 */           !mc.field_71422_O.field_78845_b.equals(waypoint.server)) {
/*     */           continue;
/*     */         }
/* 237 */         waypoint.renderBox();
/* 238 */         waypoint.render();
/* 239 */         GlStateManager.func_179126_j();
/* 240 */         GlStateManager.func_179132_a(true);
/* 241 */         GlStateManager.func_179145_e();
/* 242 */         GlStateManager.func_179084_k();
/* 243 */         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/* 244 */         RenderHelper.func_74518_a();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onRender2D(Render2DEvent event) {
/* 252 */     if (Feature.fullNullCheck()) {
/*     */       return;
/*     */     }
/* 255 */     if (((Boolean)this.inventories.getValue()).booleanValue()) {
/* 256 */       int x = -4 + ((Integer)this.xOffset.getValue()).intValue();
/* 257 */       int y = 10 + ((Integer)this.yOffset.getValue()).intValue();
/* 258 */       this.textRadarY = 0;
/* 259 */       for (String player : phobosUsers) {
/* 260 */         if (Banzem.inventoryManager.inventories.get(player) != null) {
/*     */           continue;
/*     */         }
/* 263 */         List<ItemStack> stacks = (List<ItemStack>)Banzem.inventoryManager.inventories.get(player);
/* 264 */         renderShulkerToolTip(stacks, x, y, player);
/* 265 */         y += ((Integer)this.yPerPlayer.getValue()).intValue() + 60;
/* 266 */         this.textRadarY = y - 10 - ((Integer)this.yOffset.getValue()).intValue() + ((Integer)this.trOffset.getValue()).intValue();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void connect() throws IOException {
/* 272 */     if (!INSTANCE.status) {
/* 273 */       Socket socket = new Socket((String)this.ip.getValue(), 1488);
/* 274 */       (handler = new IRCHandler(socket)).start();
/* 275 */       handler.outputStream.writeUTF("update");
/* 276 */       handler.outputStream.writeUTF(mc.field_71439_g.func_70005_c_());
/* 277 */       handler.outputStream.flush();
/* 278 */       INSTANCE.status = true;
/* 279 */       Command.sendMessage("Â§aIRC connected successfully!");
/*     */     } else {
/* 281 */       Command.sendMessage("Â§cIRC is already connected!");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void disconnect() throws IOException {
/* 286 */     if (INSTANCE.status) {
/* 287 */       handler.socket.close();
/* 288 */       if (!handler.isInterrupted()) {
/* 289 */         handler.interrupt();
/*     */       }
/*     */     } else {
/* 292 */       Command.sendMessage("Â§cIRC is not connected!");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void friendAll() throws IOException {
/* 297 */     handler.outputStream.writeUTF("friendall");
/* 298 */     handler.outputStream.flush();
/*     */   }
/*     */   
/*     */   public void list() throws IOException {
/* 302 */     handler.outputStream.writeUTF("list");
/* 303 */     handler.outputStream.flush();
/*     */   }
/*     */   
/*     */   public void renderShulkerToolTip(List<ItemStack> stacks, int x, int y, String name) {
/* 307 */     GlStateManager.func_179098_w();
/* 308 */     GlStateManager.func_179140_f();
/* 309 */     GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/* 310 */     GlStateManager.func_179147_l();
/* 311 */     GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
/* 312 */     mc.func_110434_K().func_110577_a(SHULKER_GUI_TEXTURE);
/* 313 */     RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
/* 314 */     RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + ((Integer)this.invH.getValue()).intValue(), 500);
/* 315 */     RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
/* 316 */     GlStateManager.func_179097_i();
/* 317 */     Color color = new Color(0, 0, 0, 255);
/* 318 */     this.renderer.drawStringWithShadow(name, (x + 8), (y + 6), ColorUtil.toRGBA(color));
/* 319 */     GlStateManager.func_179126_j();
/* 320 */     RenderHelper.func_74520_c();
/* 321 */     GlStateManager.func_179091_B();
/* 322 */     GlStateManager.func_179142_g();
/* 323 */     GlStateManager.func_179145_e();
/* 324 */     for (int i = 0; i < stacks.size(); i++) {
/* 325 */       int iX = x + i % 9 * 18 + 8;
/* 326 */       int iY = y + i / 9 * 18 + 18;
/* 327 */       ItemStack itemStack = stacks.get(i);
/* 328 */       (mc.func_175599_af()).field_77023_b = 501.0F;
/* 329 */       RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
/* 330 */       RenderUtil.itemRender.func_180453_a(mc.field_71466_p, itemStack, iX, iY, null);
/* 331 */       (mc.func_175599_af()).field_77023_b = 0.0F;
/*     */     } 
/* 333 */     GlStateManager.func_179140_f();
/* 334 */     GlStateManager.func_179084_k();
/* 335 */     GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
/*     */   }
/*     */   
/*     */   private static class IRCHandler
/*     */     extends Thread {
/*     */     public Socket socket;
/*     */     public DataInputStream inputStream;
/*     */     public DataOutputStream outputStream;
/*     */     
/*     */     public IRCHandler(Socket socket) {
/* 345 */       super(Util.mc.field_71439_g.func_70005_c_());
/* 346 */       this.socket = socket;
/*     */       try {
/* 348 */         this.inputStream = new DataInputStream(socket.getInputStream());
/* 349 */         this.outputStream = new DataOutputStream(socket.getOutputStream());
/* 350 */       } catch (IOException e) {
/* 351 */         e.printStackTrace();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 357 */       Command.sendMessage("Â§aSocket thread starting!");
/*     */ 
/*     */ 
/*     */       
/*     */       while (true) {
/*     */         try {
/* 363 */           String input = this.inputStream.readUTF();
/* 364 */           if (input.equalsIgnoreCase("message")) {
/* 365 */             String name = this.inputStream.readUTF();
/* 366 */             String message = this.inputStream.readUTF();
/* 367 */             Command.sendMessage("Â§c[IRC] Â§r<" + name + ">: " + message);
/*     */           } 
/* 369 */           if (input.equalsIgnoreCase("list")) {
/* 370 */             String f = this.inputStream.readUTF();
/*     */             
/* 372 */             String[] split = f.split("%%%"), friends = split;
/* 373 */             for (String friend : split) {
/* 374 */               Command.sendMessage("Â§b" + friend.replace("_&_", " ID: "));
/*     */             }
/* 376 */           } else if (input.equalsIgnoreCase("friendall")) {
/* 377 */             String f = this.inputStream.readUTF();
/*     */             
/* 379 */             String[] split2 = f.split("%%%"), friends = split2;
/* 380 */             for (String friend : split2) {
/* 381 */               if (!friend.equals(Util.mc.field_71439_g.func_70005_c_())) {
/* 382 */                 Banzem.friendManager.addFriend(friend);
/* 383 */                 Command.sendMessage("Â§b" + friend + " has been friended");
/*     */               } 
/*     */             } 
/* 386 */           } else if (input.equalsIgnoreCase("waypoint")) {
/* 387 */             String name = this.inputStream.readUTF();
/* 388 */             String[] inputs = this.inputStream.readUTF().split(":");
/* 389 */             String[] colors = this.inputStream.readUTF().split(":");
/* 390 */             String server = inputs[0];
/* 391 */             String dimension = inputs[1];
/* 392 */             Color color = new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]), Integer.parseInt(colors[3]));
/* 393 */             Banzem.waypointManager.waypoints.put(name, new WaypointManager.Waypoint(name, server, Integer.parseInt(dimension), Integer.parseInt(inputs[2]), Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), color));
/* 394 */             Command.sendMessage("Â§c[IRC] Â§r" + name + " has set a waypoint at Â§c(" + Integer.parseInt(inputs[2]) + "," + Integer.parseInt(inputs[3]) + "," + Integer.parseInt(inputs[4]) + ")Â§r on the server Â§c" + server + "Â§r in the dimension Â§c" + IRC.getDimension(Integer.parseInt(dimension)));
/* 395 */             if (((Boolean)IRC.INSTANCE.ding.getValue()).booleanValue()) {
/* 396 */               Util.mc.field_71441_e.func_184134_a(Util.mc.field_71439_g.field_70165_t, Util.mc.field_71439_g.field_70163_u, Util.mc.field_71439_g.field_70161_v, SoundEvents.field_187604_bf, SoundCategory.PLAYERS, 1.0F, 0.7F, false);
/*     */             }
/* 398 */           } else if (input.equalsIgnoreCase("removewaypoint")) {
/* 399 */             String name = this.inputStream.readUTF();
/* 400 */             Banzem.waypointManager.waypoints.remove(name);
/* 401 */             Command.sendMessage("Â§c[IRC] Â§r" + name + " has removed their waypoint");
/* 402 */             if (((Boolean)IRC.INSTANCE.ding.getValue()).booleanValue()) {
/* 403 */               Util.mc.field_71441_e.func_184134_a(Util.mc.field_71439_g.field_70165_t, Util.mc.field_71439_g.field_70163_u, Util.mc.field_71439_g.field_70161_v, SoundEvents.field_187604_bf, SoundCategory.PLAYERS, 1.0F, -0.7F, false);
/*     */             }
/* 405 */           } else if (input.equalsIgnoreCase("inventory")) {
/* 406 */             String name = this.inputStream.readUTF();
/* 407 */             byte[] inventory = IRC.readByteArrayLWithLength(this.inputStream);
/* 408 */             for (String player : IRC.phobosUsers) {
/* 409 */               if (player.equalsIgnoreCase(name)) {
/* 410 */                 Banzem.inventoryManager.inventories.put(player, IRC.deserializeInventory(inventory));
/*     */               }
/*     */             } 
/* 413 */           } else if (input.equalsIgnoreCase("users")) {
/* 414 */             byte[] inputBytes = IRC.readByteArrayLWithLength(this.inputStream);
/* 415 */             ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inputBytes));
/* 416 */             List<String> players = (List<String>)stream.readObject();
/* 417 */             Command.sendMessage("Â§c[IRC]Â§r Active Users:");
/* 418 */             for (String name2 : players) {
/* 419 */               Command.sendMessage(name2);
/* 420 */               if (!IRC.phobosUsers.contains(name2)) {
/* 421 */                 IRC.phobosUsers.add(name2);
/*     */               }
/*     */             } 
/*     */           } 
/* 425 */           IRC.INSTANCE.status = !this.socket.isClosed();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/* 434 */         catch (IOException|ClassNotFoundException e) {
/* 435 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\IRC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */