package bgu.spl.mics.application.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private final Ewoks ewoks;
    public HanSoloMicroservice(){
        super("Han");
        this.ewoks = Ewoks.getInstance();
    }


    @Override
    protected void initialize() {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        this.subscribeEvent(AttackEvent.class, new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent c) {
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
            }
        });
        this.subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
            @Override
            public void call(TerminateBroadcast c) {
                // TODO : call this object .terminate()
            }
        });
    }
}
