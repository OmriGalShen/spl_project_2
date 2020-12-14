package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.AttackEvent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks instance = null; // singleton pattern
    private ArrayList<Ewok> ewoksList; // may be final? - Eden //////////////////////////////////////////////

    private static class EwoksHolder { // singleton pattern
        private static Ewoks instance = new Ewoks();
    }


    private Ewoks() { // singleton pattern
        this.ewoksList = new ArrayList<>(0);
    }

    public static Ewoks getInstance() { // singleton pattern
        return Ewoks.EwoksHolder.instance;
    }

    private Ewoks(int size) { // what is this for? - Eden ///////////////////////////////////////////////
        this.ewoksList = new ArrayList<>();
        for (int i=0; i < size; i++) {
            this.ewoksList.add(new Ewok(i));
        }
    }

/*    public static Ewoks getInstance() { // singleton pattern
        if(instance == null) {
            // only on creation of first instance synchronize:
            // this is to make sure only one thread creates the first instance
            synchronized (Ewoks.class) {
                if(instance == null)
                    instance = new Ewoks();
            }
        }
        return instance;
    }
*/

    public static Ewoks getInstance(int size) { // singleton pattern
        if(instance == null) {
            // only on creation of first instance synchronize:
            // this is to make sure only one thread creates the first instance
            synchronized (Ewoks.class) {
                if(instance == null)
                    instance = new Ewoks(size);
            } // synchronized
        } // if
        return instance;
    }

    public void acquire(int serialNumber) {
        Ewok ewok = ewoksList.get(serialNumber-1);
        synchronized (this) {
            while (!ewok.available) {
                try {
                    wait(); // blocking!!
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException on Ewoks acquire()");
                    e.printStackTrace();
                }
            } // while
            ewok.acquire();
        } // synchronized
    }

    public void release(int serialNumber) {
        Ewok ewok = ewoksList.get(serialNumber-1);
        ewok.release();
        synchronized (this) {
            notifyAll(); // give waiting thread opportunity to catch the released Ewok
        }
    }

    public static void init(AttackEvent c, Ewoks ewoks) {
        List<Integer> serials = c.getAttack().getSerials();
        Collections.sort(serials); // prevent deadlock
        for(int serial: serials) { // acquire all resources
            ewoks.acquire(serial); // blocking if ewok not available
        }
        try { // all resources were acquired
            Thread.sleep(c.getAttack().getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int serial: serials) { // release all resources
            ewoks.release(serial);
        }
    }
}
