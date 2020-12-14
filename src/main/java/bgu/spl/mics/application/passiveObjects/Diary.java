package bgu.spl.mics.application.passiveObjects;

//import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.Main;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private static Diary instance=null;
    private AtomicInteger totalAttacks;
    private long HanSoloFinish, C3POFinish; // timestamp - finished all the attacks
    private long R2D2Deactivate; // timestamp - finished deactivating
    private long LeiaTerminate, HanSoloTerminate,C3POTerminate,R2D2Terminate, LandoTerminate; // timestamp - right before the termination

    private Diary(){
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate = 0;
        LeiaTerminate = 0;
        HanSoloTerminate = 0;
        C3POTerminate = 0;
        R2D2Terminate = 0;
        LandoTerminate = 0;
    }

    public static Diary getInstance() { // singleton pattern
        if(instance == null) {
            // only on creation of first instance synchronize:
            // to make sure that only one thread creates the first instance
            synchronized (Diary.class) {
                if(instance == null)
                    instance = new Diary();
            }
        }
        return instance;
    }

    public void incrementTotalAttacks() { totalAttacks.getAndIncrement(); }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish- Main.startTime;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate- Main.startTime;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) { R2D2Deactivate = r2D2Deactivate- Main.startTime; }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish- Main.startTime;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate- Main.startTime;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate- Main.startTime;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate- Main.startTime;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate- Main.startTime;
    }

/*
    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    public void setTotalAttacks(AtomicInteger totalAttacks) {
        this.totalAttacks = totalAttacks;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }
*/
}
