package Modele;

import java.util.List;
import java.util.Random;

public class Marchall extends Personne implements Movable{
    double nevrosite;


    /**
     * Constructeur de Marshall.
     */

    public Marchall(){
        super("Marshall");
        id = -1;
        position = 0;
        nevrosite = Partie.NEVROSITE_MARSHALL;
        sprite = "../Vue/Images/marshall.png";
    }


    /**
     * Retourne la liste des mouvements possibles du Marshal dans le train spécifié.
     * @param t Train dans lequel le Marshal se déplace.
     * @return Liste des directions de mouvement possibles.
     */

    @Override
    public List<Direction> mouvementsPossibles(Train t) {
        return t.mouvementPossibles(false,position,false);
    }


    /**
     * Déplace le Marshal dans la direction spécifiée.
     * @param T Train dans lequel le Marshal se déplace.
     * @param d Direction dans laquelle le Marshal se déplace.
     * @return Message indiquant le résultat du déplacement.
     */
    @Override
    public String move(Train T , Direction d){
        Random r = new Random();
        if(!this.mouvementsPossibles(T).contains(d)){
            return "Le Marshall a perdu le sens de l'orientation";
        }
        if(r.nextDouble() <  nevrosite) {
            T.get_Wagon()[position].interieur.remove(this);
            position += d.dir();
            T.get_Wagon()[position].interieur.add(this);
            List<Bandit> attrape = T.get_Wagon()[position].liste_bandits_int();
            for (Bandit b : attrape) {
                b.fuit_marshall(T.get_Wagon()[position]);
            }
            return "Le Marshall se déplace, prenez gardes.";
        }
        return "Le Marshall planifie sa riposte.";


    }

    /**
     * Indique si le Marshall peut être la cible d'un tir.
     * @return False, car le Marshall ne peut être la cible d'un tir par defaut .
     */
    @Override
    public boolean isTargeted(){return false;}

    /**
     * Définit si le Marshall peut être la cible d'un tir.
     * @param b Boolean indiquant si le Marshall peut être la cible d'un tir.
     */
    @Override
    public void setTargeted(boolean b){}
}

