/*     */ package me.mohalk.banzem.manager;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import me.mohalk.banzem.features.modules.Module;
/*     */ 
/*     */ public class FileManager extends Feature {
/*  19 */   private final Path base = getMkDirectory(getRoot(), new String[] { "QuartzHack" });
/*  20 */   private final Path config = getMkDirectory(this.base, new String[] { "config" });
/*     */   
/*     */   public FileManager() {
/*  23 */     getMkDirectory(this.base, new String[] { "util" });
/*  24 */     for (Module.Category category : Banzem.moduleManager.getCategories()) {
/*  25 */       getMkDirectory(this.config, new String[] { category.getName() });
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean appendTextFile(String data, String file) {
/*     */     try {
/*  31 */       Path path = Paths.get(file, new String[0]);
/*  32 */       Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, new OpenOption[] { Files.exists(path, new java.nio.file.LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE });
/*  33 */     } catch (IOException e) {
/*  34 */       System.out.println("WARNING: Unable to write file: " + file);
/*  35 */       return false;
/*     */     } 
/*  37 */     return true;
/*     */   }
/*     */   
/*     */   public static List<String> readTextFileAllLines(String file) {
/*     */     try {
/*  42 */       Path path = Paths.get(file, new String[0]);
/*  43 */       return Files.readAllLines(path, StandardCharsets.UTF_8);
/*  44 */     } catch (IOException e) {
/*  45 */       System.out.println("WARNING: Unable to read file, creating new file: " + file);
/*  46 */       appendTextFile("", file);
/*  47 */       return Collections.emptyList();
/*     */     } 
/*     */   }
/*     */   
/*     */   private String[] expandPath(String fullPath) {
/*  52 */     return fullPath.split(":?\\\\\\\\|\\/");
/*     */   }
/*     */   
/*     */   private Stream<String> expandPaths(String... paths) {
/*  56 */     return Arrays.<String>stream(paths).map(this::expandPath).flatMap(Arrays::stream);
/*     */   }
/*     */   
/*     */   private Path lookupPath(Path root, String... paths) {
/*  60 */     return Paths.get(root.toString(), paths);
/*     */   }
/*     */   
/*     */   private Path getRoot() {
/*  64 */     return Paths.get("", new String[0]);
/*     */   }
/*     */   
/*     */   private void createDirectory(Path dir) {
/*     */     try {
/*  69 */       if (!Files.isDirectory(dir, new java.nio.file.LinkOption[0])) {
/*  70 */         if (Files.exists(dir, new java.nio.file.LinkOption[0])) {
/*  71 */           Files.delete(dir);
/*     */         }
/*  73 */         Files.createDirectories(dir, (FileAttribute<?>[])new FileAttribute[0]);
/*     */       } 
/*  75 */     } catch (IOException e) {
/*  76 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   private Path getMkDirectory(Path parent, String... paths) {
/*  81 */     if (paths.length < 1) {
/*  82 */       return parent;
/*     */     }
/*  84 */     Path dir = lookupPath(parent, paths);
/*  85 */     createDirectory(dir);
/*  86 */     return dir;
/*     */   }
/*     */   
/*     */   public Path getBasePath() {
/*  90 */     return this.base;
/*     */   }
/*     */   
/*     */   public Path getBaseResolve(String... paths) {
/*  94 */     String[] names = expandPaths(paths).<String>toArray(x$0 -> new String[x$0]);
/*  95 */     if (names.length < 1) {
/*  96 */       throw new IllegalArgumentException("missing path");
/*     */     }
/*  98 */     return lookupPath(getBasePath(), names);
/*     */   }
/*     */   
/*     */   public Path getMkBaseResolve(String... paths) {
/* 102 */     Path path = getBaseResolve(paths);
/* 103 */     createDirectory(path.getParent());
/* 104 */     return path;
/*     */   }
/*     */   
/*     */   public Path getConfig() {
/* 108 */     return getBasePath().resolve("config");
/*     */   }
/*     */   
/*     */   public Path getCache() {
/* 112 */     return getBasePath().resolve("cache");
/*     */   }
/*     */   
/*     */   public Path getNotebot() {
/* 116 */     return getBasePath().resolve("notebot");
/*     */   }
/*     */   
/*     */   public Path getMkBaseDirectory(String... names) {
/* 120 */     return getMkDirectory(getBasePath(), new String[] { expandPaths(names).collect(Collectors.joining(File.separator)) });
/*     */   }
/*     */   
/*     */   public Path getMkConfigDirectory(String... names) {
/* 124 */     return getMkDirectory(getConfig(), new String[] { expandPaths(names).collect(Collectors.joining(File.separator)) });
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\manager\FileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */