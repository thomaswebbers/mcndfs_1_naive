package ndfs.mcndfs_2_improved;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

import graph.State;

/**
 * This class provides a color map for graph states.
 */
public class GlobalColors {

    private final ConcurrentHashMap<State, Integer> countMap ;
    private final ConcurrentHashMap<State, Boolean> redMap; //if State is in here, it is red

    private Boolean hasResult;
    private Boolean result;


    //! the boolean is theoretically unnecessary, but removing it might break HashMap


    GlobalColors() {
        countMap = new ConcurrentHashMap<State, Integer>();
        redMap = new ConcurrentHashMap<State, Boolean>();
        hasResult = false;
        result = false;
    }


    public void increaseCount (State state){countMap.put(state, countMap.get(state) + 1);}

    public void removeFromCount (State state){countMap.remove(state);}

    public Boolean hasResult(){return hasResult;}

    public void resultFound(){hasResult = true;}

    public Boolean result(){return result;}

    public void supplyResult(Boolean result){this.result = result;}

    public Integer getCountValue (State state){return countMap.get(state);}

    public void addValue(State state, Integer value){countMap.put(state, value);}

    public Map<State, Integer> getCountMap() {return countMap;}

    public void setRed(State state){
        redMap.put(state, true);
    }

    public boolean hasRed(State state){
        return redMap.get(state) != null; //I know, i know. Redundant.
    }


}
