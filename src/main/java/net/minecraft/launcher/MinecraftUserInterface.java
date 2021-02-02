package net.minecraft.launcher;

import com.mojang.launcher.*;
import net.minecraft.launcher.game.*;
import com.mojang.launcher.events.*;

public interface MinecraftUserInterface extends UserInterface
{
    void showOutdatedNotice();
    
    String getTitle();
    
    GameOutputLogProcessor showGameOutputTab(final MinecraftGameRunner p0);
    
    boolean shouldDowngradeProfiles();
}
