package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.lang.Thread;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker implements Callable{

    private int workerId;
    private final Graph graph;
    private GlobalColors globalColors;
    private LocalColors localColors = new LocalColors();
    //Make long??
    public volatile Lock lock = null;
    public volatile Condition stateFinish = null;

    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class resultFoundException extends Exception {
    }

    /**
     * Constructs a Worker object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public Worker(int workerId, GlobalColors globalColors, File promelaFile, Lock lock, Map<State, Integer> countMap, Condition stateFinish) throws FileNotFoundException {
        this.workerId = workerId;
        this.globalColors = globalColors;
        this.graph = GraphFactory.createGraph(promelaFile);
        this.lock = lock;
        this.stateFinish = stateFinish;

        System.out.println("MY ID is: " + this.workerId);

        //create global_graph and localGraph (which is a graph array?)
    }

    private void dfsRed(State s) throws resultFoundException{
        if(globalColors.hasResult()){
            return;
        }



        //System.out.println("MY ID is: " + workerId);

        //s.pink[i] := true
        localColors.setPink(s, workerId);
        //for all t in post(s) do
        for (State t: graph.post(s)){
            //if t.color[i]=cyan
            if(localColors.hasColor(t, workerId, Color.CYAN)){
                //report cycle and exit all
                globalColors.supplyResult(true);
                globalColors.resultFound();
                System.out.println("FINALRED");
                throw new resultFoundException();
            }
            //if NOT t.pink[i] && NOT t.red
            if(!localColors.hasPink(t, workerId) && !globalColors.hasRed(t)){
                //dfs_red(t, i)
                dfsRed(t);
            }
        }
        //if s ELEMENT OF Accepting
        if(s.isAccepting()){
            //s.count := s.count - 1
            lock.lock();
            Integer sCount = globalColors.getCountValue(s);
            globalColors.removeFromCount(s);
            if((sCount-1) > 0 ){
                globalColors.addValue(s, sCount-1);


            }
            while(globalColors.getCountValue(s) != null){
           // while(globalColors.getCountMap().get(s) != null) {
                try {
                    System.out.println("Waiting for worker to reach 0...");
                     stateFinish.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stateFinish.signalAll();
            lock.unlock();

        }
        //s.red:= true
        globalColors.setRed(s);
        //s.pink[i] := false
        localColors.removePink(s, workerId);
    }

    private void dfsBlue(State s) throws resultFoundException{
        System.out.println("hasresult: "+globalColors.hasResult());
        if(globalColors.hasResult()){
            return;
        }


        //System.out.println("MY ID is: " + myID);
        //!start
        //s.color[W_ID] := cyan
        localColors.color(s, this.workerId, Color.CYAN);

        //for all t in post(s) do
        for (State t: graph.post(s)){
            //if t.color[W_ID] == (white && NOT t.red)
            if (localColors.hasColor(t, this.workerId, Color.WHITE) && !globalColors.hasRed(t)) {
                //dfs_blue(t, W_ID) //W_ID not necessary?
                dfsBlue(t);
            }
        }
        //if s ELEMENT OF Accepted

        if (s.isAccepting()){
            //s.count := s.count +1
            lock.lock();
            if(globalColors.getCountValue(s) == null){
            //if (countMap.get(s) == null){
                globalColors.addValue(s,1);

            }else{
                int sCount = globalColors.getCountValue(s);
                globalColors.removeFromCount(s);
                globalColors.addValue(s, sCount+1);

            }
            lock.unlock();
            //dfs_red(s, W_ID)
            dfsRed(s);
        }
        //s.color[W_ID] := blue
        localColors.color(s, this.workerId, Color.BLUE);
    }


    public boolean getResult() {
        return globalColors.result();
    }


    private void nndfs(State s) throws resultFoundException{
        dfsBlue(s);
        if(!globalColors.hasResult()){
            System.out.println("FINALBLUE");
            globalColors.resultFound();
            globalColors.supplyResult(false);
            throw new resultFoundException();
        }

    }

    public Boolean call() {
        try{
            nndfs(graph.getInitialState());
        }catch (resultFoundException e){
            return globalColors.result();
        }
        return globalColors.result();
    }
}