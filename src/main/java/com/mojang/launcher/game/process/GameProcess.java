package com.mojang.launcher.game.process;

import java.util.*;
import com.google.common.base.*;

public interface GameProcess
{
    List<String> getStartupArguments();
    
    Collection<String> getSysOutLines();
    
    Predicate<String> getSysOutFilter();
    
    boolean isRunning();
    
    void setExitRunnable(final GameProcessRunnable p0);
    
    GameProcessRunnable getExitRunnable();
    
    int getExitCode();
    
    void stop();
}
