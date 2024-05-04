package Modele;

public enum Direction{
    AVANT("Avant" , -1),
    ARRIERE("Arrière" , 1),
    HAUT("Haut" , -Partie.NB_WAGON),
    BAS("Bas",Partie.NB_WAGON),
    ICI("Ici",0);

    private final String nom;
    private final int dir;

    Direction(String name,int d){
        nom = name;
        dir =d;
    }


    public int dir(){return dir;}
    @Override
    public String toString(){return nom;}

}
