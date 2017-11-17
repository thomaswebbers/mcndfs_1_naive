package ndfs.src.ndfs.mcndfs_1_naive;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import graph.State;
import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    public static final int NUMBER_OF_WORKERS = 2;
    private Colors colors = null;
    Worker[] workers;

    private boolean result = false;
    //Make long??
    private final Map<State, Integer> countMap = new HashMap<State, Integer>();
    public volatile Lock lock = new Lock();

    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class CycleFoundException extends Exception {
    }


    public NNDFS()  {
        colors = new Colors();
        workers = new Worker[NUMBER_OF_WORKERS+1];
    }


    private void start(File promelaFile) throws  FileNotFoundException{
        for(int i = 1; i <= NUMBER_OF_WORKERS; i++){
            Worker workerObject = new Worker(colors, promelaFile, lock);
            workers[i] = workerObject;
            new Thread (workerObject).start();
        }

        boolean result = ndfs();
    }


    public static void main(String[] args) throws FileNotFoundException {
        File promelaFile = new File(args[0]);
        new NNDFS().start(promelaFile);
    }


   @Override
    public boolean ndfs() {
       for (int i = 1; i <= NUMBER_OF_WORKERS; i++) {
           if(workers[i].getResult()){
               return true;
           }
       }
       return false;
   }
}


