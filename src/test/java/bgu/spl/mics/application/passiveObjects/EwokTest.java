package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok testEwok;
    @BeforeEach
    void setUp() {
        testEwok = new Ewok(1);//should initialize with available=true
    }

    @Test
    void acquire() {
        assertTrue(testEwok.available);
        testEwok.acquire();
        assertFalse(testEwok.available);
    }

    @Test
    void release() {
        testEwok.available=false;
        testEwok.release();
        assertTrue(testEwok.available);
    }
}