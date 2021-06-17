package me.dzikimlecz.discordchess.util.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class BlockingContainerImpl<E> implements BlockingContainer<E> {
    private final BlockingQueue<E> content = new ArrayBlockingQueue<>(1);
    private @Nullable E cache;
    private final Object lock = new Object();

    @Override public @Nullable E put(@NotNull E value) throws InterruptedException {
        cache = null;
        final var old = content.peek();
        content.put(value);
        synchronized (lock) {
            lock.notifyAll();
        }
        return old;
    }

    @Override public @Nullable E putInstantly(@Nullable E value) {
        cache = null;
        final var old = content.poll();
        if (value != null) content.offer(value);
        synchronized (lock) {
            lock.notifyAll();
        }
        return old;
    }

    @Override public @NotNull E take() throws InterruptedException {
        if (cache == null)
            cache = content.take();
        return cache;
    }

    @Override public @Nullable E takeInstantly() {
        return content.peek();
    }

    @Override public void clear() {
        this.content.clear();
        cache = null;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override public boolean isEmpty() {
        return content.isEmpty() && cache == null;
    }

    @Override public boolean isFilled() {
        return !content.isEmpty() || cache != null;
    }

    @Override public boolean contains(@NotNull E element) {
        if (cache != null)
            return cache.equals(element);
        else {
            final var peeked = content.peek();
            return peeked != null && peeked.equals(element);
        }
    }

    @Override public void waitUntilEmpty() throws InterruptedException {
        if (cache != null) {
            content.add(cache);
            cache = null;
        }
        synchronized (lock) {
            while (isFilled())
                lock.wait();
        }
    }

    @Override public void waitUntilFilled() throws InterruptedException {
        if (cache != null) return;
        synchronized (lock) {
            while (isEmpty())
                lock.wait();
        }
    }
}
