package me.vaperion.plugins.utils.config.configs;

import io.github.thatkawaiisam.assemble.AssembleStyle;
import me.vaperion.plugins.Hub;
import me.vaperion.plugins.utils.Configuration;
import me.vaperion.plugins.utils.config.ConfPath;
import me.vaperion.plugins.utils.config.LoadAfterConfLoad;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard {
    @ConfPath(path = "provider.refresh-rate", confName = "config", fallback = "2")
    public long scoreboardRefreshRate = 2L;
    @ConfPath(path = "provider.type", confName = "config")
    public String scoreboardStyleString = "MODERN";

    @ConfPath(path = "provider.title", confName = "config")
    public String scoreboardTitle = "";
    @ConfPath(path = "provider.footer-enabled", confName = "config", fallback = "false")
    public boolean scoreboardFooterEnabled = false;
    @ConfPath(path = "provider.lines.normal", confName = "config")
    public List<String> scoreboardLines = new ArrayList<>();
    @ConfPath(path = "provider.lines.queue", confName = "config")
    public List<String> scoreboardLinesInQueue = new ArrayList<>();
    @ConfPath(path = "provider.lines.footer", confName = "config")
    public List<String> scoreboardLinesFooter = new ArrayList<>();

    @LoadAfterConfLoad
    public void setVariables() {
        Hub.getInstance().getAssemble().setAssembleStyle(AssembleStyle.valueOf(scoreboardStyleString.toUpperCase()));
        Hub.getInstance().getAssemble().setTicks(scoreboardRefreshRate);
        Configuration.scoreboard = this;
    }
}