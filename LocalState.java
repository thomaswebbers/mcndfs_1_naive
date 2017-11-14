package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import ndfs.NDFS;


public class LocalState {
    private int workerID;
    private State state;

    public LocalState(int workerID_input, state_input){
        workerID = workerID_input;
        state = state_input;
    }

    public void setState(State input){
        state = input;
    }

    public void setWorkerID(int input){
        workerID = input;
    }

    public void getState(){
        return state;
    }

    public void getWorkerID(){
        return workerID;
    }

    @Override
    public boolean equals(LocalState input){
        if(input.getState() == state){
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