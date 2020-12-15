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
    private static final Ewoks instance = null; // may be final? - Eden //////////////////////////////////////////////
    private final ArrayList<Ewok> ewoksList; // may be final? - Eden //////////////////////////////////////////////
    private final Boolean lock;

    private static class EwoksHolder { // singleton pattern
        private static Ewoks instance = new Ewoks(0, true);
    }

    private Ewoks(int size, Boolean lock) { // constructor according to the size
        this.ewoksList = new ArrayList<>();
        for (int i=0; i < size; i++) {
            this.ewoksList.add(new Ewok(i));
        }
        this.lock = lock;

    }

    public static Ewoks getInstance() { // singleton pattern
        return EwoksHolder.instance;
    }




    public void acquireEwoks(List <Integer> serial) {
        int currEwok;
        for (Integer integer : serial) {
            currEwok = integer;
            while (!isAvailable(currEwok)) {
                synchronized (lock) {
                    try {
                        wait(); // blocking!!
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException on Ewoks acquire()");
                        e.printStackTrace();
                    } //catch
                } // synchronized
            } // while
            acquire(currEwok);
        } // while
    } // while

    public boolean isAvailable(int num) {
        Ewok ewok = ewoksList.get(num-1);
        if (ewok != null)
            return ewok.isAvailable();
        return false;
    }

    public void acquire(int serialNumber) {
        if(serialNumber < ewoksList.size()) {
            Ewok ewok = ewoksList.get(serialNumber - 1);
            ewok.acquire();
        }
    }

    public void release(int serialNumber) {
        if(serialNumber < ewoksList.size()) {
            Ewok ewok = ewoksList.get(serialNumber - 1);
            ewok.release();
            synchronized (lock) {
                notifyAll(); // give waiting thread opportunity to catch the released Ewok
            }
        }
    }

    public static void initHanSoloAndC3P0(AttackEvent c, Ewoks ewoks) { // to spare code duplications
        List<Integer> serialNumbers = c.getAttack().getSerials();
        for(int serial: serialNumbers) { // acquire all resources
            ewoks.acquire(serial); // blocking if ewok not available
        }
        try { // all resources were acquired
            Thread.sleep(c.getAttack().getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int serial: serialNumbers) { // release all resources
            ewoks.release(serial);
        }
    }
}
