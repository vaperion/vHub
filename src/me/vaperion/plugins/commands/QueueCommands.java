package me.vaperion.plugins.commands;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;
import me.vaperion.plugins.Hub;
import me.vaperion.plugins.utils.ChatUtils;
import me.vaperion.plugins.utils.Configuration;

public class QueueCommands {

    @Command(name = "joinqueue", aliases = {"jq", "queue.join"}, inGameOnly = true)
    public void queueJoinCommand(CommandArgs args) {
        if (args.length() == 0) {
            args.getSender().sendMessage("§cUsage: /queue join (name)");
            return;
        }
        Hub.getInstance().getQueueProvider().offer(args.getPlayer(), args.getArgs(0));
    }

    @Command(name = "leavequeue", aliases = {"lq", "queue.leave"}, inGameOnly = true)
    public void queueLeaveCommand(CommandArgs args) {
        if (!Hub.getInstance().getQueueProvider().isQueued(args.getPlayer(), null)) {
            args.getSender().sendMessage(ChatUtils.colorize(Configuration.language.queueNotQueuedMessage));
            return;
        }
        String name = Hub.getInstance().getQueueProvider().getQueueName(args.getPlayer());
        Hub.getInstance().getQueueProvider().remove(args.getPlayer());
        args.getSender().sendMessage(ChatUtils.colorize(Configuration.language.queueLeftMessage).replace("%queue%", name));
    }

}
