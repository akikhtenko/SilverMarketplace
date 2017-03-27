package com.silverbars.marketplace.orderboard.common;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.generate;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LockingRunnerStressTest {
    private static final int MIN_SLEEP_MS = 5;
    private static final int MAX_SLEEP_MS = 30;
    private static final int EXCLUSIVE_RESOURCES_NUMBER = 10;
    private static final int ITERATIONS_PER_RESOURCE = 100;

    private static class MutatingOperationWrapper {
        public Map<UUID, Integer> counters = newHashMap();

        public Integer counterFor(UUID resource) {
            return counters.get(resource);
        }

        public void mutateCounter(UUID resource) {
            try {
                sleep(ThreadLocalRandom.current().nextInt(MIN_SLEEP_MS, MAX_SLEEP_MS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Integer current = counters.getOrDefault(resource, 0);
            counters.put(resource, ++current);
        }
    }

    @Test
    public void should_run_concurrent_operation_thread_safely() throws InterruptedException {
        LockingRunner<UUID> withLock = new LockingRunner<>();
        MutatingOperationWrapper operationWrapper = new MutatingOperationWrapper();
        ExecutorService executor = newFixedThreadPool(10);
        List<UUID> exclusiveResources = generate(UUID::randomUUID).limit(EXCLUSIVE_RESOURCES_NUMBER).collect(toList());

        range(0, ITERATIONS_PER_RESOURCE).forEach(i -> {
            exclusiveResources.forEach(resource -> executor.submit(
                    () -> withLock.on(resource).run(() -> operationWrapper.mutateCounter(resource))));
        });
        executor.shutdown();
        executor.awaitTermination(30, SECONDS);

        exclusiveResources.forEach(resource ->
                assertThat(operationWrapper.counterFor(resource), is(ITERATIONS_PER_RESOURCE)));
    }
}