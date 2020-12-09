package me.vaperion.plugins.commands;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;
import me.vaperion.plugins.Hub;

public class HubCommands {

    @Command(name = "reloadmessages", permission = "vhub.admin")
    public void reloadmessagesCommand(CommandArgs args) {
        Hub.getInstance().loadMessages();
        args.getSender().sendMessage("§aMessages of vHub have been reloaded.");
        args.getSender().sendMessage("§aTo reload ranks use /reloadranks.");
    }

    @Command(name = "reloadranks", permission = "vhub.admin")
    public void reloadranksCommand(CommandArgs args) {
        Hub.getInstance().getRankHandler().getRanks().clear();
        Hub.getInstance().getRankHandler().loadRanks();
        args.getSender().sendMessage("§aRanks have been reloaded.");
    }

}
