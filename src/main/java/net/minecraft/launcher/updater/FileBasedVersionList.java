package net.minecraft.launcher.updater;

import org.apache.commons.io.*;
import java.io.*;
import com.mojang.launcher.versions.*;
import net.minecraft.launcher.game.*;

public abstract class FileBasedVersionList extends VersionList
{
    public String getContent(final String path) throws IOException {
        return IOUtils.toString(this.getFileInputStream(path)).replaceAll("\\r\\n", "\r").replaceAll("\\r", "\n");
    }
    
    protected abstract InputStream getFileInputStream(final String p0) throws FileNotFoundException;
    
    @Override
    public CompleteMinecraftVersion getCompleteVersion(final Version version) throws IOException {
        if (version instanceof CompleteVersion) {
            return (CompleteMinecraftVersion)version;
        }
        if (!(version instanceof PartialVersion)) {
            throw new IllegalArgumentException("Version must be a partial");
        }
        final PartialVersion partial = (PartialVersion)version;
        final CompleteMinecraftVersion complete = this.gson.fromJson(this.getContent("versions/" + version.getId() + "/" + version.getId() + ".json"), CompleteMinecraftVersion.class);
        final MinecraftReleaseType type = (MinecraftReleaseType)version.getType();
        this.replacePartialWithFull(partial, complete);
        return complete;
    }
}
