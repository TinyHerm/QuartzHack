/*     */ package me.mohalk.banzem.features.gui.font;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import me.mohalk.banzem.features.modules.client.FontMod;
/*     */ import me.mohalk.banzem.features.modules.client.Media;
/*     */ import net.minecraft.client.renderer.GlStateManager;
/*     */ import net.minecraft.client.renderer.texture.DynamicTexture;
/*     */ import org.lwjgl.opengl.GL11;
/*     */ 
/*     */ public class CustomFont
/*     */   extends CFont
/*     */ {
/*  15 */   private final int[] colorCode = new int[32];
/*  16 */   protected CFont.CharData[] boldChars = new CFont.CharData[256];
/*  17 */   protected CFont.CharData[] italicChars = new CFont.CharData[256];
/*  18 */   protected CFont.CharData[] boldItalicChars = new CFont.CharData[256];
/*     */   protected DynamicTexture texBold;
/*     */   protected DynamicTexture texItalic;
/*     */   protected DynamicTexture texItalicBold;
/*     */   
/*     */   public CustomFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
/*  24 */     super(font, antiAlias, fractionalMetrics);
/*  25 */     setupMinecraftColorcodes();
/*  26 */     setupBoldItalicIDs();
/*     */   }
/*     */   
/*     */   public float drawStringWithShadow(String text, double x, double y, int color) {
/*  30 */     float shadowWidth = drawString(text, x + 1.0D, y + 1.0D, color, true);
/*  31 */     return Math.max(shadowWidth, drawString(text, x, y, color, false));
/*     */   }
/*     */   
/*     */   public float drawString(String text, float x, float y, int color) {
/*  35 */     return drawString(text, x, y, color, false);
/*     */   }
/*     */   
/*     */   public float drawCenteredString(String text, float x, float y, int color) {
/*  39 */     return drawString(text, x - (getStringWidth(text) / 2), y, color);
/*     */   }
/*     */   
/*     */   public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
/*  43 */     float shadowWidth = drawString(text, (x - (getStringWidth(text) / 2)) + 1.0D, y + 1.0D, color, true);
/*  44 */     return drawString(text, x - (getStringWidth(text) / 2), y, color);
/*     */   }
/*     */   
/*     */   public float drawString(String textIn, double xI, double yI, int color, boolean shadow) {
/*  48 */     String text = (Media.getInstance().isOn() && ((Boolean)(Media.getInstance()).changeOwn.getValue()).booleanValue()) ? textIn.replace(Media.getPlayerName(), (CharSequence)(Media.getInstance()).ownName.getValue()) : textIn;
/*  49 */     double x = xI;
/*  50 */     double y = yI;
/*  51 */     if (FontMod.getInstance().isOn() && !((Boolean)(FontMod.getInstance()).shadow.getValue()).booleanValue() && shadow) {
/*  52 */       x -= 0.5D;
/*  53 */       y -= 0.5D;
/*     */     } 
/*  55 */     x--;
/*  56 */     if (text == null) {
/*  57 */       return 0.0F;
/*     */     }
/*  59 */     if (color == 553648127) {
/*  60 */       color = 16777215;
/*     */     }
/*  62 */     if ((color & 0xFC000000) == 0) {
/*  63 */       color |= 0xFF000000;
/*     */     }
/*  65 */     if (shadow) {
/*  66 */       color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
/*     */     }
/*  68 */     CFont.CharData[] currentData = this.charData;
/*  69 */     float alpha = (color >> 24 & 0xFF) / 255.0F;
/*  70 */     boolean bold = false;
/*  71 */     boolean italic = false;
/*  72 */     boolean strikethrough = false;
/*  73 */     boolean underline = false;
/*  74 */     boolean render = true;
/*  75 */     x *= 2.0D;
/*  76 */     y = (y - 3.0D) * 2.0D;
/*  77 */     if (render) {
/*  78 */       GL11.glPushMatrix();
/*  79 */       GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
/*  80 */       GlStateManager.func_179147_l();
/*  81 */       GlStateManager.func_179112_b(770, 771);
/*  82 */       GlStateManager.func_179131_c((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
/*  83 */       int size = text.length();
/*  84 */       GlStateManager.func_179098_w();
/*  85 */       GlStateManager.func_179144_i(this.tex.func_110552_b());
/*  86 */       GL11.glBindTexture(3553, this.tex.func_110552_b());
/*  87 */       for (int i = 0; i < size; i++) {
/*  88 */         char character = text.charAt(i);
/*  89 */         if (character == '§' && i < size) {
/*  90 */           int colorIndex = 21;
/*     */           try {
/*  92 */             colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
/*  93 */           } catch (Exception e) {
/*  94 */             e.printStackTrace();
/*     */           } 
/*  96 */           if (colorIndex < 16) {
/*  97 */             bold = false;
/*  98 */             italic = false;
/*  99 */             underline = false;
/* 100 */             strikethrough = false;
/* 101 */             GlStateManager.func_179144_i(this.tex.func_110552_b());
/* 102 */             currentData = this.charData;
/* 103 */             if (colorIndex < 0 || colorIndex > 15) {
/* 104 */               colorIndex = 15;
/*     */             }
/* 106 */             if (shadow) {
/* 107 */               colorIndex += 16;
/*     */             }
/* 109 */             int colorcode = this.colorCode[colorIndex];
/* 110 */             GlStateManager.func_179131_c((colorcode >> 16 & 0xFF) / 255.0F, (colorcode >> 8 & 0xFF) / 255.0F, (colorcode & 0xFF) / 255.0F, alpha);
/* 111 */           } else if (colorIndex == 17) {
/* 112 */             bold = true;
/* 113 */             if (italic) {
/* 114 */               GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
/* 115 */               currentData = this.boldItalicChars;
/*     */             } else {
/* 117 */               GlStateManager.func_179144_i(this.texBold.func_110552_b());
/* 118 */               currentData = this.boldChars;
/*     */             } 
/* 120 */           } else if (colorIndex == 18) {
/* 121 */             strikethrough = true;
/* 122 */           } else if (colorIndex == 19) {
/* 123 */             underline = true;
/* 124 */           } else if (colorIndex == 20) {
/* 125 */             italic = true;
/* 126 */             if (bold) {
/* 127 */               GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
/* 128 */               currentData = this.boldItalicChars;
/*     */             } else {
/* 130 */               GlStateManager.func_179144_i(this.texItalic.func_110552_b());
/* 131 */               currentData = this.italicChars;
/*     */             } 
/* 133 */           } else if (colorIndex == 21) {
/* 134 */             bold = false;
/* 135 */             italic = false;
/* 136 */             underline = false;
/* 137 */             strikethrough = false;
/* 138 */             GlStateManager.func_179131_c((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
/* 139 */             GlStateManager.func_179144_i(this.tex.func_110552_b());
/* 140 */             currentData = this.charData;
/*     */           } 
/* 142 */           i++;
/*     */         
/*     */         }
/* 145 */         else if (character < currentData.length && character >= '\000') {
/* 146 */           GL11.glBegin(4);
/* 147 */           drawChar(currentData, character, (float)x, (float)y);
/* 148 */           GL11.glEnd();
/* 149 */           if (strikethrough) {
/* 150 */             drawLine(x, y + ((currentData[character]).height / 2), x + (currentData[character]).width - 8.0D, y + ((currentData[character]).height / 2), 1.0F);
/*     */           }
/* 152 */           if (underline) {
/* 153 */             drawLine(x, y + (currentData[character]).height - 2.0D, x + (currentData[character]).width - 8.0D, y + (currentData[character]).height - 2.0D, 1.0F);
/*     */           }
/* 155 */           x += ((currentData[character]).width - 8 + this.charOffset);
/*     */         } 
/* 157 */       }  GL11.glHint(3155, 4352);
/* 158 */       GL11.glPopMatrix();
/*     */     } 
/* 160 */     return (float)x / 2.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStringWidth(String text) {
/* 165 */     if (text == null) {
/* 166 */       return 0;
/*     */     }
/* 168 */     int width = 0;
/* 169 */     CFont.CharData[] currentData = this.charData;
/* 170 */     boolean bold = false;
/* 171 */     boolean italic = false;
/* 172 */     int size = text.length();
/* 173 */     for (int i = 0; i < size; i++) {
/* 174 */       char character = text.charAt(i);
/* 175 */       if (character == '§' && i < size) {
/* 176 */         int colorIndex = "0123456789abcdefklmnor".indexOf(character);
/* 177 */         if (colorIndex < 16) {
/* 178 */           bold = false;
/* 179 */           italic = false;
/* 180 */         } else if (colorIndex == 17) {
/* 181 */           bold = true;
/* 182 */           currentData = italic ? this.boldItalicChars : this.boldChars;
/* 183 */         } else if (colorIndex == 20) {
/* 184 */           italic = true;
/* 185 */           currentData = bold ? this.boldItalicChars : this.italicChars;
/* 186 */         } else if (colorIndex == 21) {
/* 187 */           bold = false;
/* 188 */           italic = false;
/* 189 */           currentData = this.charData;
/*     */         } 
/* 191 */         i++;
/*     */       
/*     */       }
/* 194 */       else if (character < currentData.length && character >= '\000') {
/* 195 */         width += (currentData[character]).width - 8 + this.charOffset;
/*     */       } 
/* 197 */     }  return width / 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFont(Font font) {
/* 202 */     super.setFont(font);
/* 203 */     setupBoldItalicIDs();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAntiAlias(boolean antiAlias) {
/* 208 */     super.setAntiAlias(antiAlias);
/* 209 */     setupBoldItalicIDs();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFractionalMetrics(boolean fractionalMetrics) {
/* 214 */     super.setFractionalMetrics(fractionalMetrics);
/* 215 */     setupBoldItalicIDs();
/*     */   }
/*     */   
/*     */   private void setupBoldItalicIDs() {
/* 219 */     this.texBold = setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
/* 220 */     this.texItalic = setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
/* 221 */     this.texItalicBold = setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
/*     */   }
/*     */   
/*     */   private void drawLine(double x, double y, double x1, double y1, float width) {
/* 225 */     GL11.glDisable(3553);
/* 226 */     GL11.glLineWidth(width);
/* 227 */     GL11.glBegin(1);
/* 228 */     GL11.glVertex2d(x, y);
/* 229 */     GL11.glVertex2d(x1, y1);
/* 230 */     GL11.glEnd();
/* 231 */     GL11.glEnable(3553);
/*     */   }
/*     */   
/*     */   public List<String> wrapWords(String text, double width) {
/* 235 */     ArrayList<String> finalWords = new ArrayList<>();
/* 236 */     if (getStringWidth(text) > width) {
/* 237 */       String[] words = text.split(" ");
/* 238 */       String currentWord = "";
/* 239 */       char lastColorCode = Character.MAX_VALUE;
/* 240 */       for (String word : words) {
/* 241 */         for (int i = 0; i < (word.toCharArray()).length; i++) {
/* 242 */           char c = word.toCharArray()[i];
/* 243 */           if (c == '§' && i < (word.toCharArray()).length - 1)
/* 244 */             lastColorCode = word.toCharArray()[i + 1]; 
/*     */         } 
/* 246 */         StringBuilder stringBuilder = new StringBuilder();
/* 247 */         if (getStringWidth(stringBuilder.append(currentWord).append(word).append(" ").toString()) < width) {
/* 248 */           currentWord = currentWord + word + " ";
/*     */         } else {
/*     */           
/* 251 */           finalWords.add(currentWord);
/* 252 */           currentWord = "§" + lastColorCode + word + " ";
/*     */         } 
/* 254 */       }  if (currentWord.length() > 0) {
/* 255 */         if (getStringWidth(currentWord) < width) {
/* 256 */           finalWords.add("§" + lastColorCode + currentWord + " ");
/* 257 */           currentWord = "";
/*     */         } else {
/* 259 */           for (String s : formatString(currentWord, width)) {
/* 260 */             finalWords.add(s);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } else {
/* 265 */       finalWords.add(text);
/*     */     } 
/* 267 */     return finalWords;
/*     */   }
/*     */   
/*     */   public List<String> formatString(String string, double width) {
/* 271 */     ArrayList<String> finalWords = new ArrayList<>();
/* 272 */     String currentWord = "";
/* 273 */     char lastColorCode = Character.MAX_VALUE;
/* 274 */     char[] chars = string.toCharArray();
/* 275 */     for (int i = 0; i < chars.length; i++) {
/* 276 */       char c = chars[i];
/* 277 */       if (c == '§' && i < chars.length - 1) {
/* 278 */         lastColorCode = chars[i + 1];
/*     */       }
/* 280 */       StringBuilder stringBuilder = new StringBuilder();
/* 281 */       if (getStringWidth(stringBuilder.append(currentWord).append(c).toString()) < width) {
/* 282 */         currentWord = currentWord + c;
/*     */       } else {
/*     */         
/* 285 */         finalWords.add(currentWord);
/* 286 */         currentWord = "§" + lastColorCode + c;
/*     */       } 
/* 288 */     }  if (currentWord.length() > 0) {
/* 289 */       finalWords.add(currentWord);
/*     */     }
/* 291 */     return finalWords;
/*     */   }
/*     */   
/*     */   private void setupMinecraftColorcodes() {
/* 295 */     for (int index = 0; index < 32; index++) {
/* 296 */       int noClue = (index >> 3 & 0x1) * 85;
/* 297 */       int red = (index >> 2 & 0x1) * 170 + noClue;
/* 298 */       int green = (index >> 1 & 0x1) * 170 + noClue;
/* 299 */       int blue = (index >> 0 & 0x1) * 170 + noClue;
/* 300 */       if (index == 6) {
/* 301 */         red += 85;
/*     */       }
/* 303 */       if (index >= 16) {
/* 304 */         red /= 4;
/* 305 */         green /= 4;
/* 306 */         blue /= 4;
/*     */       } 
/* 308 */       this.colorCode[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\gui\font\CustomFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */