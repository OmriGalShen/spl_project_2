package bgu.spl.mics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testResolve(){
        assertFalse(future.isDone());
        String resolveStr = "result";
        future.resolve(resolveStr);
        assertTrue(future.isDone());
        assertEquals(resolveStr,future.get());
    }

    @Test
    void testSimpleGet() {
        assertFalse(future.isDone());
        String resolveStr = "result";
        future.resolve(resolveStr);
        assertEquals(resolveStr,future.get());
    }

    @Test
    void testIsDone() {
        assertFalse(future.isDone());
        String resolveStr = "someResult";
        future.resolve(resolveStr);
        assertTrue(future.isDone());
    }

    @Test
    void testTimedGet() {
        assertFalse(future.isDone());
        assertNull(future.get(10,TimeUnit.MICROSECONDS));
        assertFalse(future.isDone());
        String resolveStr = "result";
        future.resolve(resolveStr);
        assertEquals(resolveStr,future.get(10,TimeUnit.SECONDS));
    }
}
