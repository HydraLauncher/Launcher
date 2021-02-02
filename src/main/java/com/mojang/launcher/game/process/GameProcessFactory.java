package com.mojang.launcher.game.process;

import java.io.*;

public interface GameProcessFactory
{
    GameProcess startGame(final GameProcessBuilder p0) throws IOException;
}
