package me.dzikimlecz.discordchess.util.concurrent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class BlockingContainerTest {
    private final BlockingContainer<String> container = BlockingContainer.create();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Test
    @DisplayName("Should put a value in the container")
    public void putInstantlyTest() {
        //When
        var value = "hello";
        container.putInstantly(value);
        //Then
        assertTrue(container.contains(value));
    }

    @Test
    @DisplayName("Should take a value from the container")
    public void takeInstantlyTest() {
        //When
        var value = "hello";
        container.putInstantly(value);
        //Then
        assertEquals(value, container.takeInstantly());
    }

    @Test
    @DisplayName("Should clear the container")
    public void clearTest() {
        //Given
        container.putInstantly("hello");
        //When
        container.clear();
        //Then
        assertNull(container.takeInstantly());
    }

    @Test
    @DisplayName("Should put a value to the full container after waiting")
    public void putTest() throws InterruptedException {
        //Given
        container.putInstantly("first");
        //When
        executor.schedule(container::clear, 2, SECONDS);
        var value = "second";
        container.put(value);
        //Then
        assertEquals(value, container.takeInstantly());
        executor.shutdown();
    }

    @Test
    @DisplayName("Should take a value from the container after waiting")
    public void takeTest() throws InterruptedException {
        //Given
        container.clear();
        //When
        var value = "etwas";
        executor.schedule(() -> container.putInstantly(value), 2, SECONDS);
        //Then
        final var taken = container.take();
        assertEquals(value, taken);
        executor.shutdown();
    }

    @Test
    public void isEmptyTest() {
        //When
        container.clear();
        //Then
        assertTrue(container.isEmpty());
        container.putInstantly("literally anything");
        assertFalse(container.isEmpty());
    }

    @Test
    public void isFilledTest() {
        //When
        container.clear();
        //Then
        assertFalse(container.isFilled());
        container.putInstantly("literally anything");
        assertTrue(container.isFilled());
    }

    @Test
    @DisplayName("Should wait until the container becomes empty")
    public void waitUntilEmptyTest() {
        //Given
        container.putInstantly("hope you're having a nice day!");
        //When
        executor.schedule(container::clear, 2, SECONDS);
        //Then
        assertTimeoutPreemptively(Duration.of(2500, MILLIS), container::waitUntilEmpty);
        executor.shutdown();
    }

    @Test
    @DisplayName("Should wait until the container becomes fill")
    public void waitUntilFillTest() {
        //Given
        container.clear();
        //When
        var value = "etwas";
        executor.schedule(() -> container.putInstantly(value), 2, SECONDS);
        //Then
        assertTimeoutPreemptively(Duration.of(2500, MILLIS), container::waitUntilFilled);
        executor.shutdown();
    }

}