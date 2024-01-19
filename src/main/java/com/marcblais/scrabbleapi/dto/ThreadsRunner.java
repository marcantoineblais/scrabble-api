package com.marcblais.scrabbleapi.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreadsRunner {

    public static void runThreads(List<Thread> threads) {
        Set<Thread> threadSubSet = new HashSet<>();
        int cores = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < threads.size(); i++) {
            threadSubSet.add(threads.get(i));

            if (i % cores == cores - 1 || i == threads.size() - 1) {
                for (Thread thread : threadSubSet) {
                    thread.start();
                }

                for (Thread thread : threadSubSet) {
                    try {
                        thread.join();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                threadSubSet.clear();
            }
        }
    }
}
