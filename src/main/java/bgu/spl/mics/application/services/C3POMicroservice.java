package bgu.spl.mics.application.services;
import java.util.List;

import bgu.spl.mics.MessageBusImpl;
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
            List<Integer> serials = c.getAttack().getSerials();
            for(int serial: serials ){
                ewoks.acquire(serial);
                ewoks.release(serial);
            }
            try {
                Thread.sleep(c.getAttack().getDuration());
                Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
                MessageBusImpl.getInstance().complete(c,true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // -- subscribe to TerminateBroadcast and terminate accordingly --
        this.subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setC3POTerminate(System.currentTimeMillis());
            this.terminate();
        });
        //------------------------------------------------------------------
    }
}
