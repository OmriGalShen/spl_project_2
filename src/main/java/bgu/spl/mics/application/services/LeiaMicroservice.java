package bgu.spl.mics.application.services;
import java.util.ArrayList;
import java.util.List;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Future[] futures;
	private AttackEvent[] attackEvents;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.futures = new Future[attacks.length];
        for (int i = 0; i <attacks.length ; i++) {
            attackEvents[i] = new AttackEvent(attacks[i]);
        }
    }

    @Override
    protected void initialize() {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        for (int i = 0; i < attackEvents.length; i++) {
            futures[i] = messageBus.sendEvent(attackEvents[i]);
        }
    }
}
