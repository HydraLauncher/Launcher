package com.mojang.launcher.game.process.direct;

import com.mojang.launcher.events.*;
import java.io.*;
import org.apache.commons.io.*;
import com.mojang.launcher.game.process.*;
import org.apache.logging.log4j.*;

public class DirectProcessInputMonitor extends Thread
{
    private static final Logger LOGGER;
    private final DirectGameProcess process;
    private final GameOutputLogProcessor logProcessor;
    
    public DirectProcessInputMonitor(final DirectGameProcess process, final GameOutputLogProcessor logProcessor) {
        this.process = process;
        this.logProcessor = logProcessor;
    }
    
    @Override
    public void run() {
        final InputStreamReader reader = new InputStreamReader(this.process.getRawProcess().getInputStream());
        final BufferedReader buf = new BufferedReader(reader);
        String line = null;
        while (this.process.isRunning()) {
            try {
                while ((line = buf.readLine()) != null) {
                    this.logProcessor.onGameOutput(this.process, line);
                    if (this.process.getSysOutFilter().apply(line) == Boolean.TRUE) {
                        this.process.getSysOutLines().add(line);
                    }
                }
            }
            catch (IOException ex) {
                DirectProcessInputMonitor.LOGGER.error(ex);
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
        }
        final GameProcessRunnable onExit = this.process.getExitRunnable();
        if (onExit != null) {
            onExit.onGameProcessEnded(this.process);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
