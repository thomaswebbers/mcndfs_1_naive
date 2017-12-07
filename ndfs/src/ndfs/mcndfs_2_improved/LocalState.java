package ndfs.mcndfs_2_improved;

import java.util.Objects;

import graph.State;



public class LocalState {
    long workerID;
    State state;

    public LocalState(long workerID_input, State state_input){
        workerID = workerID_input;
        state = state_input;
    }

    public void setState(State input){
        state = input;
    }

    public void setWorkerID(long input){
        workerID = input;
    }

    public State getState(){
        return state;
    }

    public long getWorkerID(){
        return workerID;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals( this.getClass())) {
            return false;
        }

        LocalState input = (LocalState) obj;

        if(input.getState().equals(state)){
            if (input.getWorkerID() == workerID){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(state, workerID); //works because states can be meaningfully compared to each other
    }
}