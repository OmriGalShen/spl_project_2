package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private MessageBusImpl testMessageBus;

    @BeforeEach
    void setUp() {
        testMessageBus = MessageBusImpl.getInstance(); //Singleton Pattern
    }

    @Test
    void testComplete() {
        AttackEvent testEvent = new AttackEvent(new Attack(new ArrayList<>(),100)); //example of event
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        Future<Boolean> c3p0Future = testC3PO.sendEvent(testEvent);
        if(c3p0Future==null)fail();
        testMessageBus.complete(testEvent,true); //should resolve c3p0Future with the value true
        assertTrue(c3p0Future.isDone()); //check if complete resolved the future
        assertEquals(true,c3p0Future.get()); //check if the resolved passed the true value
    }

    /**
     * This test checks the methods:
     * awaitMessage, sendBroadcast, subscribeBroadcast and register
     */
    @Test
    void testBroadcast() {
        TerminateBroadcast testBroadcast = new TerminateBroadcast();//example of Broadcast
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        R2D2Microservice testR2D2 = new R2D2Microservice(100);//example of Microservice
        testMessageBus.register(testC3PO);
        testMessageBus.register(testR2D2);
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

    /**
     * This test checks the methods:
     * awaitMessage, sendEvent, subscribeEvent and register
     */
    @Test
    void testEvent() {
        AttackEvent testEvent = new AttackEvent(new Attack(new ArrayList<>(),100)); //example of event
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        R2D2Microservice testR2D2 = new R2D2Microservice(100);//example of Microservice
        testMessageBus.register(testC3PO);
        testMessageBus.register(testR2D2);
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
}