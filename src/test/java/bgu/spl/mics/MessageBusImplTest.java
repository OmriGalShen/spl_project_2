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
        AttackEvent testEvent = new AttackEvent(); //example of event
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        R2D2Microservice testR2D2 = new R2D2Microservice(100);//example of Microservice
        testMessageBus.register(testC3PO);
        testMessageBus.register(testR2D2);
        testR2D2.subscribeEvent(AttackEvent.class,new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent c) {
                //empty callback
            }
        });
        Future<Boolean> c3p0Future = testC3PO.sendEvent(testEvent);
        if(c3p0Future==null)fail("Returned Future was null");
        else {
            testMessageBus.complete(testEvent, true); //should resolve c3p0Future with the value true
            assertTrue(c3p0Future.isDone()); //check if complete resolved the future
            assertEquals(true, c3p0Future.get()); //check if the resolved passed the value
        }
        /* unregister methods here were added after unit tests submissions to
           to prevents problem with singleton MessageBusImp
         */
        testMessageBus.unregister(testC3PO);
        testMessageBus.unregister(testR2D2);
    }

    /**
     * This test checks the methods:
     * sendBroadcast, subscribeBroadcast and register
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
                //empty callback
            }
        });
        testC3PO.sendBroadcast(testBroadcast);
        try {
            Message message = testMessageBus.awaitMessage(testR2D2);
            assertEquals(testBroadcast,message);
        }
        catch (Exception e){
            fail("awaitMessage thrown Exception");
        }
        /* unregister methods here were added after unit tests submissions to
           to prevents problem with singleton MessageBusImp
         */
        testMessageBus.unregister(testC3PO);
        testMessageBus.unregister(testR2D2);
    }

    /**
     * This test checks the methods:
     * sendEvent, subscribeEvent and register
     */
    @Test
    void testEvent() {
        AttackEvent testEvent = new AttackEvent(); //example of event
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        R2D2Microservice testR2D2 = new R2D2Microservice(100);//example of Microservice
        testMessageBus.register(testC3PO);
        testMessageBus.register(testR2D2);
        testR2D2.subscribeEvent(AttackEvent.class,new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent c) {
                //empty callback
            }
        });

        testC3PO.sendEvent(testEvent);
        try {
            Message message = testMessageBus.awaitMessage(testR2D2);
            assertEquals(testEvent,message);
        }
        catch (Exception e){
            fail("awaitMessage thrown Exception");
        }
        /* unregister methods here were added after unit tests submissions to
           to prevents problem with singleton MessageBusImp
         */
        testMessageBus.unregister(testC3PO);
        testMessageBus.unregister(testR2D2);
    }


    @Test
    void testAwaitMessage() {
        AttackEvent testEvent = new AttackEvent(); //example of event
        C3POMicroservice testC3PO = new C3POMicroservice(); //example of Microservice
        R2D2Microservice testR2D2 = new R2D2Microservice(100);//example of Microservice
        try{
            testMessageBus.awaitMessage(testC3PO);
            fail("awaitMessage should throw IllegalStateException " +
                    "for unregistered microservice");
        }
        catch (Exception e){
            assertTrue(e instanceof IllegalStateException);
        }
        //example of a receiving Microservice
        testMessageBus.register(testC3PO);
        testMessageBus.register(testR2D2);
        testR2D2.subscribeEvent(AttackEvent.class,new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent c) {
                //empty callback
            }
        });
        testC3PO.sendEvent(testEvent);
        try {
            Message message = testMessageBus.awaitMessage(testR2D2);
            if(message == null) fail("Message was null");
            else{
                assertTrue(message instanceof AttackEvent);
                assertEquals(testEvent,message);
            }
        }
        catch (InterruptedException e){
            fail("awaitMessage thrown InterruptedException");
        }
        catch (IllegalStateException e){
            fail("awaitMessage thrown IllegalStateException");
        }
        /* unregister methods here were added after unit tests submissions to
           to prevents problem with singleton MessageBusImp
         */
        testMessageBus.unregister(testC3PO);
        testMessageBus.unregister(testR2D2);
    }
}