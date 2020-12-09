package me.vaperion.plugins.utils.config.configs;

import me.vaperion.plugins.utils.Configuration;
import me.vaperion.plugins.utils.config.ConfPath;
import me.vaperion.plugins.utils.config.LoadAfterConfLoad;

import java.util.ArrayList;
import java.util.List;

public class Language {
    @ConfPath(path = "messages.no-permission", confName = "config")
    public String noPermissionMessage = "";

    @ConfPath(path = "messages.join.clearchat-amount", confName = "config")
    public int joinClearchatAmount = 100;

    @ConfPath(path = "messages.join.send-welcome", confName = "config")
    public boolean joinSendWelcome = true;

    @ConfPath(path = "messages.join.welcome", confName = "config")
    public List<String> joinWelcomeMessage = new ArrayList<>();

    @ConfPath(path = "messages.queue.full", confName = "config")
    public String queueFullMessage = "";

    @ConfPath(path = "messages.queue.success", confName = "config")
    public String queueSuccessMessage = "";

    @ConfPath(path = "messages.queue.left", confName = "config")
    public String queueLeftMessage = "";

    @ConfPath(path = "messages.queue.already-queued", confName = "config")
    public String queueAlreadyMessage = "";

    @ConfPath(path = "messages.queue.not-queued", confName = "config")
    public String queueNotQueuedMessage = "";

    @ConfPath(path = "messages.queue.not-found", confName = "config")
    public String queueNotFoundMessage = "";

    @ConfPath(path = "messages.queue.removed", confName = "config")
    public String queueRemovedMessage = "";

    @ConfPath(path = "messages.queue.lines", confName = "config")
    public List<String> queueInfoMessage = new ArrayList<>();

    @LoadAfterConfLoad
    public void setVariables() {
        Configuration.language = this;
    }
}
