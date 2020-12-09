package me.vaperion.plugins.commands;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;
import me.marvin.simplequeue.queue.Queue;
import me.marvin.simplequeue.queue.QueueEntry;
import me.vaperion.plugins.Hub;
import me.vaperion.plugins.utils.ChatUtils;
import me.vaperion.plugins.utils.Configuration;
import org.bukkit.entity.Player;

public class QueueAdminCommands {

    @Command(name = "queue")
    public void queueCommand(CommandArgs args) {
        args.getSender().sendMessage("§7§m------------------------------------------");
        args.getSender().sendMessage("§a§lQueue");
        args.getSender().sendMessage("");
        args.getSender().sendMessage(" §a/joinqueue (name) §7Join a queue");
        args.getSender().sendMessage(" §a/leavequeue §7Leave your queue");
        if (args.getSender().hasPermission("vhub.admin")) {
            args.getSender().sendMessage(" §a/queue create (name) §7Create a queue");
            args.getSender().sendMessage(" §a/queue remove (name) §7Remove a queue");
            args.getSender().sendMessage(" §a/queue info (name) §7View a queue");
            args.getSender().sendMessage(" §a/queue pause (name) §7Pause a queue");
            args.getSender().sendMessage(" §a/queue setlimit (name) (number) §7Set queue limit");
        }
        args.getSender().sendMessage("§7§m------------------------------------------");
    }

    @Command(name = "queue.create", permission = "vhub.admin")
    public void queueCreateCommand(CommandArgs args) {
        if (args.length() == 0) {
            args.getSender().sendMessage("§cUsage: /queue info (name)");
            return;
        }
        Queue<Player> queue = (Queue<Player>) Hub.getInstance().getQueueProvider().getQueue(args.getArgs(0));
        if (queue != null) {
            args.getSender().sendMessage("§cA queue with the name " + args.getArgs(0) + " already exists.");
            return;
        }
        Hub.getInstance().getQueueProvider().createQueue(args.getArgs(0));
        args.getSender().sendMessage("§aQueue for " + args.getArgs(0) + " has been created.");
    }

    @Command(name = "queue.remove", permission = "vhub.admin")
    public void queueRemoveCommand(CommandArgs args) {
        if (args.length() == 0) {
            args.getSender().sendMessage("§cUsage: /queue remove (name)");
            return;
        }
        Queue<Player> queue = (Queue<Player>) Hub.getInstance().getQueueProvider().getQueue(args.getArgs(0));
        if (queue == null) {
            args.getSender().sendMessage("§cA queue with the name " + args.getArgs(0) + " does not exist.");
            return;
        }
        Hub.getInstance().getQueueProvider().destroyQueue(args.getArgs(0));
        args.getSender().sendMessage("§aQueue for " + queue.getId() + " has been removed. A total of " + queue.getEntries().size() + " players have been unqueued.");
    }

    @Command(name = "queue.info", permission = "vhub.admin")
    public void queueInfoCommand(CommandArgs args) {
        if (args.length() == 0) {
            args.getSender().sendMessage("§cUsage: /queue info (name)");
            return;
        }
        Queue<Player> queue = (Queue<Player>) Hub.getInstance().getQueueProvider().getQueue(args.getArgs(0));
        if (queue == null) {
            args.getSender().sendMessage(ChatUtils.colorize(Configuration.language.queueNotFoundMessage));
            return;
        }
        args.getSender().sendMessage("§7§m------------------------------------------");
        args.getSender().sendMessage("§a§l" + queue.getId() + " Queue");
        args.getSender().sendMessage(" §7* §aLimit: §r" + (queue.getLimit() == -1 ? "Unlimited" : String.valueOf(queue.getLimit())));
        args.getSender().sendMessage(" §7* §aPaused: §r" + queue.isPaused());
        args.getSender().sendMessage(" §7* §aPlayers (" + queue.getEntries().size() + "): §r");
        for (int i = 0; i < queue.getEntries().size(); i++) {
            if (i >= 5) break;
            QueueEntry<Player> entry = queue.getEntries().get(i);
            args.getSender().sendMessage("   §7- §r" + ChatUtils.colorize(Hub.getInstance().getRankHandler().getRankForPlayer(entry.getEntry()).getColor()) + entry.getEntry().getDisplayName());
        }
        if (queue.getEntries().size() > 5) {
            args.getSender().sendMessage("   §7§oAnd " + (queue.getEntries().size() - 5) + " more...");
        }
        args.getSender().sendMessage("§7§m------------------------------------------");
    }

    @Command(name = "queue.pause", permission = "vhub.admin")
    public void queuePauseCommand(CommandArgs args) {
        if (args.length() == 0) {
            args.getSender().sendMessage("§cUsage: /queue info (name)");
            return;
        }
        Queue<Player> queue = (Queue<Player>) Hub.getInstance().getQueueProvider().getQueue(args.getArgs(0));
        if (queue == null) {
            args.getSender().sendMessage(ChatUtils.colorize(Configuration.language.queueNotFoundMessage));
            return;
        }
        queue.setPaused(!queue.isPaused());
        args.getSender().sendMessage("§aQueue for " + queue.getId() + " is " + (queue.isPaused() ? "now" : "no longer") + " paused.");
    }

    @Command(name = "queue.setlimit", permission = "vhub.admin")
    public void queueSetlimitCommand(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage("§cUsage: /queue setlimit (name) (number)");
            return;
        }
        Queue<Player> queue = (Queue<Player>) Hub.getInstance().getQueueProvider().getQueue(args.getArgs(0));
        if (queue == null) {
            args.getSender().sendMessage(ChatUtils.colorize(Configuration.language.queueNotFoundMessage));
            return;
        }
        int limit = -1;
        try {
            limit = Integer.parseInt(args.getArgs(1));
        } catch (Exception ex) {
            args.getSender().sendMessage("§cThe second argument must be a number.");
            return;
        }
        if (limit < 1 && limit != -1) {
            args.getSender().sendMessage("§cThe limit must be above 0, or -1 (= unlimited).");
            return;
        }
        queue.setLimit(limit);
        args.getSender().sendMessage("§aLimit for the " + queue.getId() + " queue has been updated to " + (limit == -1 ? "Unlimited" : String.valueOf(limit)));
    }

}
