package org.spongepowered.asm.lib;

class Edge {
  static final int NORMAL = 0;
  
  static final int EXCEPTION = 2147483647;
  
  int info;
  
  Label successor;
  
  Edge next;
}


/* Location:              C:\Users\Administrator\Downloads\QuartzHackRewrite-0.3.0-releasee.jar!\org\spongepowered\asm\lib\Edge.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */