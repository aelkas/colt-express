package Modele;

import org.junit.Test;
import static org.junit.Assert.*;

public class ButinTest {

    @Test
    public void testBourse() {
        Butin bourse = Butin.BOURSE;
        assertEquals("Bourse", bourse.toString());
        assertTrue(bourse.valeur() >= 0 && bourse.valeur() < 500);
        assertNotNull(bourse.getSprite());
        assertTrue(bourse.getSpriteH() > 0);
        assertTrue(bourse.getSpriteW() > 0);
    }

    @Test
    public void testBijoux() {
        Butin bijoux = Butin.BIJOUX;
        assertEquals("Bijoux", bijoux.toString());
        assertEquals(500, bijoux.valeur());
        assertNotNull(bijoux.getSprite());
        assertTrue(bijoux.getSpriteH() > 0);
        assertTrue(bijoux.getSpriteW() > 0);
    }

    @Test
    public void testMagot() {
        Butin magot = Butin.MAGOT;
        assertEquals("Magot", magot.toString());
        assertEquals(1000, magot.valeur());
        assertNotNull(magot.getSprite());
        assertTrue(magot.getSpriteH() > 0);
        assertTrue(magot.getSpriteW() > 0);
    }
}
