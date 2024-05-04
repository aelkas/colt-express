package Modele;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class Bandit_Bot extends Bandit{

    public static Bandit_Bot create_Bandit_Bot(Partie p ,int position){
        Random r = new Random();
        switch (r.nextInt(3)){
            case 0 : return new Random_Bot(position,p);
            case 1: return new Blood_thirsty_Bot(position,p);
            case 2: return new Goblin_Bot(position,p);
        }
        return null;
    }



    //valeur absolue de la soustraction entre a et b
    public int abs_substraction(int a, int b) {return (a>b? a-b : b-a);}

    protected Partie partie;
    public Bandit_Bot(String name, int pos, Partie partie) {
        super(name, pos);
        this.partie = partie;
    }
    abstract void target(Train train, LinkedList<Action> acts, Bandit src);
    abstract List<Action> actions_bot();
    //distance entre deux bandit en prenant compte des toits
    public int dist(Bandit src, Bandit tgt){
       //pour verifier si on peut arriver avant d'utiliser toutes nos actions
        int dist =((src.getToit()?1:0)-(tgt.getToit()?1:0));
        dist = (dist>0 ?dist :-dist);
        int pos = src.position - tgt.position;
        dist += (pos>0? pos:-pos);
        return dist;
    }

    //get la position ou le nombre du wagon dans lequel le butin est le plus proche
    public int butinproche_position(){
        int min = Integer.MAX_VALUE;
        int cpt = 0;
        Wagon[] wagon = this.partie.getTrain().get_Wagon();
        for (int i = 0; i < Partie.NB_WAGON; i++) {
            if(!(wagon[i].liste_passagers().isEmpty()) || !(wagon[i].loot_int.isEmpty())
                    || !(wagon[i].loot_toit.isEmpty())){
                int dist = this.position - i;
                dist = ( dist>=0 ? dist : -dist );
                if(dist<min){
                    cpt = i;
                    min = dist;
                }
            }
        }
        return cpt;
    }

    //donne la direction dans laquelle on doit y aller pour partir de src a tgt
    public Direction get_direction(int position, int position2, boolean toit, boolean toit2){
        Direction dir = Direction.ICI;
        if(toit2){
            if(toit) dir = (position<position2? Direction.ARRIERE :Direction.AVANT);
            else dir = Direction.HAUT;
        }
        else{
            if(toit) dir = Direction.BAS;
            else dir = (position<position2? Direction.ARRIERE :Direction.AVANT);
        }
        return dir;
    }
}
