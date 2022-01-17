/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.world.World
 */
package me.mohalk.banzem.features.modules.player;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import me.mohalk.banzem.features.modules.Module;
import me.mohalk.banzem.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;

public class FakePlayer
extends Module {
    public List<Integer> fakePlayerIdList = new ArrayList<Integer>();
    public Setting<Boolean> moving = this.register(new Setting<Boolean>("Moving", false));
    private static FakePlayer INSTANCE = new FakePlayer();
    private EntityOtherPlayerMP otherPlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns fake player", Module.Category.PLAYER, false, false, false);
        this.setInstance();
    }

    public static FakePlayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakePlayer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (this.otherPlayer != null) {
            Random random = new Random();
            this.otherPlayer.field_191988_bg = FakePlayer.mc.field_71439_g.field_191988_bg + (float)random.nextInt(5) / 10.0f;
            this.otherPlayer.field_70702_br = FakePlayer.mc.field_71439_g.field_70702_br + (float)random.nextInt(5) / 10.0f;
            if (this.moving.getValue().booleanValue()) {
                this.travel(this.otherPlayer.field_70702_br, this.otherPlayer.field_70701_bs, this.otherPlayer.field_191988_bg);
            }
        }
    }

    public void travel(float strafe, float vertical, float forward) {
        double d0 = this.otherPlayer.field_70163_u;
        float f1 = 0.8f;
        float f2 = 0.02f;
        float f3 = EnchantmentHelper.func_185294_d((EntityLivingBase)this.otherPlayer);
        if (f3 > 3.0f) {
            f3 = 3.0f;
        }
        if (!this.otherPlayer.field_70122_E) {
            f3 *= 0.5f;
        }
        if (f3 > 0.0f) {
            f1 += (0.54600006f - f1) * f3 / 3.0f;
            f2 += (this.otherPlayer.func_70689_ay() - f2) * f3 / 4.0f;
        }
        this.otherPlayer.func_191958_b(strafe, vertical, forward, f2);
        this.otherPlayer.func_70091_d(MoverType.SELF, this.otherPlayer.field_70159_w, this.otherPlayer.field_70181_x, this.otherPlayer.field_70179_y);
        this.otherPlayer.field_70159_w *= (double)f1;
        this.otherPlayer.field_70181_x *= (double)0.8f;
        this.otherPlayer.field_70179_y *= (double)f1;
        if (!this.otherPlayer.func_189652_ae()) {
            this.otherPlayer.field_70181_x -= 0.02;
        }
        if (this.otherPlayer.field_70123_F && this.otherPlayer.func_70038_c(this.otherPlayer.field_70159_w, this.otherPlayer.field_70181_x + (double)0.6f - this.otherPlayer.field_70163_u + d0, this.otherPlayer.field_70179_y)) {
            this.otherPlayer.field_70181_x = 0.3f;
        }
    }

    @Override
    public void onEnable() {
        if (FakePlayer.mc.field_71441_e == null || FakePlayer.mc.field_71439_g == null) {
            this.toggle();
            return;
        }
        this.fakePlayerIdList = new ArrayList<Integer>();
        this.addFakePlayer(-100);
    }

    public void addFakePlayer(int entityId) {
        if (this.otherPlayer == null) {
            this.otherPlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.field_71441_e, new GameProfile(UUID.randomUUID(), "Eralp232"));
            this.otherPlayer.func_82149_j((Entity)FakePlayer.mc.field_71439_g);
            this.otherPlayer.field_71071_by.func_70455_b(FakePlayer.mc.field_71439_g.field_71071_by);
        }
        FakePlayer.mc.field_71441_e.func_73027_a(entityId, (Entity)this.otherPlayer);
        this.fakePlayerIdList.add(entityId);
    }

    @Override
    public void onDisable() {
        for (int id : this.fakePlayerIdList) {
            FakePlayer.mc.field_71441_e.func_73028_b(id);
        }
        if (this.otherPlayer != null) {
            FakePlayer.mc.field_71441_e.func_72900_e((Entity)this.otherPlayer);
            this.otherPlayer = null;
        }
    }
}

