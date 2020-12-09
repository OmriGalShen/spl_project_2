package bgu.spl.mics.application.passiveObjects;

import java.util.*;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks instance=null;
    private List<Ewok> ewoksList;

    private Ewoks(){
        this.ewoksList = Collections.synchronizedList(new ArrayList<>(0));
    }
    public static Ewoks getInstance(){ //Singleton pattern
        if(instance == null){
            //only on creation of first instance synchronize:
            // this is to make sure only one thread creates the first instance
            synchronized (Ewoks.class){
                if(instance==null)
                    instance = new Ewoks();
            }
        }
        return instance;
    }
    public void initialize(int size){
        for (int i = 0; i <size ; i++) {
            this.ewoksList.add(new Ewok(i));
        }
    }
    public void acquire(int serialNumber){
        Ewok e = ewoksList.get(serialNumber-1);
        while (!e.available){
            synchronized (this){
                try {
                    wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

        }
        e.acquire();
    }
    public void release(int serialNumber){
        ewoksList.get(serialNumber-1).release();
        synchronized (this) {
            notifyAll();
        }
    }
}
