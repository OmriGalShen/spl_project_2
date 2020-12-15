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
    private static final Ewoks instance = null; // may be final? - Eden //////////////////////////////////////////////
    private final ArrayList<Ewok> ewoksList; // may be final? - Eden //////////////////////////////////////////////

    private static class EwoksHolder { // singleton pattern
        private static Ewoks instance = new Ewoks(0);
    }

    private Ewoks(int size) { // constructor according to the size
        this.ewoksList = new ArrayList<>();
        for (int i=0; i < size; i++) {
            this.ewoksList.add(new Ewok(i));
        }
    }

    public static Ewoks getInstance() { // singleton pattern
        return EwoksHolder.instance;
    }



    public void acquire(int serialNumber) {
        if (serialNumber < ewoksList.size()) {
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
    }

    public void release(int serialNumber) {
        if (serialNumber < ewoksList.size()) {
            Ewok ewok = ewoksList.get(serialNumber - 1);
            ewok.release();
            synchronized (this) {
                notifyAll(); // give waiting thread opportunity to catch the released Ewok
            }
        }
    }

    public static void initHanSoloAndC3P0(AttackEvent c, Ewoks ewoks) { // to spare code duplications
        List<Integer> serials = c.getAttack().getSerialNumbers();
        //Collections.sort(serials); // prevent deadlock - Omri   is it still needed? - Eden //////////////////////////////////////
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
