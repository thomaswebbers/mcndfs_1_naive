package ndfs.mcndfs_1_naive;

import java.util.concurrent.atomic;

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
