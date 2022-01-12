package me.zero.alpine.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EventHandler {}


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\me\zero\alpine\listener\EventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */