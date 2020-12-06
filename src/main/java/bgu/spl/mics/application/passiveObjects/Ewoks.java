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
    private static Ewoks instance;
    private List<Ewok> ewoksList;

    private Ewoks(){
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
        this.ewoksList = Collections.synchronizedList(new ArrayList<>(size));
    }
    public void acquire(int serialNumber){
        Ewok e = ewoksList.get(serialNumber);
        while (!e.available){
            synchronized (this){
                try {
                    wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

        }
        ewoksList.get(serialNumber).acquire();
    }
    public void release(int serialNumber){
        ewoksList.get(serialNumber).release();
        synchronized (this) {
            notifyAll();
        }
    }
    public boolean isAvailable(int serialNumber){
        return ewoksList.get(serialNumber).available;
    }
}
