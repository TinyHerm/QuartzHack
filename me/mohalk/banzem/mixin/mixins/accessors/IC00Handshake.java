package me.mohalk.banzem.mixin.mixins.accessors;

import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({C00Handshake.class})
public interface IC00Handshake {
  @Accessor("ip")
  String getIp();
  
  @Accessor("ip")
  void setIp(String paramString);
}


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\mohalk\banzem\mixin\mixins\accessors\IC00Handshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */