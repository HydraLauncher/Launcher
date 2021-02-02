package net.minecraft.launcher.ui.bottombar;

import com.mojang.launcher.events.*;
import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.game.*;
import net.minecraft.launcher.*;
import com.mojang.launcher.*;
import java.awt.event.*;
import java.awt.*;
import net.minecraft.launcher.profile.*;
import com.mojang.launcher.game.*;
import com.mojang.launcher.updater.*;
import javax.swing.*;

public class PlayButtonPanel extends JPanel implements RefreshedVersionsListener, RefreshedProfilesListener, UserChangedListener
{
    private final Launcher minecraftLauncher;
    private final JButton playButton;
    private final JLabel demoHelpLink;
    
    public PlayButtonPanel(final Launcher minecraftLauncher) {
        this.playButton = new JButton("Play");
        this.demoHelpLink = new JLabel("(Why can I only play demo?)");
        this.minecraftLauncher = minecraftLauncher;
        minecraftLauncher.getProfileManager().addRefreshedProfilesListener(this);
        minecraftLauncher.getProfileManager().addUserChangedListener(this);
        this.checkState();
        this.createInterface();
        this.playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final GameLaunchDispatcher dispatcher = PlayButtonPanel.this.getMinecraftLauncher().getLaunchDispatcher();
                if (dispatcher.isRunningInSameFolder()) {
                    final int result = JOptionPane.showConfirmDialog(((SwingUserInterface)PlayButtonPanel.this.getMinecraftLauncher().getUserInterface()).getFrame(), "You already have an instance of Minecraft running. If you launch another one in the same folder, they may clash and corrupt your saves.\nThis could cause many issues, in singleplayer or otherwise. We will not be responsible for anything that goes wrong.\nDo you want to start another instance of Minecraft, despite this?\nYou may solve this issue by launching the game in a different folder (see the \"Edit Profile\" button)", "Duplicate instance warning", 0);
                    if (result == 0) {
                        dispatcher.play();
                    }
                }
                else {
                    dispatcher.play();
                }
            }
        });
    }
    
    protected void createInterface() {
        this.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridy = 0;
        constraints.gridx = 0;
        this.add(this.playButton, constraints);
        final GridBagConstraints gridBagConstraints = constraints;
        ++gridBagConstraints.gridy;
        constraints.weighty = 0.0;
        constraints.anchor = 10;
        final Font smalltextFont = this.demoHelpLink.getFont().deriveFont(this.demoHelpLink.getFont().getSize() - 2.0f);
        this.demoHelpLink.setCursor(new Cursor(12));
        this.demoHelpLink.setFont(smalltextFont);
        this.demoHelpLink.setHorizontalAlignment(0);
        this.demoHelpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                OperatingSystem.openLink(LauncherConstants.URL_DEMO_HELP);
            }
        });
        this.add(this.demoHelpLink, constraints);
        this.playButton.setFont(this.playButton.getFont().deriveFont(1, (float)(this.playButton.getFont().getSize() + 2)));
    }
    
    @Override
    public void onProfilesRefreshed(final ProfileManager manager) {
        this.checkState();
    }
    
    public void checkState() {
        final GameLaunchDispatcher.PlayStatus status = this.minecraftLauncher.getLaunchDispatcher().getStatus();
        this.playButton.setText(status.getName());
        this.playButton.setEnabled(status.canPlay());
        this.demoHelpLink.setVisible(status == GameLaunchDispatcher.PlayStatus.CAN_PLAY_DEMO);
        if (status == GameLaunchDispatcher.PlayStatus.DOWNLOADING) {
            final GameInstanceStatus instanceStatus = this.minecraftLauncher.getLaunchDispatcher().getInstanceStatus();
            if (instanceStatus != GameInstanceStatus.IDLE) {
                this.playButton.setText(instanceStatus.getName());
            }
        }
    }
    
    @Override
    public void onVersionsRefreshed(final VersionManager manager) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PlayButtonPanel.this.checkState();
            }
        });
    }
    
    public Launcher getMinecraftLauncher() {
        return this.minecraftLauncher;
    }
    
    @Override
    public void onUserChanged(final ProfileManager manager) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PlayButtonPanel.this.checkState();
            }
        });
    }
}
