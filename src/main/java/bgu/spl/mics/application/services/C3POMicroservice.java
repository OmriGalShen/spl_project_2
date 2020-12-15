package bgu.spl.mics.application.services;

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

            Ewoks.initHanSoloAndC3P0(c, ewoks); // a method that sets the Attacks and Ewoks for the micro service //////////////////////

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
