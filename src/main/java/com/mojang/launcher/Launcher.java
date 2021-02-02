package com.mojang.launcher;

import java.io.*;
import java.net.*;
import com.mojang.authlib.*;
import com.mojang.launcher.versions.*;
import java.util.concurrent.*;
import com.mojang.launcher.updater.*;
import org.apache.logging.log4j.*;

public class Launcher
{
    private static final Logger LOGGER;
    private final VersionManager versionManager;
    private final File workingDirectory;
    private final UserInterface ui;
    private final Proxy proxy;
    private final PasswordAuthentication proxyAuth;
    private final ThreadPoolExecutor downloaderExecutorService;
    private final Agent agent;
    private final ReleaseTypeFactory releaseTypeFactory;
    private final int launcherFormatVersion;
    
    public Launcher(final UserInterface ui, final File workingDirectory, final Proxy proxy, final PasswordAuthentication proxyAuth, final VersionManager versionManager, final Agent agent, final ReleaseTypeFactory releaseTypeFactory, final int launcherFormatVersion) {
        this.downloaderExecutorService = new ExceptionalThreadPoolExecutor(16, 16, 30L, TimeUnit.SECONDS);
        this.ui = ui;
        this.proxy = proxy;
        this.proxyAuth = proxyAuth;
        this.workingDirectory = workingDirectory;
        this.agent = agent;
        this.versionManager = versionManager;
        this.releaseTypeFactory = releaseTypeFactory;
        this.launcherFormatVersion = launcherFormatVersion;
        this.downloaderExecutorService.allowCoreThreadTimeOut(true);
    }
    
    public ReleaseTypeFactory getReleaseTypeFactory() {
        return this.releaseTypeFactory;
    }
    
    public VersionManager getVersionManager() {
        return this.versionManager;
    }
    
    public File getWorkingDirectory() {
        return this.workingDirectory;
    }
    
    public UserInterface getUserInterface() {
        return this.ui;
    }
    
    public Proxy getProxy() {
        return this.proxy;
    }
    
    public PasswordAuthentication getProxyAuth() {
        return this.proxyAuth;
    }
    
    public ThreadPoolExecutor getDownloaderExecutorService() {
        return this.downloaderExecutorService;
    }
    
    public void shutdownLauncher() {
        this.getUserInterface().shutdownLauncher();
    }
    
    public Agent getAgent() {
        return this.agent;
    }
    
    public int getLauncherFormatVersion() {
        return this.launcherFormatVersion;
    }
    
    static {
        Thread.currentThread().setContextClassLoader(Launcher.class.getClassLoader());
        LOGGER = LogManager.getLogger();
    }
}
