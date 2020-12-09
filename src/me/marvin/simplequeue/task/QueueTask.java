package me.marvin.simplequeue.task;

import me.marvin.simplequeue.SimpleQueueHandler;
import me.marvin.simplequeue.queue.Queue;
import me.marvin.simplequeue.queue.QueueEntry;

import java.util.Iterator;

public class QueueTask<T> extends Thread {
    private volatile boolean stopped;
    private SimpleQueueHandler<T> handler;

    public QueueTask(SimpleQueueHandler<T> handler) {
        this.handler = handler;
        this.stopped = false;
        setDaemon(true);
        setName("queue-task");
    }

    public void setStopped() {
        this.stopped = true;
    }

    @Override
    public void run() {
        while (!stopped) {
            Iterator<Queue<T>> iterator = handler.getQueues().values().iterator();
            iterator.forEachRemaining(queue -> {
                if (!queue.isPaused()) {
                    QueueEntry<T> entry;

                    try {
                        entry = queue.getEntries().get(0);
                    } catch (IndexOutOfBoundsException ex) {
                        entry = null;
                    }

                    if (entry != null) {
                        if (handler.getRemovePredicate() != null) {
                            if (entry.getEntry() != null) {
                                handler.getConsumer().accept(entry.getEntry(), queue, handler);
                            }

                            if (handler.getRemovePredicate().test(entry.getEntry())) {
                                queue.getEntries().remove(entry);
                                handler.getEntries().remove(entry.getEntry());
                            }
                        } else {
                            if (entry.getEntry() != null) {
                                handler.getConsumer().accept(entry.getEntry(), queue, handler);
                            }

                            queue.getEntries().remove(entry);
                            handler.getEntries().remove(entry.getEntry());
                        }
                    }
                }
            });

            try {
                Thread.sleep(handler.getDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}