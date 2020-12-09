package me.marvin.simplequeue.provider;

public interface QueuePriorityProvider<T> {
    String getProviderName();
    int getEntryPriority(T t);
}
