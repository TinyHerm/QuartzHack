/*     */ package net.jodah.typetools;
/*     */ 
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import sun.misc.Unsafe;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TypeResolver
/*     */ {
/*  53 */   private static final Map<Class<?>, Reference<Map<TypeVariable<?>, Type>>> TYPE_VARIABLE_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
/*     */   private static volatile boolean CACHE_ENABLED = true;
/*     */   private static boolean RESOLVES_LAMBDAS;
/*     */   private static Object JAVA_LANG_ACCESS;
/*     */   private static Method GET_CONSTANT_POOL;
/*     */   private static Method GET_CONSTANT_POOL_SIZE;
/*     */   private static Method GET_CONSTANT_POOL_METHOD_AT;
/*  60 */   private static final Map<String, Method> OBJECT_METHODS = new HashMap<>();
/*     */ 
/*     */   
/*     */   private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS;
/*     */   
/*  65 */   private static final Double JAVA_VERSION = Double.valueOf(Double.parseDouble(System.getProperty("java.specification.version", "0"))); static { try {
/*     */       Class<?> sharedSecretsClass;
/*     */       AccessMaker accessSetter;
/*  68 */       final Unsafe unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedExceptionAction<Unsafe>()
/*     */           {
/*     */             public Unsafe run() throws Exception {
/*  71 */               Field f = Unsafe.class.getDeclaredField("theUnsafe");
/*  72 */               f.setAccessible(true);
/*     */               
/*  74 */               return (Unsafe)f.get((Object)null);
/*     */             }
/*     */           });
/*     */ 
/*     */ 
/*     */       
/*  80 */       if (JAVA_VERSION.doubleValue() < 9.0D) {
/*  81 */         sharedSecretsClass = Class.forName("sun.misc.SharedSecrets");
/*     */         
/*  83 */         accessSetter = new AccessMaker()
/*     */           {
/*     */             public void makeAccessible(AccessibleObject accessibleObject) {
/*  86 */               accessibleObject.setAccessible(true);
/*     */             }
/*     */           };
/*  89 */       } else if (JAVA_VERSION.doubleValue() < 12.0D) {
/*     */         try {
/*  91 */           sharedSecretsClass = Class.forName("jdk.internal.misc.SharedSecrets");
/*  92 */         } catch (ClassNotFoundException e) {
/*     */           
/*  94 */           sharedSecretsClass = Class.forName("jdk.internal.access.SharedSecrets");
/*     */         } 
/*     */         
/*  97 */         Field overrideField = AccessibleObject.class.getDeclaredField("override");
/*  98 */         final long overrideFieldOffset = unsafe.objectFieldOffset(overrideField);
/*  99 */         accessSetter = new AccessMaker()
/*     */           {
/*     */             public void makeAccessible(AccessibleObject accessibleObject) {
/* 102 */               unsafe.putBoolean(accessibleObject, overrideFieldOffset, true);
/*     */             }
/*     */           };
/*     */       } else {
/* 106 */         sharedSecretsClass = Class.forName("jdk.internal.access.SharedSecrets");
/*     */ 
/*     */         
/* 109 */         Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
/* 110 */         long implLookupFieldOffset = unsafe.staticFieldOffset(implLookupField);
/* 111 */         Object lookupStaticFieldBase = unsafe.staticFieldBase(implLookupField);
/* 112 */         MethodHandles.Lookup implLookup = (MethodHandles.Lookup)unsafe.getObject(lookupStaticFieldBase, implLookupFieldOffset);
/* 113 */         final MethodHandle overrideSetter = implLookup.findSetter(AccessibleObject.class, "override", boolean.class);
/* 114 */         accessSetter = new AccessMaker()
/*     */           {
/*     */             public void makeAccessible(AccessibleObject object) throws Throwable {
/* 117 */               overrideSetter.invokeWithArguments(new Object[] { object, Boolean.valueOf(true) });
/*     */             }
/*     */           };
/*     */       } 
/* 121 */       Method javaLangAccessGetter = sharedSecretsClass.getMethod("getJavaLangAccess", new Class[0]);
/* 122 */       accessSetter.makeAccessible(javaLangAccessGetter);
/* 123 */       JAVA_LANG_ACCESS = javaLangAccessGetter.invoke(null, new Object[0]);
/* 124 */       GET_CONSTANT_POOL = JAVA_LANG_ACCESS.getClass().getMethod("getConstantPool", new Class[] { Class.class });
/*     */       
/* 126 */       String constantPoolName = (JAVA_VERSION.doubleValue() < 9.0D) ? "sun.reflect.ConstantPool" : "jdk.internal.reflect.ConstantPool";
/* 127 */       Class<?> constantPoolClass = Class.forName(constantPoolName);
/* 128 */       GET_CONSTANT_POOL_SIZE = constantPoolClass.getDeclaredMethod("getSize", new Class[0]);
/* 129 */       GET_CONSTANT_POOL_METHOD_AT = constantPoolClass.getDeclaredMethod("getMethodAt", new Class[] { int.class });
/*     */ 
/*     */       
/* 132 */       accessSetter.makeAccessible(GET_CONSTANT_POOL);
/* 133 */       accessSetter.makeAccessible(GET_CONSTANT_POOL_SIZE);
/* 134 */       accessSetter.makeAccessible(GET_CONSTANT_POOL_METHOD_AT);
/*     */ 
/*     */ 
/*     */       
/* 138 */       Object constantPool = GET_CONSTANT_POOL.invoke(JAVA_LANG_ACCESS, new Object[] { Object.class });
/* 139 */       GET_CONSTANT_POOL_SIZE.invoke(constantPool, new Object[0]);
/*     */       
/* 141 */       for (Method method : Object.class.getDeclaredMethods()) {
/* 142 */         OBJECT_METHODS.put(method.getName(), method);
/*     */       }
/* 144 */       RESOLVES_LAMBDAS = true;
/* 145 */     } catch (Throwable throwable) {}
/*     */ 
/*     */     
/* 148 */     Map<Class<?>, Class<?>> types = new HashMap<>();
/* 149 */     types.put(boolean.class, Boolean.class);
/* 150 */     types.put(byte.class, Byte.class);
/* 151 */     types.put(char.class, Character.class);
/* 152 */     types.put(double.class, Double.class);
/* 153 */     types.put(float.class, Float.class);
/* 154 */     types.put(int.class, Integer.class);
/* 155 */     types.put(long.class, Long.class);
/* 156 */     types.put(short.class, Short.class);
/* 157 */     types.put(void.class, Void.class);
/* 158 */     PRIMITIVE_WRAPPERS = Collections.unmodifiableMap(types); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static interface AccessMaker
/*     */   {
/*     */     void makeAccessible(AccessibleObject param1AccessibleObject) throws Throwable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Unknown {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void enableCache() {
/* 178 */     CACHE_ENABLED = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void disableCache() {
/* 185 */     TYPE_VARIABLE_CACHE.clear();
/* 186 */     CACHE_ENABLED = false;
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
/*     */   public static <T, S extends T> Class<?> resolveRawArgument(Class<T> type, Class<S> subType) {
/* 199 */     return resolveRawArgument(resolveGenericType(type, subType), subType);
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
/*     */   public static Class<?> resolveRawArgument(Type genericType, Class<?> subType) {
/* 213 */     Class<?>[] arguments = resolveRawArguments(genericType, subType);
/* 214 */     if (arguments == null) {
/* 215 */       return Unknown.class;
/*     */     }
/* 217 */     if (arguments.length != 1) {
/* 218 */       throw new IllegalArgumentException("Expected 1 argument for generic type " + genericType + " but found " + arguments.length);
/*     */     }
/*     */     
/* 221 */     return arguments[0];
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
/*     */   public static <T, S extends T> Class<?>[] resolveRawArguments(Class<T> type, Class<S> subType) {
/* 235 */     return resolveRawArguments(resolveGenericType(type, subType), subType);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, S extends T> Type reify(Class<T> type, Class<S> context) {
/* 257 */     return reify(resolveGenericType(type, context), getTypeVariableMap(context, null));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type reify(Type type, Class<?> context) {
/* 301 */     return reify(type, getTypeVariableMap(context, null));
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
/*     */   public static Type reify(Type type) {
/* 336 */     return reify(type, new HashMap<>(0));
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
/*     */   public static Class<?>[] resolveRawArguments(Type genericType, Class<?> subType) {
/* 350 */     Class<?>[] result = null;
/* 351 */     Class<?> functionalInterface = null;
/*     */ 
/*     */     
/* 354 */     if (RESOLVES_LAMBDAS && subType.isSynthetic()) {
/*     */ 
/*     */       
/* 357 */       Class<?> fi = (genericType instanceof ParameterizedType && ((ParameterizedType)genericType).getRawType() instanceof Class) ? (Class)((ParameterizedType)genericType).getRawType() : ((genericType instanceof Class) ? (Class)genericType : null);
/*     */       
/* 359 */       if (fi != null && fi.isInterface()) {
/* 360 */         functionalInterface = fi;
/*     */       }
/*     */     } 
/* 363 */     if (genericType instanceof ParameterizedType) {
/* 364 */       ParameterizedType paramType = (ParameterizedType)genericType;
/* 365 */       Type[] arguments = paramType.getActualTypeArguments();
/* 366 */       result = new Class[arguments.length];
/* 367 */       for (int i = 0; i < arguments.length; i++)
/* 368 */         result[i] = resolveRawClass(arguments[i], subType, functionalInterface); 
/* 369 */     } else if (genericType instanceof TypeVariable) {
/* 370 */       result = new Class[1];
/* 371 */       result[0] = resolveRawClass(genericType, subType, functionalInterface);
/* 372 */     } else if (genericType instanceof Class) {
/* 373 */       TypeVariable[] arrayOfTypeVariable = ((Class)genericType).getTypeParameters();
/* 374 */       result = new Class[arrayOfTypeVariable.length];
/* 375 */       for (int i = 0; i < arrayOfTypeVariable.length; i++) {
/* 376 */         result[i] = resolveRawClass(arrayOfTypeVariable[i], subType, functionalInterface);
/*     */       }
/*     */     } 
/* 379 */     return result;
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
/*     */   public static Type resolveGenericType(Class<?> type, Type subType) {
/*     */     Class<?> rawType;
/* 392 */     if (subType instanceof ParameterizedType) {
/* 393 */       rawType = (Class)((ParameterizedType)subType).getRawType();
/*     */     } else {
/* 395 */       rawType = (Class)subType;
/*     */     } 
/* 397 */     if (type.equals(rawType)) {
/* 398 */       return subType;
/*     */     }
/*     */     
/* 401 */     if (type.isInterface())
/* 402 */       for (Type superInterface : rawType.getGenericInterfaces()) {
/* 403 */         Type type1; if (superInterface != null && !superInterface.equals(Object.class) && (
/* 404 */           type1 = resolveGenericType(type, superInterface)) != null) {
/* 405 */           return type1;
/*     */         }
/*     */       }  
/* 408 */     Type superClass = rawType.getGenericSuperclass(); Type result;
/* 409 */     if (superClass != null && !superClass.equals(Object.class) && (
/* 410 */       result = resolveGenericType(type, superClass)) != null) {
/* 411 */       return result;
/*     */     }
/* 413 */     return null;
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
/*     */   public static Class<?> resolveRawClass(Type genericType, Class<?> subType) {
/* 425 */     return resolveRawClass(genericType, subType, null);
/*     */   }
/*     */   
/*     */   private static Class<?> resolveRawClass(Type genericType, Class<?> subType, Class<?> functionalInterface) {
/* 429 */     if (genericType instanceof Class)
/* 430 */       return (Class)genericType; 
/* 431 */     if (genericType instanceof ParameterizedType)
/* 432 */       return resolveRawClass(((ParameterizedType)genericType).getRawType(), subType, functionalInterface); 
/* 433 */     if (genericType instanceof GenericArrayType) {
/* 434 */       GenericArrayType arrayType = (GenericArrayType)genericType;
/* 435 */       Class<?> component = resolveRawClass(arrayType.getGenericComponentType(), subType, functionalInterface);
/* 436 */       return Array.newInstance(component, 0).getClass();
/* 437 */     }  if (genericType instanceof TypeVariable) {
/* 438 */       TypeVariable<?> variable = (TypeVariable)genericType;
/* 439 */       genericType = getTypeVariableMap(subType, functionalInterface).get(variable);
/*     */       
/* 441 */       genericType = (genericType == null) ? resolveBound(variable) : resolveRawClass(genericType, subType, functionalInterface);
/*     */     } 
/*     */     
/* 444 */     return (genericType instanceof Class) ? (Class)genericType : Unknown.class;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Type reify(Type genericType, Map<TypeVariable<?>, Type> typeVariableTypeMap) {
/* 449 */     if (genericType == null)
/* 450 */       return null; 
/* 451 */     if (genericType instanceof Class) {
/* 452 */       return genericType;
/*     */     }
/* 454 */     return reify(genericType, typeVariableTypeMap, new HashMap<>());
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
/*     */   private static Type reify(Type genericType, Map<TypeVariable<?>, Type> typeVariableMap, Map<ParameterizedType, ReifiedParameterizedType> partial) {
/* 466 */     if (genericType instanceof Class) {
/* 467 */       return genericType;
/*     */     }
/*     */     
/* 470 */     if (genericType instanceof ParameterizedType) {
/* 471 */       ParameterizedType parameterizedType = (ParameterizedType)genericType;
/*     */       
/* 473 */       if (partial.containsKey(parameterizedType)) {
/* 474 */         ReifiedParameterizedType res = partial.get(genericType);
/* 475 */         res.addReifiedTypeArgument(res);
/* 476 */         return res;
/*     */       } 
/* 478 */       Type[] genericTypeArguments = parameterizedType.getActualTypeArguments();
/* 479 */       ReifiedParameterizedType result = new ReifiedParameterizedType(parameterizedType);
/* 480 */       partial.put(parameterizedType, result);
/* 481 */       for (Type genericTypeArgument : genericTypeArguments) {
/* 482 */         Type reified = reify(genericTypeArgument, typeVariableMap, partial);
/*     */ 
/*     */         
/* 485 */         if (reified != result) {
/* 486 */           result.addReifiedTypeArgument(reified);
/*     */         }
/*     */       } 
/* 489 */       return result;
/* 490 */     }  if (genericType instanceof GenericArrayType) {
/* 491 */       GenericArrayType genericArrayType = (GenericArrayType)genericType;
/* 492 */       Type genericComponentType = genericArrayType.getGenericComponentType();
/* 493 */       Type reifiedComponentType = reify(genericArrayType.getGenericComponentType(), typeVariableMap, partial);
/*     */       
/* 495 */       if (genericComponentType == reifiedComponentType) {
/* 496 */         return genericComponentType;
/*     */       }
/* 498 */       if (reifiedComponentType instanceof Class) {
/* 499 */         return Array.newInstance((Class)reifiedComponentType, 0).getClass();
/*     */       }
/* 501 */       throw new UnsupportedOperationException("Attempted to reify generic array type, whose generic component type could not be reified to some Class<?>. Handling for this case is not implemented");
/*     */     } 
/*     */     
/* 504 */     if (genericType instanceof TypeVariable) {
/* 505 */       TypeVariable<?> typeVariable = (TypeVariable)genericType;
/* 506 */       Type mapping = typeVariableMap.get(typeVariable);
/* 507 */       if (mapping != null) {
/* 508 */         return reify(mapping, typeVariableMap, partial);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 514 */       return reify(typeVariable.getBounds()[0], typeVariableMap, partial);
/* 515 */     }  if (genericType instanceof WildcardType) {
/* 516 */       WildcardType wildcardType = (WildcardType)genericType;
/* 517 */       Type[] upperBounds = wildcardType.getUpperBounds();
/* 518 */       Type[] lowerBounds = wildcardType.getLowerBounds();
/* 519 */       if (upperBounds.length == 1 && lowerBounds.length == 0) {
/* 520 */         return reify(upperBounds[0], typeVariableMap, partial);
/*     */       }
/* 522 */       throw new UnsupportedOperationException("Attempted to reify wildcard type with name '" + wildcardType
/* 523 */           .getTypeName() + "' which has " + upperBounds.length + " upper bounds and " + lowerBounds.length + " lower bounds. Reification of wildcard types is only supported for the trivial case of exactly 1 upper bound and 0 lower bounds.");
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 528 */     throw new UnsupportedOperationException("Reification of type with name '" + genericType
/* 529 */         .getTypeName() + "' and class name '" + genericType
/* 530 */         .getClass().getName() + "' is not implemented.");
/*     */   }
/*     */ 
/*     */   
/*     */   private static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> targetType, Class<?> functionalInterface) {
/* 535 */     Reference<Map<TypeVariable<?>, Type>> ref = TYPE_VARIABLE_CACHE.get(targetType);
/* 536 */     Map<TypeVariable<?>, Type> map = (ref != null) ? ref.get() : null;
/*     */     
/* 538 */     if (map == null) {
/* 539 */       map = new HashMap<>();
/*     */ 
/*     */       
/* 542 */       if (functionalInterface != null) {
/* 543 */         populateLambdaArgs(functionalInterface, targetType, map);
/*     */       }
/*     */       
/* 546 */       populateSuperTypeArgs(targetType.getGenericInterfaces(), map, (functionalInterface != null));
/*     */ 
/*     */       
/* 549 */       Type genericType = targetType.getGenericSuperclass();
/* 550 */       Class<?> type = targetType.getSuperclass();
/* 551 */       while (type != null && !Object.class.equals(type)) {
/* 552 */         if (genericType instanceof ParameterizedType)
/* 553 */           populateTypeArgs((ParameterizedType)genericType, map, false); 
/* 554 */         populateSuperTypeArgs(type.getGenericInterfaces(), map, false);
/*     */         
/* 556 */         genericType = type.getGenericSuperclass();
/* 557 */         type = type.getSuperclass();
/*     */       } 
/*     */ 
/*     */       
/* 561 */       type = targetType;
/* 562 */       while (type.isMemberClass()) {
/* 563 */         genericType = type.getGenericSuperclass();
/* 564 */         if (genericType instanceof ParameterizedType) {
/* 565 */           populateTypeArgs((ParameterizedType)genericType, map, (functionalInterface != null));
/*     */         }
/* 567 */         type = type.getEnclosingClass();
/*     */       } 
/*     */       
/* 570 */       if (CACHE_ENABLED) {
/* 571 */         TYPE_VARIABLE_CACHE.put(targetType, new WeakReference<>(map));
/*     */       }
/*     */     } 
/* 574 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void populateSuperTypeArgs(Type[] types, Map<TypeVariable<?>, Type> map, boolean depthFirst) {
/* 582 */     for (Type type : types) {
/* 583 */       if (type instanceof ParameterizedType) {
/* 584 */         ParameterizedType parameterizedType = (ParameterizedType)type;
/* 585 */         if (!depthFirst)
/* 586 */           populateTypeArgs(parameterizedType, map, depthFirst); 
/* 587 */         Type rawType = parameterizedType.getRawType();
/* 588 */         if (rawType instanceof Class)
/* 589 */           populateSuperTypeArgs(((Class)rawType).getGenericInterfaces(), map, depthFirst); 
/* 590 */         if (depthFirst)
/* 591 */           populateTypeArgs(parameterizedType, map, depthFirst); 
/* 592 */       } else if (type instanceof Class) {
/* 593 */         populateSuperTypeArgs(((Class)type).getGenericInterfaces(), map, depthFirst);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void populateTypeArgs(ParameterizedType type, Map<TypeVariable<?>, Type> map, boolean depthFirst) {
/* 602 */     if (type.getRawType() instanceof Class) {
/* 603 */       TypeVariable[] arrayOfTypeVariable = ((Class)type.getRawType()).getTypeParameters();
/* 604 */       Type[] typeArguments = type.getActualTypeArguments();
/*     */       
/* 606 */       if (type.getOwnerType() != null) {
/* 607 */         Type owner = type.getOwnerType();
/* 608 */         if (owner instanceof ParameterizedType) {
/* 609 */           populateTypeArgs((ParameterizedType)owner, map, depthFirst);
/*     */         }
/*     */       } 
/* 612 */       for (int i = 0; i < typeArguments.length; i++) {
/* 613 */         TypeVariable<?> variable = arrayOfTypeVariable[i];
/* 614 */         Type typeArgument = typeArguments[i];
/*     */         
/* 616 */         if (typeArgument instanceof Class) {
/* 617 */           map.put(variable, typeArgument); continue;
/* 618 */         }  if (typeArgument instanceof GenericArrayType) {
/* 619 */           map.put(variable, typeArgument); continue;
/* 620 */         }  if (typeArgument instanceof ParameterizedType) {
/* 621 */           map.put(variable, typeArgument); continue;
/* 622 */         }  if (typeArgument instanceof TypeVariable) {
/* 623 */           TypeVariable<?> typeVariableArgument = (TypeVariable)typeArgument;
/* 624 */           if (depthFirst) {
/* 625 */             Type existingType = map.get(variable);
/* 626 */             if (existingType != null) {
/* 627 */               map.put(typeVariableArgument, existingType);
/*     */               
/*     */               continue;
/*     */             } 
/*     */           } 
/* 632 */           Type resolvedType = map.get(typeVariableArgument);
/* 633 */           if (resolvedType == null)
/* 634 */             resolvedType = resolveBound(typeVariableArgument); 
/* 635 */           map.put(variable, resolvedType);
/*     */         } 
/*     */         continue;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type resolveBound(TypeVariable<?> typeVariable) {
/* 645 */     Type[] bounds = typeVariable.getBounds();
/* 646 */     if (bounds.length == 0) {
/* 647 */       return Unknown.class;
/*     */     }
/* 649 */     Type bound = bounds[0];
/* 650 */     if (bound instanceof TypeVariable) {
/* 651 */       bound = resolveBound((TypeVariable)bound);
/*     */     }
/* 653 */     return (bound == Object.class) ? Unknown.class : bound;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void populateLambdaArgs(Class<?> functionalInterface, Class<?> lambdaType, Map<TypeVariable<?>, Type> map) {
/* 661 */     if (RESOLVES_LAMBDAS)
/*     */     {
/* 663 */       for (Method m : functionalInterface.getMethods()) {
/* 664 */         if (!isDefaultMethod(m) && !Modifier.isStatic(m.getModifiers()) && !m.isBridge()) {
/*     */           
/* 666 */           Method objectMethod = OBJECT_METHODS.get(m.getName());
/* 667 */           if (objectMethod == null || !Arrays.equals((Object[])m.getTypeParameters(), (Object[])objectMethod.getTypeParameters())) {
/*     */ 
/*     */ 
/*     */             
/* 671 */             Type returnTypeVar = m.getGenericReturnType();
/* 672 */             Type[] paramTypeVars = m.getGenericParameterTypes();
/*     */             
/* 674 */             Member member = getMemberRef(lambdaType);
/* 675 */             if (member == null) {
/*     */               return;
/*     */             }
/*     */             
/* 679 */             if (returnTypeVar instanceof TypeVariable) {
/*     */               
/* 681 */               Class<?> returnType = (member instanceof Method) ? ((Method)member).getReturnType() : ((Constructor)member).getDeclaringClass();
/* 682 */               returnType = wrapPrimitives(returnType);
/* 683 */               if (!returnType.equals(Void.class)) {
/* 684 */                 map.put((TypeVariable)returnTypeVar, returnType);
/*     */               }
/*     */             } 
/*     */             
/* 688 */             Class<?>[] arguments = (member instanceof Method) ? ((Method)member).getParameterTypes() : ((Constructor)member).getParameterTypes();
/*     */ 
/*     */             
/* 691 */             int paramOffset = 0;
/* 692 */             if (paramTypeVars.length > 0 && paramTypeVars[0] instanceof TypeVariable && paramTypeVars.length == arguments.length + 1) {
/*     */               
/* 694 */               Class<?> instanceType = member.getDeclaringClass();
/* 695 */               map.put((TypeVariable)paramTypeVars[0], instanceType);
/* 696 */               paramOffset = 1;
/*     */             } 
/*     */ 
/*     */             
/* 700 */             int argOffset = 0;
/* 701 */             if (paramTypeVars.length < arguments.length) {
/* 702 */               argOffset = arguments.length - paramTypeVars.length;
/*     */             }
/*     */ 
/*     */             
/* 706 */             for (int i = 0; i + argOffset < arguments.length; i++) {
/* 707 */               if (paramTypeVars[i] instanceof TypeVariable)
/* 708 */                 map.put((TypeVariable)paramTypeVars[i + paramOffset], wrapPrimitives(arguments[i + argOffset])); 
/*     */             } 
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isDefaultMethod(Method m) {
/* 718 */     return (JAVA_VERSION.doubleValue() >= 1.8D && m.isDefault());
/*     */   }
/*     */   
/*     */   private static Member getMemberRef(Class<?> type) {
/*     */     Object constantPool;
/*     */     try {
/* 724 */       constantPool = GET_CONSTANT_POOL.invoke(JAVA_LANG_ACCESS, new Object[] { type });
/* 725 */     } catch (Exception ignore) {
/* 726 */       return null;
/*     */     } 
/*     */     
/* 729 */     Member result = null;
/* 730 */     for (int i = getConstantPoolSize(constantPool) - 1; i >= 0; i--) {
/* 731 */       Member member = getConstantPoolMethodAt(constantPool, i);
/*     */       
/* 733 */       if (member != null && (!(member instanceof Constructor) || 
/*     */         
/* 735 */         !member.getDeclaringClass().getName().equals("java.lang.invoke.SerializedLambda")) && 
/* 736 */         !member.getDeclaringClass().isAssignableFrom(type)) {
/*     */ 
/*     */         
/* 739 */         result = member;
/*     */ 
/*     */         
/* 742 */         if (!(member instanceof Method) || !isAutoBoxingMethod((Method)member))
/*     */           break; 
/*     */       } 
/*     */     } 
/* 746 */     return result;
/*     */   }
/*     */   
/*     */   private static boolean isAutoBoxingMethod(Method method) {
/* 750 */     Class<?>[] parameters = method.getParameterTypes();
/* 751 */     return (method.getName().equals("valueOf") && parameters.length == 1 && parameters[0].isPrimitive() && 
/* 752 */       wrapPrimitives(parameters[0]).equals(method.getDeclaringClass()));
/*     */   }
/*     */   
/*     */   private static Class<?> wrapPrimitives(Class<?> clazz) {
/* 756 */     return clazz.isPrimitive() ? PRIMITIVE_WRAPPERS.get(clazz) : clazz;
/*     */   }
/*     */   
/*     */   private static int getConstantPoolSize(Object constantPool) {
/*     */     try {
/* 761 */       return ((Integer)GET_CONSTANT_POOL_SIZE.invoke(constantPool, new Object[0])).intValue();
/* 762 */     } catch (Exception ignore) {
/* 763 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Member getConstantPoolMethodAt(Object constantPool, int i) {
/*     */     try {
/* 769 */       return (Member)GET_CONSTANT_POOL_METHOD_AT.invoke(constantPool, new Object[] { Integer.valueOf(i) });
/* 770 */     } catch (Exception ignore) {
/* 771 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\net\jodah\typetools\TypeResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */