package Vue;

import Modele.*;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


/**Classe Vue
 * Contient le main principale pour l'affichage graphique<br>
 * Stockent des variables utilisé par les différentes sections de la Vue
 */
public class CVue {
    final static Font font1 = new Font("Arial", Font.BOLD, 20);
    final static Font font2 = new Font("Arial", Font.BOLD, 14);
    static Toolkit toolkit = Toolkit.getDefaultToolkit();
    static final Dimension screenSize = toolkit.getScreenSize();
    static final int screenWidth = screenSize.width;
    static final int screenHeight = screenSize.height;
    private final JFrame frame;
    /*bg[0] -> background du menu de démarrage
    * bg[1] -> background pour VueCommandes
    * */
    private final ImageIcon[] bg = new ImageIcon[] {new ImageIcon((new ImageIcon(Objects.requireNonNull(CVue.class.getResource("Images/public.jpg")))).getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH)),
            new ImageIcon((new ImageIcon(Objects.requireNonNull(CVue.class.getResource("Images/backGroundCommandes.jpg")))).getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH))};

    //Main graphique
    public static void main(String[] args) {
        EventQueue.invokeLater(CVue::new);
    }

    public CVue() {
        Partie.resetAll();
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(screenWidth,screenHeight));
        frame.setTitle("Colt Express");
        frame.setMinimumSize(new Dimension(screenWidth*8/10 , screenHeight*8/10));

        JPanel app = new JPanel(new CardLayout());
        JPanel container = new JPanel(new BorderLayout());
        VueInput menu = new VueInput(this);

        app.add(menu);
        app.add(container);
        app.setOpaque(false);
        frame.add(app);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize( new Dimension(CVue.screenWidth,CVue.screenHeight));
        frame.setVisible(true);

        JLabel label = new JLabel(bg[0]);
        frame.setContentPane(label);
        frame.add(menu);

    }

    /**Passe de l'écran d'acueil à l'interface de jeu
     *  Precondition au lancement: tous les parametres de jeu doivent être intialisé avec des valeurs valides
     * @param names Matrice contenant les noms des bandits entrés dans le menu pour l'initialisation
     */
    public void switchToGame(String[][] names){
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        JLabel label = new JLabel(bg[1]);
        frame.setContentPane(label);
        frame.setLayout(new BorderLayout());

        Partie p = new Partie(true, names); //initialisation de la partie

        //Création et ajout des JPanels principaux
        VuePlateau plateau = new VuePlateau(p.getTrain());
        frame.add(plateau, BorderLayout.NORTH);
        VueCommandes tableau_de_bord = new VueCommandes(p);
        frame.add(tableau_de_bord,BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize( new Dimension(CVue.screenWidth,CVue.screenHeight));
        frame.setVisible(true);

    }


    /**Fonction qui parcours un {@code Swing.Container} en profondeur pour rendre ses composants transparent ou pas<br>
     * Permet d'afficher les backgrounds
     * @param container Le container dont souhaite changer l'opacité
     * @param b Vrai pour rendre container opaque, Faux sinon
     */
    public static void setOpacityALL(Container container, boolean b){
        for (Component component : container.getComponents()) {
            if (component instanceof JComponent) {
                ((JComponent) component).setOpaque(b);
            }
            if (component instanceof Container) {
                setOpacityALL((Container) component,b);
            }
        }
        ((JComponent) container).setOpaque(b);
    }
}

