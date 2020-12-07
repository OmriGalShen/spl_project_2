package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
        this.subscribeEvent(DeactivationEvent.class, c -> {
            System.out.println("R2D2: Deactivating the shields!");
            try {
                Thread.sleep(this.duration);
                System.out.println("R2D2: requesting Bomb Destroyer! ");
                this.sendEvent(new BombDestroyerEvent());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
        });
        // -- subscribe to TerminateBroadcast and terminate accordingly --
        this.subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
            this.terminate();
        });
        //------------------------------------------------------------------
    }
}
