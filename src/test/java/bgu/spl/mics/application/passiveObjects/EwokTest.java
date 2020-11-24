package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok testEwok;
    @BeforeEach
    void setUp() {
        testEwok = new Ewok(1);
    }

    @Test
    void acquire() {
        assertTrue(testEwok.available);
        testEwok.acquire();
        assertFalse(testEwok.available);
    }

    @Test
    void release() {
        assertTrue(testEwok.available);
        testEwok.acquire();
        assertFalse(testEwok.available);
        testEwok.release();
        assertTrue(testEwok.available);
    }
}