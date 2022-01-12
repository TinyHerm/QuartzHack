/*     */ package me.mohalk.banzem.features.setting;
/*     */ 
/*     */ import java.util.function.Predicate;
/*     */ import me.mohalk.banzem.event.events.ClientEvent;
/*     */ import me.mohalk.banzem.features.Feature;
/*     */ import net.minecraftforge.common.MinecraftForge;
/*     */ import net.minecraftforge.fml.common.eventhandler.Event;
/*     */ 
/*     */ public class Setting<T> {
/*     */   private final String name;
/*     */   private final T defaultValue;
/*     */   private T value;
/*     */   private T plannedValue;
/*     */   private T min;
/*     */   private T max;
/*     */   private boolean hasRestriction;
/*     */   private boolean shouldRenderStringName;
/*     */   private Predicate<T> visibility;
/*     */   private String description;
/*     */   private Feature feature;
/*     */   
/*     */   public Setting(String name, T defaultValue) {
/*  23 */     this.name = name;
/*  24 */     this.defaultValue = defaultValue;
/*  25 */     this.value = defaultValue;
/*  26 */     this.plannedValue = defaultValue;
/*  27 */     this.description = "";
/*     */   }
/*     */   
/*     */   public Setting(String name, T defaultValue, String description) {
/*  31 */     this.name = name;
/*  32 */     this.defaultValue = defaultValue;
/*  33 */     this.value = defaultValue;
/*  34 */     this.plannedValue = defaultValue;
/*  35 */     this.description = description;
/*     */   }
/*     */   
/*     */   public Setting(String name, T defaultValue, T min, T max, String description) {
/*  39 */     this.name = name;
/*  40 */     this.defaultValue = defaultValue;
/*  41 */     this.value = defaultValue;
/*  42 */     this.min = min;
/*  43 */     this.max = max;
/*  44 */     this.plannedValue = defaultValue;
/*  45 */     this.description = description;
/*  46 */     this.hasRestriction = true;
/*     */   }
/*     */   
/*     */   public Setting(String name, T defaultValue, T min, T max) {
/*  50 */     this.name = name;
/*  51 */     this.defaultValue = defaultValue;
/*  52 */     this.value = defaultValue;
/*  53 */     this.min = min;
/*  54 */     this.max = max;
/*  55 */     this.plannedValue = defaultValue;
/*  56 */     this.description = "";
/*  57 */     this.hasRestriction = true;
/*     */   }
/*     */   
/*     */   public Setting(String name, T defaultValue, T min, T max, Predicate<T> visibility, String description) {
/*  61 */     this.name = name;
/*  62 */     this.defaultValue = defaultValue;
/*  63 */     this.value = defaultValue;
/*  64 */     this.min = min;
/*  65 */     this.max = max;
/*  66 */     this.plannedValue = defaultValue;
/*  67 */     this.visibility = visibility;
/*  68 */     this.description = description;
/*  69 */     this.hasRestriction = true;
/*     */   }
/*     */   
/*     */   public Setting(String name, T defaultValue, T min, T max, Predicate<T> visibility) {
/*  73 */     this.name = name;
/*  74 */     this.defaultValue = defaultValue;
/*  75 */     this.value = defaultValue;
/*  76 */     this.min = min;
/*  77 */     this.max = max;
/*  78 */     this.plannedValue = defaultValue;
/*  79 */     this.visibility = visibility;
/*  80 */     this.description = "";
/*  81 */     this.hasRestriction = true;
/*     */   }
/*     */   
/*     */   public Setting(String name, T defaultValue, Predicate<T> visibility) {
/*  85 */     this.name = name;
/*  86 */     this.defaultValue = defaultValue;
/*  87 */     this.value = defaultValue;
/*  88 */     this.visibility = visibility;
/*  89 */     this.plannedValue = defaultValue;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  93 */     return this.name;
/*     */   }
/*     */   
/*     */   public T getValue() {
/*  97 */     return this.value;
/*     */   }
/*     */   
/*     */   public void setValue(T value) {
/* 101 */     setPlannedValue(value);
/* 102 */     if (this.hasRestriction) {
/* 103 */       if (((Number)this.min).floatValue() > ((Number)value).floatValue()) {
/* 104 */         setPlannedValue(this.min);
/*     */       }
/* 106 */       if (((Number)this.max).floatValue() < ((Number)value).floatValue()) {
/* 107 */         setPlannedValue(this.max);
/*     */       }
/*     */     } 
/* 110 */     ClientEvent event = new ClientEvent(this);
/* 111 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 112 */     if (!event.isCanceled()) {
/* 113 */       this.value = this.plannedValue;
/*     */     } else {
/* 115 */       this.plannedValue = this.value;
/*     */     } 
/*     */   }
/*     */   
/*     */   public T getPlannedValue() {
/* 120 */     return this.plannedValue;
/*     */   }
/*     */   
/*     */   public void setPlannedValue(T value) {
/* 124 */     this.plannedValue = value;
/*     */   }
/*     */   
/*     */   public T getMin() {
/* 128 */     return this.min;
/*     */   }
/*     */   
/*     */   public void setMin(T min) {
/* 132 */     this.min = min;
/*     */   }
/*     */   
/*     */   public T getMax() {
/* 136 */     return this.max;
/*     */   }
/*     */   
/*     */   public void setMax(T max) {
/* 140 */     this.max = max;
/*     */   }
/*     */   
/*     */   public void setValueNoEvent(T value) {
/* 144 */     setPlannedValue(value);
/* 145 */     if (this.hasRestriction) {
/* 146 */       if (((Number)this.min).floatValue() > ((Number)value).floatValue()) {
/* 147 */         setPlannedValue(this.min);
/*     */       }
/* 149 */       if (((Number)this.max).floatValue() < ((Number)value).floatValue()) {
/* 150 */         setPlannedValue(this.max);
/*     */       }
/*     */     } 
/* 153 */     this.value = this.plannedValue;
/*     */   }
/*     */   
/*     */   public Feature getFeature() {
/* 157 */     return this.feature;
/*     */   }
/*     */   
/*     */   public void setFeature(Feature feature) {
/* 161 */     this.feature = feature;
/*     */   }
/*     */   
/*     */   public int getEnum(String input) {
/* 165 */     for (int i = 0; i < (this.value.getClass().getEnumConstants()).length; ) {
/* 166 */       Enum e = (Enum)this.value.getClass().getEnumConstants()[i];
/* 167 */       if (!e.name().equalsIgnoreCase(input)) { i++; continue; }
/* 168 */        return i;
/*     */     } 
/* 170 */     return -1;
/*     */   }
/*     */   
/*     */   public void setEnumValue(String value) {
/* 174 */     for (Enum e : (Enum[])((Enum)this.value).getClass().getEnumConstants()) {
/* 175 */       if (e.name().equalsIgnoreCase(value))
/* 176 */         this.value = (T)e; 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String currentEnumName() {
/* 181 */     return EnumConverter.getProperName((Enum)this.value);
/*     */   }
/*     */   
/*     */   public int currentEnum() {
/* 185 */     return EnumConverter.currentEnum((Enum)this.value);
/*     */   }
/*     */   
/*     */   public void increaseEnum() {
/* 189 */     this.plannedValue = (T)EnumConverter.increaseEnum((Enum)this.value);
/* 190 */     ClientEvent event = new ClientEvent(this);
/* 191 */     MinecraftForge.EVENT_BUS.post((Event)event);
/* 192 */     if (!event.isCanceled()) {
/* 193 */       this.value = this.plannedValue;
/*     */     } else {
/* 195 */       this.plannedValue = this.value;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void increaseEnumNoEvent() {
/* 200 */     this.value = (T)EnumConverter.increaseEnum((Enum)this.value);
/*     */   }
/*     */   
/*     */   public String getType() {
/* 204 */     if (isEnumSetting()) {
/* 205 */       return "Enum";
/*     */     }
/* 207 */     return getClassName(this.defaultValue);
/*     */   }
/*     */   
/*     */   public <T> String getClassName(T value) {
/* 211 */     return value.getClass().getSimpleName();
/*     */   }
/*     */   
/*     */   public String getDescription() {
/* 215 */     if (this.description == null) {
/* 216 */       return "";
/*     */     }
/* 218 */     return this.description;
/*     */   }
/*     */   
/*     */   public boolean isNumberSetting() {
/* 222 */     return (this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float);
/*     */   }
/*     */   
/*     */   public boolean isEnumSetting() {
/* 226 */     return (!isNumberSetting() && !(this.value instanceof String) && !(this.value instanceof Bind) && !(this.value instanceof Character) && !(this.value instanceof Boolean));
/*     */   }
/*     */   
/*     */   public boolean isStringSetting() {
/* 230 */     return this.value instanceof String;
/*     */   }
/*     */   
/*     */   public T getDefaultValue() {
/* 234 */     return this.defaultValue;
/*     */   }
/*     */   
/*     */   public String getValueAsString() {
/* 238 */     return this.value.toString();
/*     */   }
/*     */   
/*     */   public boolean hasRestriction() {
/* 242 */     return this.hasRestriction;
/*     */   }
/*     */   
/*     */   public void setVisibility(Predicate<T> visibility) {
/* 246 */     this.visibility = visibility;
/*     */   }
/*     */   
/*     */   public Setting<T> setRenderName(boolean renderName) {
/* 250 */     this.shouldRenderStringName = renderName;
/* 251 */     return this;
/*     */   }
/*     */   
/*     */   public boolean shouldRenderName() {
/* 255 */     if (!isStringSetting()) {
/* 256 */       return true;
/*     */     }
/* 258 */     return this.shouldRenderStringName;
/*     */   }
/*     */   
/*     */   public boolean isVisible() {
/* 262 */     if (this.visibility == null) {
/* 263 */       return true;
/*     */     }
/* 265 */     return this.visibility.test(getValue());
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\setting\Setting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */