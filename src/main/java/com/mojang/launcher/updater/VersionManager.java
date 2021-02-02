package com.mojang.launcher.updater;

import java.io.*;
import java.util.*;
import com.mojang.launcher.versions.*;
import com.mojang.launcher.updater.download.*;
import java.util.concurrent.*;
import com.mojang.launcher.events.*;

public interface VersionManager
{
    void refreshVersions() throws IOException;
    
    List<VersionSyncInfo> getVersions();
    
    List<VersionSyncInfo> getVersions(final VersionFilter<? extends ReleaseType> p0);
    
    VersionSyncInfo getVersionSyncInfo(final Version p0);
    
    VersionSyncInfo getVersionSyncInfo(final String p0);
    
    VersionSyncInfo getVersionSyncInfo(final Version p0, final Version p1);
    
    List<VersionSyncInfo> getInstalledVersions();
    
    CompleteVersion getLatestCompleteVersion(final VersionSyncInfo p0) throws IOException;
    
    DownloadJob downloadVersion(final VersionSyncInfo p0, final DownloadJob p1) throws IOException;
    
    DownloadJob downloadResources(final DownloadJob p0, final CompleteVersion p1) throws IOException;
    
    ThreadPoolExecutor getExecutorService();
    
    void addRefreshedVersionsListener(final RefreshedVersionsListener p0);
    
    void removeRefreshedVersionsListener(final RefreshedVersionsListener p0);
    
    VersionSyncInfo syncVersion(final VersionSyncInfo p0) throws IOException;
    
    void installVersion(final CompleteVersion p0) throws IOException;
    
    void uninstallVersion(final CompleteVersion p0) throws IOException;
}
