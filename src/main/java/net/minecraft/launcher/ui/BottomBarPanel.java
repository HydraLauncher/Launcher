package net.minecraft.launcher.ui;

import javax.swing.*;
import net.minecraft.launcher.*;
import net.minecraft.launcher.ui.bottombar.*;
import javax.swing.border.*;
import java.awt.*;

public class BottomBarPanel extends JPanel
{
    private final Launcher minecraftLauncher;
    private final ProfileSelectionPanel profileSelectionPanel;
    private final PlayerInfoPanel playerInfoPanel;
    private final PlayButtonPanel playButtonPanel;
    
    public BottomBarPanel(final Launcher minecraftLauncher) {
        this.minecraftLauncher = minecraftLauncher;
        final int border = 4;
        this.setBorder(new EmptyBorder(border, border, border, border));
        this.profileSelectionPanel = new ProfileSelectionPanel(minecraftLauncher);
        this.playerInfoPanel = new PlayerInfoPanel(minecraftLauncher);
        this.playButtonPanel = new PlayButtonPanel(minecraftLauncher);
        this.createInterface();
    }
    
    protected void createInterface() {
        this.setLayout(new GridLayout(1, 3));
        this.add(this.wrapSidePanel(this.profileSelectionPanel, 17));
        this.add(this.playButtonPanel);
        this.add(this.wrapSidePanel(this.playerInfoPanel, 13));
    }
    
    protected JPanel wrapSidePanel(final JPanel target, final int side) {
        final JPanel wrapper = new JPanel(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = side;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        wrapper.add(target, constraints);
        return wrapper;
    }
    
    public Launcher getMinecraftLauncher() {
        return this.minecraftLauncher;
    }
    
    public ProfileSelectionPanel getProfileSelectionPanel() {
        return this.profileSelectionPanel;
    }
    
    public PlayerInfoPanel getPlayerInfoPanel() {
        return this.playerInfoPanel;
    }
    
    public PlayButtonPanel getPlayButtonPanel() {
        return this.playButtonPanel;
    }
}
