package me.dzikimlecz.discordchess.util.concurrent;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface BlockingContainer<E> {

    /**
     * Sets the value of the container, waits until empty if it's not
     * @param value new value of the container
     * @return old value of the container, if it wasn't empty before waiting
     * @throws InterruptedException if interrupted while waiting
     */
    @Nullable E put(@NotNull E value) throws InterruptedException;

    /**
     * Sets the value of the container, no matter if it's already present or not.
     * @param value new value of the container
     * @return old value of the container, if it wasn't empty before waiting
     */
    @Nullable E putInstantly(@Nullable E value);

    /**
     * Takes the value of the container, awaits if it's not present
     * @return value of the container, when it became available
     * @throws InterruptedException if interrupted while waiting
     */
    @NotNull E take() throws InterruptedException;

    /**
     * Takes the value of the container, no matter if it's present or not.
     * @return value of the container, or null if there isn't any.
     */
    @Nullable E takeInstantly();

    /**
     * removes any value.
     */
    void clean();

    /**
     * Checks if there is no present value.
     * @return {@code true} if the container is empty, {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * Checks if there is any present value.
     * @return {@code false} if the container is empty, {@code true} otherwise.
     */
    boolean isFilled();

    /**
     * Checks if there is any present value and if it is equal to the given one.
     * @param element value to be compared
     * @return {@code true} if the container isn't empty and its value is equal to the given one, {@code false} otherwise.
     */
    boolean contains(@NotNull E element);

    /**
     * awaits until the container is empty
     * @throws InterruptedException if interrupted while waiting
     */
    void waitUntilEmpty() throws InterruptedException;

    /**
     * awaits until the container is filled
     * @throws InterruptedException if interrupted while waiting
     */
    void waitUntilFilled() throws InterruptedException;

    /**
     * creates empty BlockingContainer
     * @param <T> type of the value to be stored
     * @return new instance of BlockingContainer of the given type
     */
    static<T> BlockingContainer<T> create() {
        return new BlockingContainerImpl<>();
    }

    /**
     * creates BlockingContainer with the given content
     * @param value initial value to be stored in the container.
     * @param <T> type of the value to be stored
     * @return new instance of BlockingContainer of the given type and value
     */
    static<T> BlockingContainer<T> of(@NotNull T value) {
        final var container = new BlockingContainerImpl<T>();
        container.putInstantly(value);
        return container;
    }

}
