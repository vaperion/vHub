package me.marvin.simplequeue;

import lombok.Getter;
import me.marvin.simplequeue.provider.QueuePriorityProvider;
import me.marvin.simplequeue.queue.Queue;
import me.marvin.simplequeue.queue.QueueConsumer;
import me.marvin.simplequeue.queue.QueueEntry;
import me.marvin.simplequeue.queue.QueueResponse;
import me.marvin.simplequeue.task.QueueTask;
import me.marvin.simplequeue.util.Tuple;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Getter
public class SimpleQueueHandler<T> {
    private Comparator<QueueEntry<T>> comparator;
    private ConcurrentHashMap<String, Queue<T>> queues;
    private ConcurrentHashMap<T, QueueEntry<T>> entries;
    private QueuePriorityProvider<T> priorityProvider;
    private QueueConsumer<T> consumer;
    private Predicate<T> removePredicate;
    private QueueTask<T> task;
    private long delay;

    public SimpleQueueHandler(long delay, QueuePriorityProvider<T> priorityProvider, QueueConsumer<T> consumer) {
        this(delay, priorityProvider, consumer, null);
    }

    public SimpleQueueHandler(long delay, QueuePriorityProvider<T> priorityProvider, QueueConsumer<T> consumer, Predicate<T> removePredicate) {
        this.entries = new ConcurrentHashMap<>();
        this.queues = new ConcurrentHashMap<String, Queue<T>>() {
            @Override
            public Queue<T> get(Object k) {
                return super.get(k.toString().toLowerCase());
            }

            @Override
            public Queue<T> put(String k, Queue<T> v) {
                return super.put(k.toLowerCase(), v);
            }
        };
        this.priorityProvider = priorityProvider;
        this.removePredicate = removePredicate;
        this.consumer = consumer;
        this.comparator = ((o1, o2) -> o2.getPriority() - o1.getPriority());
        this.task = new QueueTask<>(this);
        this.delay = delay;
        this.task.start();
    }


    public Queue<T> createQueue(String id) {
        return createQueue(id, -1);
    }

    public Queue<T> createQueue(String id, int limit) {
        return queues.put(id, new Queue<>(id, limit));
    }

    public Tuple<QueueEntry<T>, QueueResponse> queueEntry(String queue, T tEntry) throws NullPointerException {
        return queueEntry(queues.get(queue), tEntry);
    }

    public Tuple<QueueEntry<T>, QueueResponse> queueEntry(Queue<T> queue, T tEntry) throws NullPointerException {
        if (queue == null) {
            return new Tuple<>(null, QueueResponse.QUEUE_NOT_FOUND);
        }

        QueueEntry<T> entry = new QueueEntry<>(queue.getId(), tEntry, queue, priorityProvider);

        if (queue.getLimit() != -1 && queue.getEntries().size() + 1 > queue.getLimit()) {
            return new Tuple<>(null, QueueResponse.QUEUE_FULL);
        }

        if (entries.containsKey(tEntry)) {
            return new Tuple<>(entries.get(tEntry), QueueResponse.ALREADY_QUEUED);
        }

        entries.put(tEntry, entry);
        queue.getEntries().add(entry);
        queue.getEntries().sort(comparator);
        return new Tuple<>(entry, QueueResponse.QUEUE_SUCCESS);
    }

    public boolean unQueueEntry(T tEntry) {
        if (!entries.containsKey(tEntry)) {
            return false;
        }

        QueueEntry<T> entry = entries.get(tEntry);
        Queue<T> queue = queues.get(entry.getQueueId());

        if (queue == null) {
            throw new NullPointerException("queue cannot be null");
        }

        if (!queue.getEntries().contains(entry)) {
            return false;
        }

        queue.getEntries().remove(entry);
        entries.remove(tEntry);
        return false;
    }
}
