package me.marvin.simplequeue.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.marvin.simplequeue.provider.QueuePriorityProvider;

@Getter
@AllArgsConstructor
public class QueueEntry<T> {
    private String queueId;
    private T entry;
    private Queue<T> queue;
    private int priority;

    public QueueEntry(String queueId, T entry, Queue<T> queue, QueuePriorityProvider<T> priorityProvider) {
        this(queueId, entry, queue, priorityProvider.getEntryPriority(entry));
    }
}
