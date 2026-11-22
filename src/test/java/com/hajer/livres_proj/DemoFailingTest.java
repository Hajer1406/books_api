package com.hajer.livres_proj;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test volontairement en échec pour démontrer la protection de branche :
 * Une Merge Request rouge NE PEUT PAS être fusionnée dans main.
 */
public class DemoFailingTest {

    @Test
    void testVolontairementEnEchecPourDemonstrationProtection() {
        fail("Échec volontaire : Démonstration que la branche main est protégée contre toute fusion de MR rouge.");
    }
}
