package Modele;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PassagerTest {

    private Cabine cabine;

    @Before
    public void setUp() {
        cabine = new Cabine(1);
    }


    @Test
    public void testSetAndGetPoche() {
        Passager passager = new Passager(1);
        Butin butin = Butin.BIJOUX;

        passager.setButin(butin);
        assertEquals(butin, passager.getPoche());
    }

    @Test
    public void testCede() {
        Passager passager = new Passager(1);
        Bandit bandit = new Bandit("Bandit", 1);
        Butin butin = Butin.BIJOUX;

        passager.setButin(butin);
        passager.cede(bandit);

        assertNull(passager.getPoche());
        assertTrue(bandit.getPoches().contains(butin));
    }

    @Test
    public void testDropButin() {
        Passager passager = cabine.liste_passagers().get(0);
        Butin butin = Butin.BIJOUX;
        passager.drop_butin(cabine);
        assertTrue(cabine.getLootInt().contains(butin));
        assertNull(passager.getPoche());
    }

    @Test
    public void testEstVise() {
        Passager passager = cabine.liste_passagers().get(0);
        passager.est_vise(cabine);
        assertFalse(cabine.getInterieur().contains(passager));
        assertNull(passager.getPoche());
    }



}
