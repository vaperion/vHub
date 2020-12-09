package me.vaperion.plugins.queue.impl;

import me.marvin.simplequeue.SimpleQueueHandler;
import me.marvin.simplequeue.queue.Queue;
import me.marvin.simplequeue.queue.QueueEntry;
import me.marvin.simplequeue.queue.QueueResponse;
import me.marvin.simplequeue.util.Tuple;
import me.vaperion.plugins.queue.HubQueueProvider;
import me.vaperion.plugins.utils.ChatUtils;
import me.vaperion.plugins.utils.Configuration;
import org.bukkit.entity.Player;

import static me.vaperion.plugins.utils.ChatUtils.colorize;

public class SimpleProvider implements HubQueueProvider<Queue<Player>> {
    private SimpleQueueHandler<Player> queue;

    public SimpleProvider(SimpleQueueHandler<Player> queue) {
        this.queue = queue;
    }

    @Override
    public void offer(Player player, String queue) {
        Tuple<QueueEntry<Player>, QueueResponse> response = this.queue.queueEntry(queue, player);

        String msg = getMessage(player, response);
        if (!msg.isEmpty())
            player.sendMessage(colorize(msg));
    }

    @Override
    public void remove(Player player) {
        this.queue.unQueueEntry(player);
    }

    @Override
    public boolean isQueued(Player player, String queue) {
        if (!this.queue.getEntries().containsKey(player)) return false;
        if (queue == null) {
            return true;
        }
        QueueEntry<Player> entry = this.queue.getEntries().get(player);
        return entry.getQueue().getId().equalsIgnoreCase(queue);
    }

    public SimpleQueueHandler<Player> getHandler() {
        return this.queue;
    }

    @Override
    public Queue<Player> getQueue(String queue) {
        return this.queue.getQueues().get(queue);
    }

    @Override
    public String getQueueName(Player player) {
        if (!isQueued(player, null)) return "";
        return this.queue.getEntries().get(player).getQueue().getId();
    }

    @Override
    public int getPosition(Player player, String queue) {
        QueueEntry<Player> entry = this.queue.getEntries().get(player);
        return entry.getQueue().getEntries().indexOf(entry) + 1;
    }

    @Override
    public int getSize(String queue) {
        Queue q = this.queue.getQueues().get(queue);
        if (q == null) return 0;
        return q.getEntries().size();
    }

    private String getMessage(Player player, Tuple<QueueEntry<Player>, QueueResponse> response) {
        String message = "";
        switch (response.getValue()) {
            case QUEUE_NOT_FOUND: {
                message = Configuration.language.queueNotFoundMessage;
                break;
            }
            case ALREADY_QUEUED: {
                message = Configuration.language.queueAlreadyMessage;
                break;
            }
            case QUEUE_SUCCESS: {
                message = Configuration.language.queueSuccessMessage;
                break;
            }
            case QUEUE_FULL: {
                message = Configuration.language.queueFullMessage;
                break;
            }
        }

        if (response != null && response.getKey() != null && response.getKey().getQueue() != null) message = message.replace("%queue%", response.getKey().getQueue().getId());
        else {
            message = message.replace("%queue% ", "");
            message = message.replace(" %queue%", "");
            message = message.replace("%queue%", "");
        }

        return message;
    }

    @Override
    public void createQueue(String queue) {
        this.queue.createQueue(queue);
    }

    @Override
    public void destroyQueue(String queue) {
        Queue<Player> q = getQueue(queue);
        if (q == null) return;
        for (QueueEntry<Player> entry : q.getEntries()) {
            this.queue.unQueueEntry(entry.getEntry());
            entry.getEntry().sendMessage(ChatUtils.colorize(Configuration.language.queueRemovedMessage.replace("%queue%", q.getId())));
        }
        this.queue.getQueues().remove(queue);
    }
}
