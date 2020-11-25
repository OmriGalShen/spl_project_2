package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private MessageBusImpl testMessage;
    private Broadcast testBroadcast;
    private MicroService testMicroService;


    @BeforeEach
    void setUp() {
        testMessage = new MessageBusImpl();
    }

    @Test
    void testSubscribeEvent() {
    }

    @Test
    void testSubscribeBroadcast() {
    }

    @Test
    void testComplete() {
    }

    @Test
    void testSendBroadcast() {
    }

    @Test
    void testSendEvent() {
    }

    @Test
    void testRegister() {
    }

    @Test
    void testAwaitMessage() {
    }
}