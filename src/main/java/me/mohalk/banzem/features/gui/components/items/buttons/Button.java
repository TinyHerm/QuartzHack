/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.SoundEvent
 */
package me.mohalk.banzem.features.gui.components.items.buttons;

import me.mohalk.banzem.Banzem;
import me.mohalk.banzem.features.gui.PhobosGui;
import me.mohalk.banzem.features.gui.components.Component;
import me.mohalk.banzem.features.gui.components.items.Item;
import me.mohalk.banzem.features.modules.client.ClickGui;
import me.mohalk.banzem.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class Button
extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width, this.y + (float)this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Banzem.colorManager.getColorWithAlpha(Banzem.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : Banzem.colorManager.getColorWithAlpha(Banzem.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        Banzem.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float)PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        Banzem.textManager.drawStringWithShadow("+", this.x + (float)this.width - (float)Banzem.textManager.getStringWidth("+"), this.y - 2.0f - (float)PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a((SoundEvent)SoundEvents.field_187909_gi, (float)1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : PhobosGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY <= this.getY() + (float)this.height;
    }
}

