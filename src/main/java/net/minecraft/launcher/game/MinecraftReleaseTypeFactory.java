package net.minecraft.launcher.game;

import java.util.*;
import com.google.common.collect.*;
import com.mojang.launcher.versions.*;

public class MinecraftReleaseTypeFactory implements ReleaseTypeFactory<MinecraftReleaseType>
{
    private static final MinecraftReleaseTypeFactory FACTORY;
    
    private MinecraftReleaseTypeFactory() {
    }
    
    @Override
    public MinecraftReleaseType getTypeByName(final String name) {
        return MinecraftReleaseType.getByName(name);
    }
    
    @Override
    public MinecraftReleaseType[] getAllTypes() {
        return MinecraftReleaseType.values();
    }
    
    @Override
    public Class<MinecraftReleaseType> getTypeClass() {
        return MinecraftReleaseType.class;
    }
    
    @Override
    public Iterator<MinecraftReleaseType> iterator() {
        return Iterators.forArray(MinecraftReleaseType.values());
    }
    
    public static MinecraftReleaseTypeFactory instance() {
        return MinecraftReleaseTypeFactory.FACTORY;
    }
    
    static {
        FACTORY = new MinecraftReleaseTypeFactory();
    }
}
