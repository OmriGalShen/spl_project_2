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
    private static Ewoks instance=null;//Singleton pattern
    private ArrayList<Ewok> ewoksList;

    private Ewoks(){
        this.ewoksList = new ArrayList<>(0);
    }
    private Ewoks(int size){
        this.ewoksList = new ArrayList<>();
        for (int i = 0; i <size ; i++) {
            this.ewoksList.add(new Ewok(i));
        }
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
    public static Ewoks getInstance(int size){ //Singleton pattern
        if(instance == null){
            //only on creation of first instance synchronize:
            // this is to make sure only one thread creates the first instance
            synchronized (Ewoks.class){
                if(instance==null)
                    instance = new Ewoks(size);
            }
        }
        return instance;
    }
    public void acquire(int serialNumber) {
        Ewok ewok = ewoksList.get(serialNumber-1);
        while (!ewok.available){
            synchronized (this){
                try {
                    wait(); //blocking!!
                    ewok.acquire();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException on Ewoks acquire()");
                    e.printStackTrace();
                }
            }
        }
    }
    public void release(int serialNumber){
        Ewok ewok = ewoksList.get(serialNumber-1);
        ewok.release();
        synchronized (this) {
            notifyAll(); // give waiting thread opportunity to catch the released Ewok
        }
    }
}
