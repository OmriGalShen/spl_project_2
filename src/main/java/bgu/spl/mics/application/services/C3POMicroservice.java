package bgu.spl.mics.application.services;
import java.util.Collections;
import java.util.List;

//import bgu.spl.mics.application.Main;
//import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private final Ewoks ewoks;
	
    public C3POMicroservice() {
        super("C3PO");
        this.ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        this.subscribeEvent(AttackEvent.class, c -> {

            System.out.println("C3PO: I got an attack to do.."); ///////////////////////////////////////////

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

            System.out.println("C3PO: I finished this attack!"); ///////////////////////////////////////////

            Diary.getInstance().setC3POFinish(System.currentTimeMillis());
            Diary.getInstance().incrementTotalAttacks();
            this.complete(c, true);
        });
        // -- subscribe to TerminateBroadcast and terminate accordingly --
        this.subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setC3POTerminate(System.currentTimeMillis());

            System.out.println("C3PO: I'm done here!"); ///////////////////////////////////////////

            this.terminate();
        });
    }
}
