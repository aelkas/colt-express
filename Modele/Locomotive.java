package Modele;


public class Locomotive extends Wagon {
    private final Butin mag = Butin.MAGOT; // Butin dans la locomotive
    private boolean dispo = true; // Disponibilité du magot

    /**
     * Retourne une représentation textuelle de la locomotive.
     * @return string concernant si le magot est dispo .
     */
    @Override
    public String toString() {
        if (dispo) {
            return "C'est la locomotive.\n"+ "elle contient le magot!\n"+ super.toString();
        }
        return "C'est la locomotive.\n"+ "elle ne contient pas le magot!\n"+ super.toString();
    }

    /**
     * Vérifie si le magot est disponible dans la locomotive.
     * @return True si le magot est disponible, sinon False.
     */
    public boolean magot_dispo(){return dispo;}

    /**
     * Constructeur de la locomotive.
     */
    public Locomotive(){
        super();
        assert size==0;
        position = 0;
        size++;
        interieur.add(new Marchall());
    }

    /**
     * Vole le magot de la locomotive.
     * @param b Bandit qui vole le magot.
     */
    public void magot_vole(Bandit b)
    {
        if(magot_dispo()) {
            b.ajoute_butin(mag);
            dispo = false;
        }
    }

    /**
     *
     * @return Le magot contenu dans la locomotive.
     */
    public Butin getMag() {
        return mag;
    }
}
