/*     */ package me.mohalk.banzem.manager;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParser;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import me.mohalk.banzem.Banzem;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.setting.EnumConverter;
/*     */ import me.mohalk.banzem.features.setting.Setting;
/*     */ 
/*     */ public class ConfigManager implements Util {
/*  20 */   public ArrayList<Feature> features = new ArrayList<>();
/*  21 */   public String config = "QuartzHack/config/"; public boolean loadingConfig;
/*     */   public boolean savingConfig;
/*     */   
/*     */   public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
/*     */     String str;
/*  26 */     switch (setting.getType()) {
/*     */       case "Boolean":
/*  28 */         setting.setValue(Boolean.valueOf(element.getAsBoolean()));
/*     */         return;
/*     */       
/*     */       case "Double":
/*  32 */         setting.setValue(Double.valueOf(element.getAsDouble()));
/*     */         return;
/*     */       
/*     */       case "Float":
/*  36 */         setting.setValue(Float.valueOf(element.getAsFloat()));
/*     */         return;
/*     */       
/*     */       case "Integer":
/*  40 */         setting.setValue(Integer.valueOf(element.getAsInt()));
/*     */         return;
/*     */       
/*     */       case "String":
/*  44 */         str = element.getAsString();
/*  45 */         setting.setValue(str.replace("_", " "));
/*     */         return;
/*     */       
/*     */       case "Bind":
/*  49 */         setting.setValue((new Bind.BindConverter()).doBackward(element));
/*     */         return;
/*     */       
/*     */       case "Enum":
/*     */         try {
/*  54 */           EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
/*  55 */           Enum value = converter.doBackward(element);
/*  56 */           setting.setValue((value == null) ? setting.getDefaultValue() : value);
/*  57 */         } catch (Exception exception) {}
/*     */         return;
/*     */     } 
/*     */ 
/*     */     
/*  62 */     Banzem.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void loadFile(JsonObject input, Feature feature) {
/*  68 */     for (Map.Entry entry : input.entrySet()) {
/*  69 */       String settingName = (String)entry.getKey();
/*  70 */       JsonElement element = (JsonElement)entry.getValue();
/*  71 */       if (feature instanceof FriendManager) {
/*     */         try {
/*  73 */           Banzem.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
/*  74 */         } catch (Exception e) {
/*  75 */           e.printStackTrace();
/*     */         } 
/*     */       } else {
/*  78 */         boolean settingFound = false;
/*  79 */         for (Setting setting : feature.getSettings()) {
/*  80 */           if (!settingName.equals(setting.getName()))
/*     */             continue;  try {
/*  82 */             setValueFromJson(feature, setting, element);
/*  83 */           } catch (Exception e) {
/*  84 */             e.printStackTrace();
/*     */           } 
/*  86 */           settingFound = true;
/*     */         } 
/*  88 */         if (settingFound)
/*     */           continue; 
/*  90 */       }  if (feature instanceof XRay) {
/*  91 */         feature.register(new Setting(settingName, Boolean.valueOf(true), v -> ((Boolean)((XRay)feature).showBlocks.getValue()).booleanValue()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void loadConfig(String name) {
/*  98 */     this.loadingConfig = true;
/*  99 */     List<File> files = (List<File>)Arrays.<Object>stream((Object[])Objects.requireNonNull((new File("QuartzHack")).listFiles())).filter(File::isDirectory).collect(Collectors.toList());
/* 100 */     this.config = files.contains(new File("QuartzHack/" + name + "/")) ? ("QuartzHack/" + name + "/") : "QuartzHack/config/";
/* 101 */     Banzem.friendManager.onLoad();
/* 102 */     for (Feature feature : this.features) {
/*     */       try {
/* 104 */         loadSettings(feature);
/* 105 */       } catch (IOException e) {
/* 106 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 109 */     saveCurrentConfig();
/* 110 */     this.loadingConfig = false;
/*     */   }
/*     */   
/*     */   public void saveConfig(String name) {
/* 114 */     this.savingConfig = true;
/* 115 */     this.config = "QuartzHack/" + name + "/";
/* 116 */     File path = new File(this.config);
/* 117 */     if (!path.exists()) {
/* 118 */       path.mkdir();
/*     */     }
/* 120 */     Banzem.friendManager.saveFriends();
/* 121 */     for (Feature feature : this.features) {
/*     */       try {
/* 123 */         saveSettings(feature);
/* 124 */       } catch (IOException e) {
/* 125 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 128 */     saveCurrentConfig();
/* 129 */     this.savingConfig = false;
/*     */   }
/*     */   
/*     */   public void saveCurrentConfig() {
/* 133 */     File currentConfig = new File("QuartzHack/currentconfig.txt");
/*     */     try {
/* 135 */       if (currentConfig.exists()) {
/* 136 */         FileWriter writer = new FileWriter(currentConfig);
/* 137 */         String tempConfig = this.config.replaceAll("/", "");
/* 138 */         writer.write(tempConfig.replaceAll("QuartzHack", ""));
/* 139 */         writer.close();
/*     */       } else {
/* 141 */         currentConfig.createNewFile();
/* 142 */         FileWriter writer = new FileWriter(currentConfig);
/* 143 */         String tempConfig = this.config.replaceAll("/", "");
/* 144 */         writer.write(tempConfig.replaceAll("QuartzHack", ""));
/* 145 */         writer.close();
/*     */       } 
/* 147 */     } catch (Exception e) {
/* 148 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public String loadCurrentConfig() {
/* 153 */     File currentConfig = new File("QuartzHack/currentconfig.txt");
/* 154 */     String name = "config";
/*     */     try {
/* 156 */       if (currentConfig.exists()) {
/* 157 */         Scanner reader = new Scanner(currentConfig);
/* 158 */         while (reader.hasNextLine()) {
/* 159 */           name = reader.nextLine();
/*     */         }
/* 161 */         reader.close();
/*     */       } 
/* 163 */     } catch (Exception e) {
/* 164 */       e.printStackTrace();
/*     */     } 
/* 166 */     return name;
/*     */   }
/*     */   
/*     */   public void resetConfig(boolean saveConfig, String name) {
/* 170 */     for (Feature feature : this.features) {
/* 171 */       feature.reset();
/*     */     }
/* 173 */     if (saveConfig) {
/* 174 */       saveConfig(name);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void saveSettings(Feature feature) throws IOException {
/* 181 */     JsonObject object = new JsonObject();
/* 182 */     File directory = new File(this.config + getDirectory(feature));
/* 183 */     if (!directory.exists())
/* 184 */       directory.mkdir();  String featureName;
/*     */     Path outputFile;
/* 186 */     if (!Files.exists(outputFile = Paths.get(featureName = this.config + getDirectory(feature) + feature.getName() + ".json", new String[0]), new java.nio.file.LinkOption[0])) {
/* 187 */       Files.createFile(outputFile, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     }
/* 189 */     Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
/* 190 */     String json = gson.toJson((JsonElement)writeSettings(feature));
/* 191 */     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile, new java.nio.file.OpenOption[0])));
/* 192 */     writer.write(json);
/* 193 */     writer.close();
/*     */   }
/*     */   
/*     */   public void init() {
/* 197 */     this.features.addAll(Banzem.moduleManager.modules);
/* 198 */     this.features.add(Banzem.friendManager);
/* 199 */     String name = loadCurrentConfig();
/* 200 */     loadConfig(name);
/* 201 */     Banzem.LOGGER.info("Config loaded.");
/*     */   }
/*     */   
/*     */   private void loadSettings(Feature feature) throws IOException {
/* 205 */     String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
/* 206 */     Path featurePath = Paths.get(featureName, new String[0]);
/* 207 */     if (!Files.exists(featurePath, new java.nio.file.LinkOption[0])) {
/*     */       return;
/*     */     }
/* 210 */     loadPath(featurePath, feature);
/*     */   }
/*     */   
/*     */   private void loadPath(Path path, Feature feature) throws IOException {
/* 214 */     InputStream stream = Files.newInputStream(path, new java.nio.file.OpenOption[0]);
/*     */     try {
/* 216 */       loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
/* 217 */     } catch (IllegalStateException e) {
/* 218 */       Banzem.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
/* 219 */       loadFile(new JsonObject(), feature);
/*     */     } 
/* 221 */     stream.close();
/*     */   }
/*     */   
/*     */   public JsonObject writeSettings(Feature feature) {
/* 225 */     JsonObject object = new JsonObject();
/* 226 */     JsonParser jp = new JsonParser();
/* 227 */     for (Setting setting : feature.getSettings()) {
/* 228 */       if (setting.isEnumSetting()) {
/* 229 */         EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
/* 230 */         object.add(setting.getName(), converter.doForward((Enum)setting.getValue()));
/*     */         continue;
/*     */       } 
/* 233 */       if (setting.isStringSetting()) {
/* 234 */         String str = (String)setting.getValue();
/* 235 */         setting.setValue(str.replace(" ", "_"));
/*     */       } 
/*     */       try {
/* 238 */         object.add(setting.getName(), jp.parse(setting.getValueAsString()));
/* 239 */       } catch (Exception e) {
/* 240 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 243 */     return object;
/*     */   }
/*     */   
/*     */   public String getDirectory(Feature feature) {
/* 247 */     String directory = "";
/* 248 */     if (feature instanceof Module) {
/* 249 */       directory = directory + ((Module)feature).getCategory().getName() + "/";
/*     */     }
/* 251 */     return directory;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\ConfigManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */