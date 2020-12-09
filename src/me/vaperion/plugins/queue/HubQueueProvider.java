package me.vaperion.plugins.queue;

import org.bukkit.entity.Player;

public interface HubQueueProvider<T> {
    void offer(Player player, String queue);
    void remove(Player player);
    boolean isQueued(Player player, String queue);
    String getQueueName(Player player);
    int getPosition(Player player, String queue);
    int getSize(String queue);

    T getQueue(String queue);
    void createQueue(String queue);
    void destroyQueue(String queue);
}
