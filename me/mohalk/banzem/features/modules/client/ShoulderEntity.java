/*    */ package me.mohalk.banzem.features.modules.client;
/*    */ import me.mohalk.banzem.features.modules.Module;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.passive.EntityOcelot;
/*    */ import net.minecraft.nbt.NBTBase;
/*    */ import net.minecraft.nbt.NBTTagCompound;
/*    */ import net.minecraft.nbt.NBTTagInt;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraftforge.client.event.RenderPlayerEvent;
/*    */ import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*    */ 
/*    */ public class ShoulderEntity extends Module {
/* 13 */   private static final ResourceLocation BLACK_OCELOT_TEXTURES = new ResourceLocation("eralp232/transparent.png");
/*    */   
/*    */   public ShoulderEntity() {
/* 16 */     super("ShoulderEntity", "Test", Module.Category.CLIENT, true, false, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 21 */     mc.field_71441_e.func_73027_a(-101, (Entity)new EntityOcelot((World)mc.field_71441_e));
/* 22 */     NBTTagCompound tag = new NBTTagCompound();
/* 23 */     tag.func_74782_a("id", (NBTBase)new NBTTagInt(-101));
/* 24 */     mc.field_71439_g.func_192027_g(tag);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 29 */     mc.field_71441_e.func_73028_b(-101);
/*    */   }
/*    */ 
/*    */   
/*    */   @SubscribeEvent
/*    */   public void onRenderPlayer(RenderPlayerEvent.Post event) {}
/*    */   
/*    */   public float interpolate(float yaw1, float yaw2, float percent) {
/* 37 */     float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0F;
/* 38 */     if (rotation < 0.0F) {
/* 39 */       rotation += 360.0F;
/*    */     }
/* 41 */     return rotation;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\features\modules\client\ShoulderEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */