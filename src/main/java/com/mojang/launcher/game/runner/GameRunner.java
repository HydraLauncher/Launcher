package com.mojang.launcher.game.runner;

import com.mojang.launcher.game.*;
import com.mojang.launcher.updater.*;
import com.mojang.launcher.updater.download.*;

public interface GameRunner
{
    GameInstanceStatus getStatus();
    
    void playGame(final VersionSyncInfo p0);
    
    boolean hasRemainingJobs();
    
    void addJob(final DownloadJob p0);
}
