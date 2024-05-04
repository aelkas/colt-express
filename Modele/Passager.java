package Modele;

import java.util.Random;


public class Passager extends Personne implements Hitable {

    // Sprites possibles pour un passager
    private static final String[] sprites = {"../Vue/Images/passagerBarbu.png" , "../Vue/Images/passagerFemme.png" , "../Vue/Images/passagerJournal.png",
            "../Vue/Images/passagerRiche.png" , "../Vue/Images/passagerVieux.png"};

    private Butin poche; // Butin que le passager porte

    /**
     * Constructeur de Passager.
     * @param p Position initiale du passager.
     */
    public Passager(int p){
        super("passager"+getCurrent_id_passager--);
        Random r = new Random();
        id = getCurrent_id_passager+1;
        position = p;
        poche =  Butin.values()[r.nextInt(2)];
        sprite = sprites[r.nextInt(sprites.length)];
    }

    /**
     * Définit le butin porté par le passager.
     * @param butin Butin à attribuer au passager.
     */
    public void setButin(Butin butin){
        poche = butin;
    }

    /**
     * Récupère le butin porté par le passager.
     * @return Le butin porté par le passager.
     */
    public Butin getPoche(){
        return poche;
    }

    /**
     * Cède le butin du passager au bandit b.
     * @param b Bandit qui braque le passager.
     */
    public void cede(Bandit b){
        if(poche != null) {
            b.ajoute_butin(poche);
            poche = null;
        }
    }

    /**
     * Lâche le butin dans le wagon spécifié.
     * @param w Wagon où le butin est lâché.
     */
    @Override
    public void drop_butin(Wagon w) {
        if(poche != null){
            Butin dropped_loot = poche;
            w.loot_int.add(dropped_loot);
            poche = null;
        }
    }

    /**
     * Traité lorsqu'un passager est visé par une action.
     * @param wagon Wagon où le passager est visé.
     */
    @Override
    public void est_vise(Wagon wagon){
        drop_butin(wagon);
        targeted = false;
        wagon.interieur.remove(this); // Passager est cliniquement mort
    }
}
