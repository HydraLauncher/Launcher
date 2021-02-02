package com.mojang.launcher.game.process.direct;

import com.google.common.base.Objects;
import com.mojang.launcher.game.process.*;
import java.util.*;
import com.mojang.launcher.events.*;
import com.google.common.collect.*;
import com.google.common.base.*;

public class DirectGameProcess extends AbstractGameProcess
{
    private static final int MAX_SYSOUT_LINES = 5;
    private final Process process;
    protected final DirectProcessInputMonitor monitor;
    private final Collection<String> sysOutLines;
    
    public DirectGameProcess(final List<String> commands, final Process process, final Predicate<String> sysOutFilter, final GameOutputLogProcessor logProcessor) {
        super(commands, sysOutFilter);
        this.sysOutLines = EvictingQueue.create(5);
        this.process = process;
        (this.monitor = new DirectProcessInputMonitor(this, logProcessor)).start();
    }
    
    public Process getRawProcess() {
        return this.process;
    }
    
    @Override
    public Collection<String> getSysOutLines() {
        return this.sysOutLines;
    }
    
    @Override
    public boolean isRunning() {
        try {
            this.process.exitValue();
        }
        catch (IllegalThreadStateException ex) {
            return true;
        }
        return false;
    }
    
    @Override
    public int getExitCode() {
        try {
            return this.process.exitValue();
        }
        catch (IllegalThreadStateException ex) {
            ex.fillInStackTrace();
            throw ex;
        }
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("process", this.process).add("monitor", this.monitor).toString();
    }
    
    @Override
    public void stop() {
        this.process.destroy();
    }
}
