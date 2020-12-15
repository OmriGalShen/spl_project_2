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


    public boolean isAvailable(int num) {
        return (num >= ewoksList.size() - 1 || !ewoksList.get(num - 1).isAvailable());
    }

    public synchronized void acquire(int serialNumber) {
        if(serialNumber < ewoksList.size())
            ewoksList.get(serialNumber - 1).acquire();
    }

    public void release(int serialNumber) { // it doesn't need to be synchronized (the only way to get here is thru a synchronized method)
        if(serialNumber < ewoksList.size()) {
            ewoksList.get(serialNumber - 1).release();
        }
    }

    public void acquireEwoks(List <Integer> serialNumbers) { // it doesn't need to be synchronized (the acquire of an ewok is synchronized)
        for (Integer serial : serialNumbers) {
            while (!isAvailable(serial)) {
                try {
                    this.wait(); // blocking!!
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException on Ewoks acquire()");
                    e.printStackTrace();
                }
            } // while
            acquire(serial);
        } // for
    }

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
