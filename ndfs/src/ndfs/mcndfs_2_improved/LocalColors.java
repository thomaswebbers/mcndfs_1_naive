package ndfs.mcndfs_2_improved;

import graph.State;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomaswebbers on 02/12/2017.
 */
public class LocalColors {
    private final Map<LocalState, Color> map = new HashMap<LocalState, Color>();
    private final Map<LocalState, Boolean> pinkMap = new HashMap<LocalState, Boolean>(); //if <State,workerID> is in here, it is pink



    /**
     * Returns <code>true</code> if the specified state has the specified color,
     * <code>false</code> otherwise.
     *
     * @param state
     *            the state to examine.
     * @param color
     *            the color
     * @return whether the specified state has the specified color.
     */
    public boolean hasColor(State state, long workerID, Color color) {
        LocalState input = new LocalState(workerID, state);

        // The initial color is white, and is not explicitly represented.
        if (color == Color.WHITE) {
            return map.get(input) == null;
        } else {
            return map.get(input) == color;
        }
    }

    /**
     * Gives the specified state the specified color.
     *
     * @param state
     *            the state to color.
     * @param color
     *            color to give to the state.
     */
    public void color(State state, long workerID, Color color) {
        LocalState input = new LocalState(workerID, state);

        if (color == Color.WHITE) {
            map.remove(input);
        } else {
            map.put(input, color);
        }
    }

    public void setPink(State state, long workerID){
        LocalState input = new LocalState(workerID, state);
        pinkMap.put(input, true);
    }

    public void removePink(State state, long workerID){
        LocalState input = new LocalState(workerID, state);
        pinkMap.remove(input);
    }

    public boolean hasPink(State state, long workerID){
        LocalState input = new LocalState(workerID, state);
        return pinkMap.get(input) != null;
    }
}
