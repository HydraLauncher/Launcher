package net.minecraft.launcher.updater;

import java.net.*;
import com.mojang.launcher.versions.*;
import java.io.*;
import net.minecraft.launcher.game.*;
import com.mojang.launcher.*;
import java.util.*;
import com.google.common.collect.*;

public class RemoteVersionList extends VersionList
{
    private final URL manifestUrl;
    private final Proxy proxy;
    
    public RemoteVersionList(final URL manifestUrl, final Proxy proxy) {
        this.manifestUrl = manifestUrl;
        this.proxy = proxy;
    }
    
    @Override
    public CompleteMinecraftVersion getCompleteVersion(final Version version) throws IOException {
        if (version instanceof CompleteVersion) {
            return (CompleteMinecraftVersion)version;
        }
        if (!(version instanceof PartialVersion)) {
            throw new IllegalArgumentException("Version must be a partial");
        }
        final PartialVersion partial = (PartialVersion)version;
        final CompleteMinecraftVersion complete = this.gson.fromJson(Http.performGet(partial.getUrl(), this.proxy), CompleteMinecraftVersion.class);
        this.replacePartialWithFull(partial, complete);
        return complete;
    }
    
    @Override
    public void refreshVersions() throws IOException {
        this.clearCache();
        final RawVersionList versionList = this.gson.fromJson(this.getContent(this.manifestUrl), RawVersionList.class);
        for (final Version version : versionList.getVersions()) {
            this.versions.add(version);
            this.versionsByName.put(version.getId(), version);
        }
        for (final MinecraftReleaseType type : MinecraftReleaseType.values()) {
            this.latestVersions.put(type, this.versionsByName.get(versionList.getLatestVersions().get(type)));
        }
    }
    
    @Override
    public boolean hasAllFiles(final CompleteMinecraftVersion version, final OperatingSystem os) {
        return true;
    }
    
    public String getContent(final URL url) throws IOException {
        return Http.performGet(url, this.proxy);
    }
    
    public Proxy getProxy() {
        return this.proxy;
    }
    
    private static class RawVersionList
    {
        private final List<PartialVersion> versions;
        private final Map<MinecraftReleaseType, String> latest;
        
        private RawVersionList() {
            this.versions = new ArrayList<PartialVersion>();
            this.latest = Maps.newEnumMap(MinecraftReleaseType.class);
        }
        
        public List<PartialVersion> getVersions() {
            return this.versions;
        }
        
        public Map<MinecraftReleaseType, String> getLatestVersions() {
            return this.latest;
        }
    }
}
