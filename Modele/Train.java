package Modele;


import java.util.ArrayList;
import java.util.List;

public class Train extends Observable {

    private final Wagon[] WAGON = new Wagon[Partie.NB_WAGON];
    private final Marchall marchall;

    public Train(){
        WAGON[0] = new Locomotive();
        for (int i = 1; i < Partie.NB_WAGON ; i++) {
            WAGON[i] = new Cabine(i);
        }
        marchall = (Marchall)WAGON[0].getInterieur().get(0); //set le marshall
    }

    public Wagon[] get_Wagon(){
        return WAGON;
    }

    @Override
    public String toString(){
        String acc = "Les wagons du trains : \n";
        for (int i = 0; i < Partie.NB_WAGON ; i++) {
            acc += "----------- \n";
            acc += WAGON[i].toString();
        }
        acc += " END. \n";
        return acc;
    }


    public Marchall getMarchall() {
        return marchall;
    }


    public List<Direction> mouvementPossibles(boolean bandit, int position , boolean toit){
        List<Direction> res = new ArrayList<>();
        if(position==0)
            res.add(Direction.ARRIERE);
        else if(position== Partie.NB_WAGON-1)
            res.add(Direction.AVANT);
        else {
            res.add(Direction.AVANT);
            res.add(Direction.ARRIERE);
        }
        if(bandit) {
            if (toit)
                res.add(Direction.BAS);
            else
                res.add(Direction.HAUT);
        }
        return res;
    }


}


