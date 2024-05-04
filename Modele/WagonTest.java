package Modele;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.*;

public class WagonTest {

    private Cabine cabine;
    @Before
    public void setUp() {
        Wagon.reinitialise();
        new Locomotive();
        cabine = new Cabine(0);
    }

    @Test
    public void testListePassagers() {
        List<Passager> passagers = cabine.liste_passagers();
        assertFalse(passagers.isEmpty());
        for (Personne personne : passagers) {
            assertTrue(personne instanceof Passager);
        }
    }

    @Test
    public void testListeBanditsInt() {
        List<Bandit> bandits = cabine.liste_bandits_int();
        assertTrue(bandits.isEmpty());
    }

    @Test
    public void testEnlevePersonne() {
        Bandit bandit = new Bandit("Bandit", 1);
        cabine.ajoute_personne(bandit, false);
        assertTrue(cabine.getInterieur().contains(bandit));

        cabine.enleve_personne(bandit);
        assertFalse(cabine.getInterieur().contains(bandit));
    }

    @Test
    public void testAjoutePersonne() {
        Bandit bandit = new Bandit("Bandit", 1);
        cabine.ajoute_personne(bandit, true);
        assertTrue(cabine.getToit().contains(bandit));
    }
}
