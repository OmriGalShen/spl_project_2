package bgu.spl.mics.application.services;



import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        this.subscribeEvent(BombDestroyerEvent.class, c -> {
            System.out.println("Lando: sending bombs!");
            try {
                Thread.sleep(this.duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Lando: The empire was destroyed!");
            System.out.println("Lando: time to return home");
            this.sendBroadcast(new TerminateBroadcast());
            Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
            System.out.println("Lando: I'm done here!");
            this.terminate();
        });
    }
}
