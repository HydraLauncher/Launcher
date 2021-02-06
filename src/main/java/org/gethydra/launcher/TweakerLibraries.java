package org.gethydra.launcher;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.launcher.updater.Library;

import java.io.InputStreamReader;

public class TweakerLibraries
{
    private static Gson gson = new Gson();

    public static final Library byteBuddy = gson.fromJson(new JsonReader(new InputStreamReader(TweakerLibraries.class.getResourceAsStream("/bytebuddy.json"))), Library.class);
    public static final Library byteBuddyAgent = gson.fromJson(new JsonReader(new InputStreamReader(TweakerLibraries.class.getResourceAsStream("/bytebuddyagent.json"))), Library.class);
    public static final Library hydraTweaker = gson.fromJson(new JsonReader(new InputStreamReader(TweakerLibraries.class.getResourceAsStream("/hydratweaker.json"))), Library.class);
}
