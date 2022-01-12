/*     */ package me.mohalk.banzem.features.gui.custom;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.util.RenderUtil;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.gui.GuiOptions;
/*     */ import net.minecraft.client.gui.GuiScreen;
/*     */ import net.minecraft.client.gui.GuiWorldSelection;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ public class GuiCustomMainScreen extends GuiScreen {
/*  16 */   private final String backgroundURL = "https://i.imgur.com/GCJRhiA.png";
/*  17 */   private final ResourceLocation resourceLocation = new ResourceLocation("eralp232/background.png");
/*     */   private int y;
/*     */   private int x;
/*     */   private int singleplayerWidth;
/*     */   private int multiplayerWidth;
/*     */   private int settingsWidth;
/*     */   private int exitWidth;
/*     */   private int textHeight;
/*     */   private float xOffset;
/*     */   private float yOffset;
/*     */   
/*     */   public static void drawCompleteImage(float posX, float posY, float width, float height) {
/*  29 */     GL11.glPushMatrix();
/*  30 */     GL11.glTranslatef(posX, posY, 0.0F);
/*  31 */     GL11.glBegin(7);
/*  32 */     GL11.glTexCoord2f(0.0F, 0.0F);
/*  33 */     GL11.glVertex3f(0.0F, 0.0F, 0.0F);
/*  34 */     GL11.glTexCoord2f(0.0F, 1.0F);
/*  35 */     GL11.glVertex3f(0.0F, height, 0.0F);
/*  36 */     GL11.glTexCoord2f(1.0F, 1.0F);
/*  37 */     GL11.glVertex3f(width, height, 0.0F);
/*  38 */     GL11.glTexCoord2f(1.0F, 0.0F);
/*  39 */     GL11.glVertex3f(width, 0.0F, 0.0F);
/*  40 */     GL11.glEnd();
/*  41 */     GL11.glPopMatrix();
/*     */   }
/*     */   
/*     */   public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
/*  45 */     return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height);
/*     */   }
/*     */   
/*     */   public void func_73866_w_() {
/*  49 */     this.x = this.field_146294_l / 2;
/*  50 */     this.y = this.field_146295_m / 4 + 48;
/*  51 */     this.field_146292_n.add(new TextButton(0, this.x, this.y + 20, "Singleplayer"));
/*  52 */     this.field_146292_n.add(new TextButton(1, this.x, this.y + 44, "Multiplayer"));
/*  53 */     this.field_146292_n.add(new TextButton(2, this.x, this.y + 66, "Settings"));
/*  54 */     this.field_146292_n.add(new TextButton(2, this.x, this.y + 88, "Exit"));
/*  55 */     GlStateManager.func_179090_x();
/*  56 */     GlStateManager.func_179147_l();
/*  57 */     GlStateManager.func_179118_c();
/*  58 */     GlStateManager.func_179103_j(7425);
/*  59 */     GlStateManager.func_179103_j(7424);
/*  60 */     GlStateManager.func_179084_k();
/*  61 */     GlStateManager.func_179141_d();
/*  62 */     GlStateManager.func_179098_w();
/*     */   }
/*     */   
/*     */   public void func_73876_c() {
/*  66 */     super.func_73876_c();
/*     */   }
/*     */   
/*     */   public void func_73864_a(int mouseX, int mouseY, int mouseButton) {
/*  70 */     if (isHovered(this.x - Banzem.textManager.getStringWidth("Singleplayer") / 2, this.y + 20, Banzem.textManager.getStringWidth("Singleplayer"), Banzem.textManager.getFontHeight(), mouseX, mouseY)) {
/*  71 */       this.field_146297_k.func_147108_a((GuiScreen)new GuiWorldSelection(this));
/*  72 */     } else if (isHovered(this.x - Banzem.textManager.getStringWidth("Multiplayer") / 2, this.y + 44, Banzem.textManager.getStringWidth("Multiplayer"), Banzem.textManager.getFontHeight(), mouseX, mouseY)) {
/*  73 */       this.field_146297_k.func_147108_a((GuiScreen)new GuiMultiplayer(this));
/*  74 */     } else if (isHovered(this.x - Banzem.textManager.getStringWidth("Settings") / 2, this.y + 66, Banzem.textManager.getStringWidth("Settings"), Banzem.textManager.getFontHeight(), mouseX, mouseY)) {
/*  75 */       this.field_146297_k.func_147108_a((GuiScreen)new GuiOptions(this, this.field_146297_k.field_71474_y));
/*  76 */     } else if (isHovered(this.x - Banzem.textManager.getStringWidth("Exit") / 2, this.y + 88, Banzem.textManager.getStringWidth("Exit"), Banzem.textManager.getFontHeight(), mouseX, mouseY)) {
/*  77 */       this.field_146297_k.func_71400_g();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
/*  82 */     this.xOffset = -1.0F * (mouseX - this.field_146294_l / 2.0F) / this.field_146294_l / 32.0F;
/*  83 */     this.yOffset = -1.0F * (mouseY - this.field_146295_m / 2.0F) / this.field_146295_m / 18.0F;
/*  84 */     this.x = this.field_146294_l / 2;
/*  85 */     this.y = this.field_146295_m / 4 + 48;
/*  86 */     GlStateManager.func_179098_w();
/*  87 */     GlStateManager.func_179084_k();
/*  88 */     this.field_146297_k.func_110434_K().func_110577_a(this.resourceLocation);
/*  89 */     drawCompleteImage(-16.0F + this.xOffset, -9.0F + this.yOffset, (this.field_146294_l + 32), (this.field_146295_m + 18));
/*  90 */     super.func_73863_a(mouseX, mouseY, partialTicks);
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage parseBackground(BufferedImage background) {
/*  95 */     int width = 1920;
/*  96 */     int srcWidth = background.getWidth();
/*  97 */     int srcHeight = background.getHeight(); int height;
/*  98 */     for (height = 1080; width < srcWidth || height < srcHeight; ) { width *= 2; height *= 2; }
/*     */     
/* 100 */     BufferedImage imgNew = new BufferedImage(width, height, 2);
/* 101 */     Graphics g = imgNew.getGraphics();
/* 102 */     g.drawImage(background, 0, 0, null);
/* 103 */     g.dispose();
/* 104 */     return imgNew;
/*     */   }
/*     */   
/*     */   private static class TextButton
/*     */     extends GuiButton {
/*     */     public TextButton(int buttonId, int x, int y, String buttonText) {
/* 110 */       super(buttonId, x, y, Banzem.textManager.getStringWidth(buttonText), Banzem.textManager.getFontHeight(), buttonText);
/*     */     }
/*     */     
/*     */     public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
/* 114 */       if (this.field_146125_m) {
/* 115 */         this.field_146124_l = true;
/* 116 */         this.field_146123_n = (mouseX >= this.field_146128_h - Banzem.textManager.getStringWidth(this.field_146126_j) / 2.0F && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g);
/* 117 */         Banzem.textManager.drawStringWithShadow(this.field_146126_j, this.field_146128_h - Banzem.textManager.getStringWidth(this.field_146126_j) / 2.0F, this.field_146129_i, Color.WHITE.getRGB());
/* 118 */         if (this.field_146123_n) {
/* 119 */           RenderUtil.drawLine((this.field_146128_h - 1) - Banzem.textManager.getStringWidth(this.field_146126_j) / 2.0F, (this.field_146129_i + 2 + Banzem.textManager.getFontHeight()), this.field_146128_h + Banzem.textManager.getStringWidth(this.field_146126_j) / 2.0F + 1.0F, (this.field_146129_i + 2 + Banzem.textManager.getFontHeight()), 1.0F, Color.WHITE.getRGB());
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean func_146116_c(Minecraft mc, int mouseX, int mouseY) {
/* 125 */       return (this.field_146124_l && this.field_146125_m && mouseX >= this.field_146128_h - Banzem.textManager.getStringWidth(this.field_146126_j) / 2.0F && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\custom\GuiCustomMainScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */