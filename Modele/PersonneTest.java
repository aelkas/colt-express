package Modele;
import org.junit.Assert;
import org.junit.Test;
import java.util.LinkedList;
import static org.junit.Assert.*;

public class PersonneTest  {


    @Test

    public void createidwork(){

        Bandit Rayan = new Bandit("Rayan",0);
        assertEquals(0 , Rayan.get_id());

        Bandit Antoine = new Bandit("Antoine",0);

        assertEquals(1 , Antoine.get_id());

        Bandit Samy = new Bandit("Samy",0);

        assertEquals(2 , Samy.get_id());

        Personne.reinitialise();
    }


    @Test

    public void ajoute_butin_test(){
        Bandit Rayan = new Bandit("Rayan",0);

        Butin b = Butin.BIJOUX ;

        Rayan.ajoute_butin(b);

        assertTrue(Rayan.getPoches().contains(b));
        Personne.reinitialise();
    }

    @Test
    public void enleve_butin_test(){
        Wagon.reinitialise();
        Bandit Rayan = new Bandit("Rayan",0);
        Wagon wag_test = new Locomotive();
        Butin b = Butin.BIJOUX ;
        Butin c = Butin.MAGOT;
        Rayan.ajoute_butin(b);
        Rayan.drop_butin(wag_test);

        Assert.assertFalse(Rayan.getPoches().contains(b));
        Assert.assertTrue(wag_test.loot_toit.contains(b));

        wag_test.loot_toit.remove(b);
        Rayan.ajoute_butin(b);
        Rayan.ajoute_butin(c);

        assertEquals(2,Rayan.getPoches().size());

        wag_test.enleve_personne(Rayan);
        wag_test.ajoute_personne(Rayan,false);
        Rayan.setToit(false);
        Rayan.drop_butin(wag_test);

        assertFalse(Rayan.getPoches().contains(wag_test.loot_int.get(0)));
        assertEquals(1,wag_test.loot_int.size());
        assertEquals(1, Rayan.getPoches().size());

        Rayan.drop_butin(wag_test);

        assertEquals(0,Rayan.getPoches().size());
        assertEquals(2,wag_test.loot_int.size());
        assertEquals(0, wag_test.loot_toit.size());
        Wagon.reinitialise();
        Personne.reinitialise();
    }

    @Test
    public void test_braque(){
        Butin b = Butin.BIJOUX ;
        Butin c = Butin.MAGOT;
        Train train = new Train();
        Bandit bandit= new Bandit("Natasha",1);
        Bandit bandit2 = new Bandit("Paul",1);
        bandit.setToit( false);
        bandit2.setToit(false);
        train.get_Wagon()[1].interieur = new LinkedList<>();
        train.get_Wagon()[bandit2.position].interieur.add(bandit2);
        train.get_Wagon()[bandit.position].interieur.add(bandit);
        Passager test_passager = new Passager(1);
        train.get_Wagon()[1].interieur.add(test_passager);
        test_passager.setButin(b);
        bandit.getPoches().add(c);
        LinkedList<Butin> total = new LinkedList<>(bandit.getPoches());
        total.add(test_passager.getPoche());
        //total.addAll(bandit.getPoches());
        total.addAll(bandit2.getPoches());


        assertEquals(1, bandit.getPoches().size());

        bandit.braque(train);
        assertEquals(2, bandit.getPoches().size());

        LinkedList<Butin> total2 = new LinkedList<>(bandit.getPoches());
        total2.addAll(bandit2.getPoches());
        total2.add(test_passager.getPoche());
        total2.remove(null);
        //on remove null car:
        // quand bandit braque quelquun
        // la liste "total2" pointe vers des objet, donc elle va pointer vers
        // un objet qui n'existe plus donc null.
        assertEquals(total,total2);
        Wagon.reinitialise();
        Personne.reinitialise();
    }
    @Test
    public void test_move(){
        Train train = new Train();
        Bandit bandit= new Bandit("Natasha",0);
        train.get_Wagon()[bandit.position].interieur.add(bandit);
        bandit.move(train,Direction.AVANT);

        assertEquals(0,bandit.position);

        bandit.move(train,Direction.ARRIERE);

        assertEquals(1,bandit.position);
        assertTrue(train.get_Wagon()[bandit.position].toit.contains(bandit));
        assertFalse(train.get_Wagon()[bandit.position-1].toit.contains(bandit));

        bandit.move(train, Direction.BAS);

        assertFalse(train.get_Wagon()[bandit.position].toit.contains(bandit));
        assertTrue(train.get_Wagon()[bandit.position].interieur.contains(bandit));

        bandit.move(train, Direction.HAUT);

        assertTrue(train.get_Wagon()[bandit.position].toit.contains(bandit));
        assertFalse(train.get_Wagon()[bandit.position].interieur.contains(bandit));

        bandit.move(train, Direction.BAS);

        assertFalse(train.get_Wagon()[bandit.position].toit.contains(bandit));
        assertTrue(train.get_Wagon()[bandit.position].interieur.contains(bandit));

        Marchall marshall_mathers = new Marchall();
        train.get_Wagon()[marshall_mathers.position].interieur.add(marshall_mathers);
        marshall_mathers.move(train,Direction.HAUT);

        assertTrue(train.get_Wagon()[marshall_mathers.position].interieur.contains(marshall_mathers));

        marshall_mathers.move(train,Direction.ARRIERE);
        assertTrue(train.get_Wagon()[marshall_mathers.position].interieur.contains(marshall_mathers));
        assertFalse(train.get_Wagon()[marshall_mathers.position-1].interieur.contains(marshall_mathers));
        Wagon.reinitialise();
        Personne.reinitialise();
    }

    @Test
    public void test_tir(){
        Train train = new Train();
        Bandit bandit= new Bandit("Natasha",0);
        Bandit bandit2 = new Bandit("Paul",2);
        train.get_Wagon()[bandit.position].toit.add(bandit);
        train.get_Wagon()[bandit2.position].toit.add(bandit2);
        bandit2.ajoute_butin(Butin.BIJOUX);
        bandit.tir(train,Direction.ARRIERE);


        assertEquals(bandit2.get_ammo()-1,bandit.get_ammo());
        assertEquals(bandit.get_hitPoints()-1,bandit2.get_hitPoints());
        assertFalse(train.get_Wagon()[bandit2.position].loot_toit.isEmpty());

        try {
            // This code may throw an assertion error
            bandit.tir(train,Direction.AVANT);
        } catch (Throwable t) {
            // If an assertion error is thrown, the test passes
            assertTrue(t instanceof AssertionError);
            return;
        }
        // If no assertion error is thrown, fail the test
        fail("Expected an AssertionError, but no exception was thrown.");

        assertEquals(bandit2.get_ammo()-1,bandit.get_ammo());
        assertEquals(bandit.get_hitPoints()-1,bandit2.get_hitPoints());

        bandit2.move(train,Direction.BAS);
        bandit2.move(train,Direction.AVANT);
        bandit2.move(train,Direction.AVANT);
        bandit2.tir(train,Direction.HAUT);

        assertEquals(bandit2.get_ammo(),bandit.get_ammo());
        assertEquals(bandit.get_hitPoints(),bandit2.get_hitPoints());

        bandit.move(train,Direction.BAS);
        bandit.move(train,Direction.ARRIERE);
        bandit2.tir(train,Direction.ARRIERE);

        assertEquals(bandit2.get_ammo(),bandit.get_ammo()-1);
        assertEquals(bandit.get_hitPoints(),bandit2.get_hitPoints()-1);

        bandit.move(train,Direction.ARRIERE);
        bandit.tir(train,Direction.AVANT);

        assertEquals(bandit2.get_ammo(),bandit.get_ammo());
        assertEquals(bandit.get_hitPoints(),bandit2.get_hitPoints()-1);

        Wagon.reinitialise();
        Personne.reinitialise();
    }

    @Test
    public void test_frappe(){
        Wagon.reinitialise();
        Butin b = Butin.BIJOUX ;
        Train train = new Train();
        Bandit bandit= new Bandit("Natasha",0);
        Bandit bandit2 = new Bandit("Paul",0);
        train.get_Wagon()[bandit.position].ajoute_personne(bandit,true);
        train.get_Wagon()[bandit2.position].ajoute_personne(bandit2,true);
        bandit.frappe(train);

        assertEquals(bandit2.get_ammo(),bandit.get_ammo());
        assertEquals(bandit.get_hitPoints(),bandit2.get_hitPoints()+1);

        bandit.ajoute_butin(b);
        bandit2.frappe(train);

        assertEquals(bandit.get_hitPoints(),bandit2.get_hitPoints());
        assertFalse(train.get_Wagon()[0].loot_toit.isEmpty());
        Wagon.reinitialise();
        Personne.reinitialise();
    }

    @Test
    public void test_move_marchall(){
        Wagon.reinitialise();
        Train train = new Train();
        Marchall marshall = new Marchall();
        train.get_Wagon()[0].ajoute_personne(marshall,false);
        marshall.move(train, Direction.AVANT);

        assertEquals(0,marshall.position);
        assertTrue(train.get_Wagon()[marshall.position].interieur.contains(marshall));

        marshall.move(train,Direction.ARRIERE);

        assertTrue(train.get_Wagon()[marshall.position].interieur.contains(marshall));
        assertFalse(train.get_Wagon()[marshall.position-1].interieur.contains(marshall));
        assertEquals(1,marshall.position);

        marshall.move(train,Direction.HAUT);

        assertEquals(1,marshall.position);

        Wagon.reinitialise();
        Personne.reinitialise();
    }
}
