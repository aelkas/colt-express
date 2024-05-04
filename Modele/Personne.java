package Modele;


import java.util.List;


/**
 * Interface Movable: Implémenté par {@code Bandit , Marchall}<br>
 * Utilisé pour implémenter l'action {@code seDeplacer}
 */
interface Movable {
    /** Renvoie une liste contenant tous les mouvement possibles pour l'appelant ({@code Bandit or Marchall}) depuis sa position actuelle
     * @param t référence vers le train contenant l'appelant
     * @return une liste de directions valides pour un déplacement
     */
    List<Direction> mouvementsPossibles(Train t);

    /** Deplace l'appelant en suivant la direction d et renvoie un message de description
     * @param T référence vers le train contenant l'appelant
     * @param d la direciton du déplacement
     *
     * @return un string indiquant si le déplacement a été effectué ou pas <br>
     * {@code if d not in mouvementPossibles(T)} renvoie un message d'erreur
     */
    String move(Train T, Direction d);
}


/**
 * Interface Hitable: Implémenté par {@code Bandit , Passager}<br>
 * Utilisé pour implémenté le drop de butin causé par une attaque ({@code Tir ou Frappe})
 */
interface Hitable{
    /**Retire un butin aléatoire de l'appelant et le drop au sol  à sa position
     * @param w le wagon contenant l'appelant
     */
    void drop_butin(Wagon w);

    /**Modifie l'état de l'appelant et appelle drop_butin<br>
     * - Pour Bandit: réduit les HP et set {@code targeted} à {@code Vrai}<br>
     * - Pour Passager: supprime le passager du wagon<br>
     * @param wagon le wagon contenant l'appelant
     */
    void est_vise(Wagon wagon);
}

/**Class Abstraite Personne<br>
 * Contient les informations de bases pour : {@code Bandit, Marchall, Passager}
 */
public abstract class Personne {
    protected int id;
    protected int position;
    protected String nom;
    protected String sprite;  //path vers l'image qui constitura le sprite de la personne
    protected boolean targeted = false;

    //Variables Static pour générer des indentifiants
    static protected int current_id_bandit = 0 ;
    static protected int getCurrent_id_passager = -2;


    Personne(String n){
        nom = n;
    }


    public int get_id(){ return this.id;}
    public String getSprite(){return sprite;}
    public int getPosition(){return position;}
    public boolean isTargeted(){return targeted;}
    public void setTargeted(boolean b){targeted = b;}


    public static void reinitialise(){
        current_id_bandit = 0;
        getCurrent_id_passager = -2;
    }

    @Override
    public String toString() {
        return nom;
    }
}