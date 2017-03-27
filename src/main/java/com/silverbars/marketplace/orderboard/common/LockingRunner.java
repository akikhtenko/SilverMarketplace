package com.silverbars.marketplace.orderboard.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockingRunner<R> {
    private LoadingCache<R, Lock> locks =
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<R, Lock>() {
                @Override
                public Lock load(R key) throws Exception {
                    return new ReentrantLock();
                }
            });

    public OperationRunner on(R resource) {
        return new OperationRunner(resource);
    }

    public class OperationRunner {
        private R resource;

        private OperationRunner(R resource) {
            this.resource = resource;
        }

        public void run(Runnable operation) {
            run(() -> {
                operation.run();
                return null;
            });
        }

        public <A> A run(Callable<A> operation) {
            Lock resourceLock = locks.getUnchecked(resource);
            resourceLock.lock();
            try {
                return operation.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                resourceLock.unlock();
            }
        }
    }
}
