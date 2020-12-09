package me.marvin.simplequeue.queue;

import lombok.Data;

import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Queue<T> {
    private CopyOnWriteArrayList<QueueEntry<T>> entries;
    private boolean paused;
    private String id;
    private int limit;

    public Queue(String id) {
        this(id, -1);
    }

    public Queue(String id, int limit) {
        this.entries = new CopyOnWriteArrayList<>();
        this.paused = false;
        this.id = id;
        this.limit = limit;
    }
}
