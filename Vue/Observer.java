package Vue;

public interface Observer {

    void update();

    /**Surcharge d'update , permet au modèle de transmettre un message à la Vue
     * @param str message à afficher dans la Vue
     */
    void update(String str);
}
