package mc_ndfs_naive_package;

import java.util.concurrent.atomic.AtomicBoolean;

import ndfs.NDFS;


public class Lock {
    private AtomicBoolean locked;

    public Lock(){
        locked.set(false);
    }

    public void lock(){
        boolean isLocked;
        do{
            isLocked = locked.getAndSet(true);
        }while(isLocked);
    }

    public void unlock(){
        locked.set(false);
    }
}
