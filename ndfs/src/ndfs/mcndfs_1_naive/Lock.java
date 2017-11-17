package ndfs.src.ndfs.mcndfs_1_naive;

import java.util.concurrent.atomic.AtomicBoolean;

public class Lock {
    private AtomicBoolean locked = new AtomicBoolean();

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
