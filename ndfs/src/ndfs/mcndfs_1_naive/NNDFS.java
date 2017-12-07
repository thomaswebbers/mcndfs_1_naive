package ndfs.mcndfs_1_naive;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import graph.State;
import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    private int numberOfWorkers;
    private GlobalColors globalColors = new GlobalColors();

    Worker[] workers;
    Thread[] threads;
    File promelaFile;
    Boolean result = false;
    Map<State, Integer> countMap;
    //Make long??

    public volatile Lock lock = new ReentrantLock();

    public volatile Condition stateFinish = lock.newCondition();

    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class CycleFoundException extends Exception {
    }


    public NNDFS(File promelaFile, int numberOfThreads) throws  FileNotFoundException{
        numberOfWorkers = numberOfThreads;
        workers = new Worker[numberOfThreads];
        globalColors = new GlobalColors();
        this.promelaFile = promelaFile;

        countMap = globalColors.getCountMap();
    }

   @Override
    public boolean ndfs() {

       ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkers);
       CompletionService<Boolean> completionService = new ExecutorCompletionService(executor);


       //Create and start threads with worker objects in them
       for(int i = 0; i < numberOfWorkers; i++) {
           try {
               completionService.submit(new Worker(i, globalColors, promelaFile, lock, countMap, stateFinish));
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
       }

       try {
           result = completionService.take().get();
           executor.shutdownNow();
           System.out.println(result);
       } catch (InterruptedException | ExecutionException e) {
           e.printStackTrace();
       }

       executor.shutdownNow();
       return globalColors.result();
    }
}


