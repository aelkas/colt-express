package Modele;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class Wagon {

    static protected int size = 0;    //compteur pour la construction


    protected int position;
    protected List<Personne> interieur;
    protected List<Butin> loot_int;  //loot droppé à l'interieur du wagon
    protected List<Bandit> toit;
    protected List<Butin> loot_toit; //loot droppé sur le toit du wagon


    Wagon(){
        loot_int = new LinkedList<>();
        loot_toit = new LinkedList<>();
        toit = new ArrayList<>();
        interieur = new ArrayList<>();
    }

    public String toString(){
        String inside = "";
        String top = " ";
        for (Personne personne : interieur) {
            inside += (personne.toString()+", ");
        }
        for (Bandit bandit : toit) {
            top += (bandit.toString() +", ");
        }

        return "C'est le Wagon no " + position +".\nil a :\n - "+ interieur.size() +
                " individus dedans : "+inside+"\n - "+ toit.size() +" bandits sur le toit : "+top+"\n";
    }

    public static void reinitialise(){
        size = 0;
    }

    public List<Passager> liste_passagers(){
        List<Passager> res = new LinkedList<>();
        for (Personne p : interieur){
            if(p instanceof Passager)
                res.add((Passager) p);
        }
        return res;
    }

    public List<Bandit> liste_bandits_int(){
        List<Bandit> res = new LinkedList<>();
        for (Personne p : interieur){
            if(p instanceof Bandit)
                res.add((Bandit) p);
        }
        return res;
    }



    public void enleve_personne(Personne p){
        interieur.remove(p);
        toit.remove((Bandit)p);
    }

    public void ajoute_personne(Personne p , Boolean roof){
        if(roof) {
            assert  p instanceof Bandit;
            toit.add((Bandit) p);}
        else interieur.add(p);
    }


    public void perte_loot_toit(){  //Un loot du toit disparait avec une certaine proba
        Random r = new Random();
        if(r.nextDouble() < Partie.PROBA_PERTE_LOOT_TOIT && !loot_toit.isEmpty()){
            int rand_index = r.nextInt(loot_toit.size());
            loot_toit.remove(rand_index);
        }
    }

    public List<Bandit> getToit() {return toit;}
    public List<Personne> getInterieur() {return interieur;}
    public List<Butin> getLootInt() {return loot_int;}
    public  List<Butin> getLootToit(){return loot_toit;}

}

class Cabine extends Wagon{

    public Cabine(int p){
        super();
        assert size > 0;
        position = size;
        size++;
        Random r = new Random();
        for (int i = 0; i < r.nextInt(Partie.NB_PASSAGER_PAR_WAGON_MAX-1)+1; i++) {
            interieur.add(new Passager(p));
        }
    }
}