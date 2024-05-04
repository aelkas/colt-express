package Modele;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTest {

    @Test
    public void testDirectionValues() {
        assertEquals("Avant", Direction.AVANT.toString());
        assertEquals("Arrière", Direction.ARRIERE.toString());
        assertEquals("Haut", Direction.HAUT.toString());
        assertEquals("Bas", Direction.BAS.toString());
        assertEquals("Ici", Direction.ICI.toString());
    }

    @Test
    public void testDirectionDirs() {
        assertEquals(-1, Direction.AVANT.dir());
        assertEquals(1, Direction.ARRIERE.dir());
        assertEquals(-Partie.NB_WAGON, Direction.HAUT.dir());
        assertEquals(Partie.NB_WAGON, Direction.BAS.dir());
        assertEquals(0, Direction.ICI.dir());
    }
}
