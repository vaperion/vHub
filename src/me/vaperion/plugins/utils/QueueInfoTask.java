package me.vaperion.plugins.utils;

import me.marvin.simplequeue.queue.Queue;
import me.marvin.simplequeue.queue.QueueEntry;
import me.vaperion.plugins.Hub;
import org.bukkit.entity.Player;

public class QueueInfoTask implements Runnable {
    @Override
    public void run() {
        for (Queue<Player> queue : Hub.getInstance().getBuiltInQueue().getQueues().values()) {
            if (queue.getEntries().isEmpty()) continue;
            for (QueueEntry<Player> entry : queue.getEntries()) {
                for (String line : Configuration.language.queueInfoMessage) {
                    entry.getEntry().sendMessage(transform(queue, entry, line));
                }
            }
        }
    }

    private String transform(Queue<Player> queue, QueueEntry<Player> entry, String line) {
        line = ChatUtils.transformLine(entry.getEntry(), line);

        for (Variable variable : Configuration.settings.variableList) {
            if (!variable.getScope().isEmpty() && !variable.getScope().equalsIgnoreCase("queue")) continue;
            Object result = null;

            if (variable.getType().equalsIgnoreCase("is-paused")) {
                result = queue.isPaused();
            }
            if (variable.getType().equalsIgnoreCase("global-players")) {
                result = CountCache.GLOBAL_COUNT;
            }
            if (variable.getType().equalsIgnoreCase("rank-priority")) {
                result = Hub.getInstance().getRankHandler().getRankForPlayer(entry.getEntry()).getPriority();
            }

            line = line.replace(variable.getFormatted(), variable.getStringForResult(result));
        }

        line = line.replace("%queue_name%", queue.getId());
        line = line.replace("%queue_pos%", String.valueOf(queue.getEntries().indexOf(entry) + 1));
        line = line.replace("%queue_max%", String.valueOf(queue.getEntries().size()));

        return ChatUtils.colorize(line);
    }
}
