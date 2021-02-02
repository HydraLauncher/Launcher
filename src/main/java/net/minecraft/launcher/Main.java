package net.minecraft.launcher;

import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.net.*;
import joptsimple.*;
import java.util.*;
import java.io.*;
import java.util.List;

import com.mojang.launcher.*;
import org.apache.logging.log4j.*;

public class Main
{
    private static final Logger LOGGER;
    
    public static void main(final String[] args) {
        Main.LOGGER.debug("main() called!");
        startLauncher(args);
    }
    
    private static void startLauncher(final String[] args) {
        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parser.accepts("winTen");
        final OptionSpec<String> proxyHostOption = parser.accepts("proxyHost").withRequiredArg();
        final OptionSpec<Integer> proxyPortOption = parser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        final OptionSpec<File> workDirOption = parser.accepts("workDir").withRequiredArg().ofType(File.class).defaultsTo(getWorkingDirectory());
        final OptionSpec<String> nonOption = parser.nonOptions();
        final OptionSet optionSet = parser.parse(args);
        final List<String> leftoverArgs = optionSet.valuesOf(nonOption);
        final String hostName = optionSet.valueOf(proxyHostOption);
        Proxy proxy = Proxy.NO_PROXY;
        if (hostName != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostName, optionSet.valueOf(proxyPortOption)));
            }
            catch (Exception ex) {}
        }
        final File workingDirectory = optionSet.valueOf(workDirOption);
        workingDirectory.mkdirs();
        Main.LOGGER.debug("About to create JFrame.");
        final Proxy finalProxy = proxy;
        final JFrame frame = new JFrame();
        frame.setTitle("Hydra Launcher " + LauncherConstants.getVersionName() + LauncherConstants.PROPERTIES.getEnvironment().getTitle());
        frame.setPreferredSize(new Dimension(900, 580));
        try {
            final InputStream in = Launcher.class.getResourceAsStream("/favicon.png");
            if (in != null) {
                frame.setIconImage(ImageIO.read(in));
            }
        }
        catch (IOException ex2) {}
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        if (optionSet.has("winTen")) {
            System.setProperty("os.name", "Windows 10");
            System.setProperty("os.version", "10.0");
        }
        Main.LOGGER.debug("Starting up launcher.");
        final Launcher launcher = new Launcher(frame, workingDirectory, finalProxy, null, leftoverArgs.toArray(new String[leftoverArgs.size()]), 100);
        if (optionSet.has("winTen")) {
            launcher.setWinTenHack();
        }
        frame.setLocationRelativeTo(null);
        Main.LOGGER.debug("End of main.");
    }
    
    public static File getWorkingDirectory() {
        final String userHome = System.getProperty("user.home", ".");
        File workingDirectory = null;
        switch (OperatingSystem.getCurrentPlatform()) {
            case LINUX: {
                workingDirectory = new File(userHome, ".hydra/");
                break;
            }
            case WINDOWS: {
                final String applicationData = System.getenv("APPDATA");
                final String folder = (applicationData != null) ? applicationData : userHome;
                workingDirectory = new File(folder, ".hydra/");
                break;
            }
            case OSX: {
                workingDirectory = new File(userHome, "Library/Application Support/hydra");
                break;
            }
            default: {
                workingDirectory = new File(userHome, "hydra/");
                break;
            }
        }
        return workingDirectory;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
