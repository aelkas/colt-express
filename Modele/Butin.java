package Modele;

import java.util.Random;
public enum Butin {

    BOURSE("Bourse" , (new Random()).nextInt(500),"../Vue/Images/bourse.png",40,40 ) ,
    BIJOUX("Bijoux",500,
            (new String[] {"../Vue/Images/bijoux1.png","../Vue/Images/bijoux2.png", "../Vue/Images/bijoux3.png"})[(new Random()).nextInt(3)],30,30),
    MAGOT("Magot",1000,"../Vue/Images/magotLoot.png",60,60);

    private final String nom;
    private final int valeur;
    private final String sprite ;
    private final int spriteH;
    private final int spriteW;


    Butin(String name , int value,String sp, int w, int h){
        nom = name;
        valeur = value;
        sprite = sp;
        spriteW = w;
        spriteH = h;
    }

    public int valeur(){return valeur;}

    @Override
    public String toString(){return nom;}

    public String getSprite() {
        return sprite;
    }

    public int getSpriteH() {
        return spriteH;
    }

    public int getSpriteW() {
        return spriteW;
    }
}


