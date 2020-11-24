package bgu.spl.mics;

import bgu.spl.mics.application.services.*;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class MicroServiceTest {
    private C3POMicroservice c3poservice;
    private HanSoloMicroservice hansservice;
    private LandoMicroservice landoservice;
    private LeiaMicroservice leiaservice;
    private R2D2Microservice r2d2service;

    @BeforeEach
    void setUp() {
        c3poservice = new C3POMicroservice();
        hansservice = new HanSoloMicroservice();
        landoservice = new LandoMicroservice(100);
        r2d2service = new R2D2Microservice(100);
    }
}