package com.marcblais.scrabbleapi.utilities;

import org.hibernate.annotations.Synchronize;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ThreadsRunner {

    public static void runThreads(Queue<Thread> threads, ThreadGroup threadGroup) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();

        while (!threads.isEmpty() || threadGroup.activeCount() > 0) {
            if (threadGroup.activeCount() < cores) {
                synchronized (threads) {
                    Thread thread = threads.poll();

                    if (thread != null)
                        thread.start();
                }
            }
        }
    }
}
