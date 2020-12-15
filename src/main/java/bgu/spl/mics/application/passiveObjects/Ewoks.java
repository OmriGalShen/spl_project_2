package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.application.messages.AttackEvent;
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
    private static Ewoks instance = null;
    private final ArrayList<Ewok> ewoksList;
    private Boolean lock;


    public static Ewoks getInstance(int size){ //Singleton pattern
        if(instance == null){
            //only on creation of first instance synchronize:
            // this is to make sure only one thread creates the first instance
            instance = new Ewoks(size, true);
        }
        return instance;
    }


//    private static class EwoksHolder { // singleton pattern
//        private static Ewoks instance = new Ewoks(0, true);
//    }

    private Ewoks(int size, Boolean lock) { // constructor according to the size
        System.out.println("-------------------------------------------------------" + size);
        this.ewoksList = new ArrayList<>();
        for (int i=0; i < size; i++) {
            this.ewoksList.add(new Ewok(i));
        }
        this.lock = lock;
    }

    public static Ewoks getInstance() { // singleton pattern
        return instance;
    }

    public boolean isAvailable(int serial) {
        return (serial >= ewoksList.size()-1 || !ewoksList.get(serial-1).isAvailable());
    }

    public synchronized void acquire(int serialNumber) {
        if(serialNumber < ewoksList.size())
            ewoksList.get(serialNumber-1).acquire();
    }

    public void release(int serialNumber) { // it doesn't need to be synchronized (the only way to get here is thru a synchronized method)
        if(serialNumber < ewoksList.size()) {
            ewoksList.get(serialNumber-1).release();
        }
    }

    public void acquireEwoks(List <Integer> serialNumbers) { // it doesn't need to be synchronized (the acquire of an ewok is synchronized)
        Collections.sort(serialNumbers);
        for (Integer serial : serialNumbers) {
            while (!isAvailable(serial)) {
                synchronized (lock) {
                    try {
                        this.wait(); // blocking!!
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } // while
            acquire(serial);
/*
            while (!getAll(serialNumbers)){
                try {
                    this.wait(); // blocking!!
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            serialNumbers.forEach(i -> {
            });
            System.out.println("-----------------------------------------------" + serial + "------------------------------------------------------");
 */
        } // for
    }

/*
    private synchronized boolean getAll(List<Integer> serialNumbers) {
        for (int serial : serialNumbers) {
            if(!isAvailable(serial))
                return false;
        }
        return true;
    }
*/
    public synchronized void releaseEwoks(List<Integer> serialNumbers) {
        for (Integer serial : serialNumbers) {
            release(serial);
        }
        notifyAll(); // give waiting thread opportunity to catch the released Ewok
    }

    public static void initHanSoloAndC3P0(AttackEvent c, Ewoks ewoks) { // to spare code duplications
        List<Integer> serialNumbers = c.getAttack().getSerials();
        ewoks.acquireEwoks(serialNumbers);
        try { // all resources were acquired
            Thread.sleep(c.getAttack().getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ewoks.releaseEwoks(serialNumbers);
    }
}
