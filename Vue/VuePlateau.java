package Vue;

import Modele.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;

import static java.lang.Math.max;

/**Classe VuePlateau<br>
 * Gère l'affichage concret du train et ses différents éléments<br>
 * Le train s'affiche 4 wagon par 4 wagon au maximum,
 * cela permet de ne pas essayer de packer 9 wagons sur une largeur d'écran
 * Les touches A et D permettent de déplacer la Vue à droite et à gauche
 */
public class VuePlateau extends JPanel implements Observer {

    private final Train train;
    private static final int dec = 5;
    //Dimensions des différents éléments
    private final static int WIDTH = CVue.screenWidth / 4 - dec;
    private final static int HEIGHTCABINE = CVue.screenHeight / 3;
    private final static int HEIGHTLOCO = CVue.screenHeight / 2;
    static final int spriteH = 98;
    static final int spriteW = 48;

    //Initialisation des Sprites
    private final ImageIcon locoSprite = new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/locomotive.png")));
    private final ImageIcon cabineSprite = new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/cabine.png")));
    private final ImageIcon[] coffreSprite = {new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/safe.png"))), new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/openSafe.png")))};
    private final ImageIcon backGround  = new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/background.jpg")));

    private final HashMap<Integer, ImageIcon> spriteMapPersonnes = new HashMap<>();
    private final HashMap<Butin, ImageIcon> spriteMapButins = new HashMap<>();
    private int start = max(Partie.NB_WAGON-4,0);

    public VuePlateau(Train t) {
        Dimension dim = new Dimension(CVue.screenWidth,
                CVue.screenHeight * 6 / 10);
        this.setPreferredSize(dim);

        this.train = t;
        //Associe chaque personne et butin à son sprite dans une hashmap pour les retrouver plus tard
        for (Wagon w : t.get_Wagon()) {
            for (Bandit b : w.getToit()) {
                spriteMapPersonnes.put(b.get_id(), new ImageIcon(Objects.requireNonNull(getClass().getResource(b.getSprite()))));
            }
            for (Personne p : w.getInterieur()) {
                spriteMapPersonnes.put(p.get_id(), new ImageIcon(Objects.requireNonNull(getClass().getResource(p.getSprite()))));
                if (p instanceof Passager) {
                    Butin b = ((Passager) p).getPoche();
                    spriteMapButins.put(b, new ImageIcon(Objects.requireNonNull(getClass().getResource(b.getSprite()))));
                }
            }
        }
        Butin magot = ((Locomotive) t.get_Wagon()[0]).getMag();
        spriteMapButins.put(magot, new ImageIcon(Objects.requireNonNull(getClass().getResource(magot.getSprite()))));
        train.addObserver(this);

        //Raccourcis clavier pour déplacer afficher le reste du train
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(start>0)
                    start--;
            }
        };

        KeyStroke keyStroke = KeyStroke.getKeyStroke("A");
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();
        inputMap.put(keyStroke, "performAction");
        actionMap.put("performAction", action);

        AbstractAction action2 = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(start+3<Partie.NB_WAGON-1)
                    start++;
            }
        };

        KeyStroke keyStroke2 = KeyStroke.getKeyStroke("D");
        inputMap.put(keyStroke2, "performAction2");
        actionMap.put("performAction2", action2);

    }


    /**
     * @see Observer
     */
    public void update() { repaint(); }

    /**
     * @see Observer
     */
    @Override
    public void update(String str) {
        update();
    }


    /**Convertit une image en BufferedImage
     * @param img une image
     * @return buffered image identique à img
     */
    public static BufferedImage toBufferedImage(Image img) {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bimage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return bimage;
    }

    /** Colorie une bufferedImage en gardant sa forme
     * @param loadImg image à colorier
     * @param red composante rouge de la couleur
     * @param green composante verte de la couleur
     * @param blue composante bleue de la couleur
     * @return loadImg colorié avec la couleur (red,green,blue)
     */
    public static BufferedImage colorImage(BufferedImage loadImg, int red, int green, int blue) {//emprunt
        int width = loadImg.getWidth();
        int height = loadImg.getHeight();
        BufferedImage tintedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tintedImg.createGraphics();
        g2d.drawImage(loadImg, 0, 0, null);
        Color tint = new Color(red, green, blue);
        g2d.setColor(tint);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();

        return tintedImg;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.repaint();
        super.paintComponent(g);


        //Calcul de la position de départ et la largeur à dessiner dans l'image du background
        int sourceX =  (int) ((start*WIDTH) * (float) backGround.getIconWidth() / (2.25* CVue.screenWidth));
        int sourceWidth = (int) ( WIDTH * 4 * (float) backGround.getIconWidth() / (2.25*CVue.screenWidth));


        // Dessin du background sur l'écran
        g.drawImage(backGround.getImage(), 0,0, this.getWidth(), this.getHeight() ,
                sourceX, 0, sourceX + sourceWidth, backGround.getIconHeight(), this);

        int ycabine = CVue.screenHeight/12 +HEIGHTLOCO - 80 ; //hauteur de dessin dans le wagon
        int ytoit = CVue.screenHeight/12 +HEIGHTLOCO - HEIGHTCABINE +5; //Hauteur de dessin sur le toit wagon
        for(int i=0; i<=3; i++) {
            paintWagon(g,i+start, i*WIDTH+Partie.NB_WAGON*dec/2);
            int x = i*WIDTH+Partie.NB_WAGON*dec/2 + spriteW/2;
            int y = 0;
            if(start==0 && i==0) {
                paintCoffre(g, (Locomotive) train.get_Wagon()[0], Partie.NB_WAGON*dec/2);
                x+= - spriteW/2 + WIDTH/2;
                y += 20;
            }
            paintPersonne(g, train.get_Wagon()[i+start],x, y+ycabine-spriteH,y+ytoit-spriteH);
            paintButin(g, train.get_Wagon()[i+start], x, y+ycabine,y+ytoit); //paint les butins dropés
        }
    }

    private void paintCoffre(Graphics g, Locomotive l, int x) {
        if(l.magot_dispo()){
            g.drawImage(coffreSprite[0].getImage(), x+WIDTH/2,CVue.screenHeight/12 + HEIGHTLOCO -160,WIDTH/4,100, this);
        }
        else{//Coffre ouvert quand le magot a été volé
            g.drawImage(coffreSprite[1].getImage(), x+WIDTH/2, CVue.screenHeight/12 + HEIGHTLOCO -160 ,WIDTH/4,100, this);
        }
    }


    private void paintWagon(Graphics g,int i, int x) {
        int y = CVue.screenHeight/12;
        int decH = HEIGHTLOCO - HEIGHTCABINE;
        Image img ;
        int h ;
        if(i==0) {
            img = locoSprite.getImage();
            h = HEIGHTLOCO;
        }
        else {
            img = cabineSprite.getImage();
            h = HEIGHTCABINE;
            y+=decH;
        }
        g.drawImage(img, x , y ,WIDTH,h,this);
    }

    private void drawBandit(Graphics g, Bandit b , int x , int y){
        if(b.isTargeted()) { //Gestion du hit effect
            BufferedImage originalSprite = toBufferedImage(spriteMapPersonnes.get(b.get_id()).getImage());
            BufferedImage redSprite = colorImage(originalSprite, 255, 0, 0); //on colorie le sprite du bandit en rouge
            g.drawImage(redSprite, x, y, spriteW, spriteH, this); //on dessine
            Thread hitThread = new Thread(() -> { //on crée un thread pour lancer un delaie d'attente
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                g.drawImage(spriteMapPersonnes.get(b.get_id()).getImage(), x, y, spriteW, spriteH, this); //on redessine le sprite original du bandit
                b.setTargeted(false);
            });
            hitThread.start();
        }
        else g.drawImage(spriteMapPersonnes.get(b.get_id()).getImage(), x, y,spriteW, spriteH, this);
        g.drawString(b.toString() ,x,y-10);
    }


    private void paintPersonne(Graphics g, Wagon w, int x,int yInt , int yToit) {

        //Toit
        for (int i = 0; i < w.getToit().size(); i++) {
            Bandit b = w.getToit().get(i);
            drawBandit(g,b,x+ i* (spriteW + 5),yToit);
        }
        //Interieur
        for (int i = 0; i < w.getInterieur().size(); i++) {
            Personne p = w.getInterieur().get(i);
            if(p instanceof Bandit){
               drawBandit(g,(Bandit) p,x+i* (spriteW + 5) , yInt);
            }
            else g.drawImage(spriteMapPersonnes.get(p.get_id()).getImage(), x+i*(spriteW+5), yInt,spriteW,spriteH, this);
        }
    }

    private void paintButin(Graphics g, Wagon w, int x, int yInt, int yToit) {
        //Toit
        for (int i = 0; i < w.getLootToit().size(); i++) {
            Butin b = w.getLootToit().get(i);
            g.drawImage(spriteMapButins.get(b).getImage(), x+i*(spriteW+5), yToit-b.getSpriteH(),b.getSpriteW(),b.getSpriteH(), this);
        }
        //Interieur
        for (int i = 0; i < w.getLootInt().size(); i++) {
            Butin b = w.getLootInt().get(i);
            g.drawImage(spriteMapButins.get(b).getImage(), x+i*(spriteW+5), yInt-b.getSpriteH() ,b.getSpriteW(),b.getSpriteH(), this);
        }
    }


}
