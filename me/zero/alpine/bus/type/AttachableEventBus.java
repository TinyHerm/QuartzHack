package me.zero.alpine.bus.type;

import me.zero.alpine.bus.EventBus;

public interface AttachableEventBus extends EventBus {
  void attach(EventBus paramEventBus);
  
  void detach(EventBus paramEventBus);
}


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\bus\type\AttachableEventBus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */