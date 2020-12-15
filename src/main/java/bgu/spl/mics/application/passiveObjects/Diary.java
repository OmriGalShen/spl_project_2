package bgu.spl.mics.application.passiveObjects;

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


    private static class DiaryHolder { // singleton pattern
        private static Diary instance = new Diary();
    }

    private Diary() { // singleton pattern
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
        return DiaryHolder.instance;
    }
    
    public void incrementTotalAttacks() { totalAttacks.getAndIncrement(); }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) { R2D2Deactivate = r2D2Deactivate; }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }
}
