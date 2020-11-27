package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private MessageBusImpl testMessageBus;

    @BeforeEach
    void setUp() {
        testMessageBus = MessageBusImpl.getInstance(); //Singleton Pattern
    }

    @Test
    void testSubscribeEvent() {
        TerminateBroadcast testBroadcast = new TerminateBroadcast();//example of Broadcast
    }

    @Test
    void testSubscribeBroadcast() {
        TerminateBroadcast testBroadcast = new TerminateBroadcast();//example of Broadcast
    }

    @Test
    void testComplete() {
    }

    @Test
    void testSendBroadcast() {
        TerminateBroadcast testBroadcast = new TerminateBroadcast();//example of Broadcast
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        R2D2Microservice testR2D2 = new R2D2Microservice(100);//example of Microservice
        testR2D2.subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
            @Override
            public void call(TerminateBroadcast c) {

            }
        });
        testC3PO.sendBroadcast(testBroadcast);
        try {
            Message message = testMessageBus.awaitMessage(testR2D2);
            assertTrue(message instanceof TerminateBroadcast);
            assertEquals(testBroadcast,message);
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException");
        }

    }

    @Test
    void testSendEvent() {
        AttackEvent testEvent = new AttackEvent(); //example of event
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        R2D2Microservice testR2D2 = new R2D2Microservice(100);//example of Microservice
        testR2D2.subscribeEvent(AttackEvent.class,new Callback<AttackEvent>() { //empty callback
            @Override
            public void call(AttackEvent c) {

            }
        });

        testC3PO.sendEvent(testEvent);
        try {
            Message message = testMessageBus.awaitMessage(testR2D2);
            assertTrue(message instanceof AttackEvent);
            assertEquals(testEvent,message);
        }
        catch (InterruptedException e){
            System.out.println("InterruptedException");
        }
    }

    @Test
    void testRegister() {
    }

    @Test
    void testAwaitMessage() {
    }
}