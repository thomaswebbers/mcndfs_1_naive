package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker {

    private final Graph graph;
    private final Colors colors = new Colors();
    private boolean result = false;
    private final Map<State, Integer> countMap = new HashMap<State, Integer>();
    public volatile Lock lock = new Lock();

    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class CycleFoundException extends Exception {
    }

    /**
     * Constructs a Worker object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public Worker(File promelaFile) throws FileNotFoundException {

        this.graph = GraphFactory.createGraph(promelaFile);
        //create global_graph and localGraph (which is a graph array?)
    }

    private void dfsRed(State s) throws CycleFoundException {

        private int myID = Thread.currentThread().getId(); //!gets thread ID
        //s.pink[i] := true
        colors.setPink(s, myID);
        //for all t in post(s) do
        for (state t: graph.post(s)){
            //if t.color[i]=cyan
            if(colors.hasColor(t, myID, Color.CYAN)){
                //report cycle and exit all
                throw new CycleFoundException();
            }
            //if NOT t.pink[i] && NOT t.red
            if(colors.hasPink(t, myID) && colors.hasRed(t)){
                //dfs_red(t, i)
                dfsRed(t);
            }
        }
        //if s ELEMENT OF Accepting
        if(s.isAccepting()){
            //s.count := s.count - 1
            lock.lock();
            int sCount = countMap.get(s);
            countMap.remove(s);
            if((sCount-1) > 0){
                countMap.put(s, sCount-1);
            }
            lock.unlock();
            //await s.count= 0 //implement exponential backoff?
            while(countMap.get(s) != null);
        }
        //s.red:= true
        colors.setRed(s);
        //s.pink[i] := false
        colors.removePink(s, myID);



    }

    private void dfsBlue(State s) throws CycleFoundException {

        private int myID = Thread.currentThread().getId(); //!gets thread ID, would this work?

        //!start
        //s.color[W_ID] := cyan
        colors.color(s, myID, Color.RED);
        //for all t in post(s) do
        for (state t: graph.post(s)){
            //if t.color[W_ID] == (white && t.red)
            if (colors.hasColor(t, myID, Color.WHITE) && colors.hasRed(t)) {
                //dfs_blue(t, W_ID) //W_ID not necessary?
                dfsBlue(t);
            }
        }
        //if s ELEMENT OF Accepted
        if (s.isAccepting()){
            //s.count := s.count +1
            lock.lock();
            if (countMap.get(s) == null){
                countMap.put(s, 1);
            }else{
                int sCount = countMap.get(s);
                countMap.remove(s);
                countMap.put(s, sCount+1);
            }
            lock.unlock();
            //dfs_red(s, W_ID)
            dfsRed(s);
        }
        //s.color[W_ID] := blue
        colors.color(s, myID, Color.BLUE);

        //!end
    }

    private void nndfs(State s) throws CycleFoundException {
        dfsBlue(s);
    }

    public void run() {
        try {
            nndfs(graph.getInitialState());
        } catch (CycleFoundException e) {
            result = true;
        }
    }

    public boolean getResult() {
        return result;
    }
}