package me.zero.alpine.listener;

@FunctionalInterface
public interface EventHook<T> {
  void invoke(T paramT);
}


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\listener\EventHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */