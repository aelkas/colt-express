package Vue;

import Controleur.ControleurMouvement;
import Controleur.ControleurTir;
import Modele.*;
import Modele.Action;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**Classe VueCommandes<br>
 * Contient tous les boutons qui permettent à l'utilisateur d'interagir avec le jeu<br>
 * Gère l'affichage des actions planifié et executé de manière dynamique<br>
 *<br>
 * Composition:<br><br>
 * HUD: Contient le nom du joueur qui joue actuellement , le nom du bandit et ses stats (hp,argent,munitions)<br>
 * <br>
 * Grille de boutons: permet à l'utilsateur de planifier ses actions, les confirmer ou les annuler.<br>
 * <br>
 * Prompt:
 * <ul>
 *      -Lors de la planification: Affiche les actions qui on été planifié au fur et à mesure<br>
 *      -Lors de l'éxécution: Affiche une description des actions qui sont menées
 * </ul>
 */
public class VueCommandes extends JPanel implements Observer{

    //Sprites de: Coeur , Balles , Blessure
    private final ImageIcon[] sprites = new ImageIcon[]{new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/coeur.png"))),new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/ammo.png"))),
            new ImageIcon(Objects.requireNonNull(getClass().getResource("Images/wound.png")))};
    private final Partie partie;
    private boolean execution = false, disableButtons = false; //Permet de des/activer les boutons juste quand cela est nécéssaire

    /** Fonction Auxilière qui permet d'ajouter un component à un container avec un {@code GridBagLayout()}
     * @param container l'element graphique qui va contenir component
     * @param component l'element graphique à ajouter à container
     * @param gbc une référence vers {@code GridBagConstraints} du container
     * @param gridx numéro de la colonne ou insérer component
     * @param gridy numéro de la ligne ou insérer component
     * @param gridwidth nombre de colonne que peut prendre l'élément ()
     */
    private static void addComponent(Container container, Component component, GridBagConstraints gbc,
                                     int gridx, int gridy, int gridwidth) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = 1;
        if(component instanceof JButton) component.setPreferredSize(new Dimension(70,40));
        container.add(component, gbc);
    }

    public VueCommandes(Partie p) {
        this.partie = p;
        Dimension dim = new Dimension(CVue.screenWidth,
                CVue.screenHeight*3/10 );
        this.setPreferredSize(dim);
        this.setLayout(new GridBagLayout());

        //Les Panels
        JPanel text = new JPanel();
        JPanel boutons = new JPanel();
        JPanel actions = new JPanel();
        JPanel fleches = new JPanel();
        JPanel prompt = new JPanel();
        JPanel promptPlanification = new JPanel();
        JPanel promptExecution = new JPanel();

        //Switch le panel de boutons
        CardLayout switcher = new CardLayout(); //permet de passer du panel de boutons d'action aux directions
        boutons.setLayout(switcher);
        boutons.add(actions,"card 1");
        boutons.add(fleches,"card 2");



        //Panel de titre
        text.setLayout(new GridLayout(3,1));
        JLabel t1 = new JLabel("Tour du Joueur N°"+(partie.getJoueurAct()+1));
        Bandit b = partie.getJoueurs()[0].getPions().get(0);
        JLabel t2 = new JLabel("Bandit : " + b );
        JPanel t3 = new JPanel(new GridLayout(1,5));
        t3.setBorder(new EmptyBorder(0, dim.width/3, 0, dim.width/3));
        JLabel l1 = new JLabel();
        JLabel l2 = new JLabel(": " +Partie.DEFAULT_HP);
        JLabel l3 = new JLabel(b.compte_butins()+"$");
        JLabel l4 = new JLabel();
        JLabel l5 = new JLabel(": " +b.get_ammo());
        l1.setIcon(sprites[0]);
        l4.setIcon(sprites[1]);

        t1.setForeground(Color.WHITE);
        t2.setForeground(Color.WHITE);
        l2.setForeground(Color.WHITE);
        l3.setForeground(Color.WHITE);
        l5.setForeground(Color.WHITE);

        t3.add(l1);
        t3.add(l2);
        t3.add(l3);
        t3.add(l4);
        t3.add(l5);
        t1.setFont(CVue.font1);
        t2.setFont(CVue.font2);
        t3.setFont(CVue.font2);
        t1.setHorizontalAlignment(JLabel.CENTER);
        t2.setHorizontalAlignment(JLabel.CENTER);
        text.add(t1);
        text.add(t2);
        text.add(t3);


        //Panels des boutons d'actions
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        boutons.setBorder(new EmptyBorder(0, dim.width/5, 0, dim.width/5)); //padding

        int buttonRoundness = 30;
        actions.setLayout(new GridBagLayout());
        RoundedButton boutonAction1 = new RoundedButton("Action",buttonRoundness);
        RoundedButton boutonAction2 = new RoundedButton("Action",buttonRoundness);
        RoundedButton boutonSeDeplacer = new RoundedButton("Se Deplacer",buttonRoundness);
        RoundedButton boutonTir = new RoundedButton("Tirer",buttonRoundness);
        RoundedButton boutonFrappe = new RoundedButton("Frapper",buttonRoundness);
        RoundedButton boutonBraque = new RoundedButton("Braquer",buttonRoundness);
        RoundedButton boutonRetour = new RoundedButton("Retour",buttonRoundness);

        addComponent(actions , boutonSeDeplacer , gbc, 0,0,1);
        addComponent(actions ,boutonBraque, gbc, 1,0,1);
        addComponent(actions ,boutonTir, gbc, 2,0,1);
        addComponent(actions ,boutonFrappe, gbc, 3,0,1);
        addComponent(actions ,boutonRetour, gbc, 0,1,2);
        addComponent(actions ,boutonAction1, gbc, 2,1,2);



        //Prompt des actions planifiées
        promptPlanification.setLayout(new GridLayout(1,Partie.DEFAULT_HP));
        JLabel promptText = new JLabel();
        promptText.setFont(CVue.font2);
        for (int i = 1; i <=Partie.DEFAULT_HP; i++) {
            promptText = new JLabel("Action N°"+i);
            promptText.setForeground(Color.WHITE);
            promptText.setHorizontalAlignment(JLabel.CENTER);
            promptPlanification.add(promptText);
        }

        //Pour L'execution
        promptExecution.add(new JLabel("Au tour de "+partie.getJoueurs()[0].getPions().get(0)+" ( J1 )"));


        fleches.setLayout(new GridLayout(4,4,10,0));
        fleches.setPreferredSize(new Dimension(dim.width/4 , dim.height));

        //Choix de directions
        RoundedButton gauche = new RoundedButton("←",buttonRoundness);
        RoundedButton droite = new RoundedButton("→",buttonRoundness);
        RoundedButton haut = new RoundedButton("↑",buttonRoundness);
        RoundedButton bas = new RoundedButton("↓",buttonRoundness);
        RoundedButton retourActions = new RoundedButton("Retour",buttonRoundness);


        fleches.add(new JLabel());
        fleches.add(haut);
        fleches.add(new JLabel());
        fleches.add(gauche);
        fleches.add(new JLabel());
        fleches.add(droite);
        fleches.add(new JLabel());
        fleches.add(bas);
        fleches.add(new JLabel());
        fleches.add(retourActions);
        fleches.add(new JLabel());
        fleches.add(boutonAction2);


        //Gestion du passage du prompt de planification à celui d'éxécution
        CardLayout switcher2 = new CardLayout();
        prompt.setLayout(switcher2);
        prompt.add(promptPlanification, "p1");
        prompt.add(promptExecution, "p2");

        //Layout globale
        addComponent(this,text , gbc , 0,0 , 1);
        addComponent(this,boutons , gbc , 0,1,1);
        addComponent(this,prompt , gbc , 0,2,1);


        //EventListeners
        boutonAction1.addActionListener(e -> partie.confirmeAction());
        boutonAction2.addActionListener(e -> {partie.confirmeAction();switcher.show(boutons,"card 1");}); //revient aux panel de choix d'actions
        boutonSeDeplacer.addActionListener(new ControleurMouvement(p,switcher,boutons));
        boutonTir.addActionListener(new ControleurTir(p,switcher,boutons));
        boutonBraque.addActionListener(e -> partie.setActionChoisie(2));
        boutonFrappe.addActionListener(e -> partie.setActionChoisie(3));
        boutonRetour.addActionListener(e -> partie.annuleAction());
        haut.addActionListener(e -> partie.setDirectionChoisie(Direction.HAUT));
        bas.addActionListener(e -> partie.setDirectionChoisie(Direction.BAS));
        gauche.addActionListener(e -> partie.setDirectionChoisie(Direction.AVANT));
        droite.addActionListener(e -> partie.setDirectionChoisie(Direction.ARRIERE));
        retourActions.addActionListener(e -> switcher.next(boutons)); //bouton de retour vers le panel de choix d'action les directions sont affichées

        //Racourcis Clavier
        createKeyShortcut(boutonSeDeplacer , KeyEvent.VK_S);
        createKeyShortcut(boutonTir , KeyEvent.VK_T);
        createKeyShortcut(boutonBraque , KeyEvent.VK_B);
        createKeyShortcut(boutonFrappe, KeyEvent.VK_F);
        createKeyShortcut(boutonAction1, KeyEvent.VK_ENTER);
        createKeyShortcut(boutonAction2, KeyEvent.VK_ENTER);
        createKeyShortcut(boutonRetour, KeyEvent.VK_ESCAPE);
        createKeyShortcut(retourActions, KeyEvent.VK_ESCAPE);
        createKeyShortcut(haut , KeyEvent.VK_UP);
        createKeyShortcut(bas , KeyEvent.VK_DOWN);
        createKeyShortcut(gauche , KeyEvent.VK_LEFT);
        createKeyShortcut(droite , KeyEvent.VK_RIGHT);

        partie.addObserver(this);
        CVue.setOpacityALL(this,false);
    }


    /**Fonction auxilière qui associe une touche à l'action de cliquer sur un bouton
     * @param b le bouton à associer à la touche
     * @param key la touche raccourci à associer au bouton b
     */
    private void createKeyShortcut(JButton b , int key){
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.doClick();
            }
        };
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key, 0);
        InputMap inputMap = b.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = b.getActionMap();
        inputMap.put(keyStroke, "clickButton");
        actionMap.put("clickButton", action);
    }


    /**
     * Fonction Auxilière qui dessine la section HUD
     */
    public void paintStats(){
        JPanel panel1 = (JPanel) this.getComponent(0);
        ((JLabel)panel1.getComponent(0)).setText("Tour du Joueur N°"+(partie.getJoueurAct()+1));
        Bandit b = partie.getJoueurs()[partie.getJoueurAct()].getPionAct();
        ((JLabel)panel1.getComponent(1)).setText("Bandit : "+b);

        JPanel stats = (JPanel) panel1.getComponent(2);
        JLabel statcoeur = ((JLabel)stats.getComponent(0));
        statcoeur.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel statammo = ((JLabel)stats.getComponent(3));
        statammo.setHorizontalAlignment(4);
        ((JLabel)stats.getComponent(1)).setText(": " +b.get_hitPoints());
        JLabel cash  = ((JLabel)stats.getComponent(2));
        cash.setText(b.compte_butins()+"$");
        cash.setHorizontalAlignment(0);
        ((JLabel)stats.getComponent(4)).setText(": " +b.get_ammo());
    }

    /**
     * Fonction Auxilière qui gère le prompt durant la phase de planification
     */
    private void paintP1(){
        if(disableButtons) {
            JPanel boutons = (JPanel) this.getComponent(1);
            for (Component c : ((JPanel) boutons.getComponent(0)).getComponents())
                c.setEnabled(true);
            disableButtons = false;
        }
        JPanel panel = (JPanel) this.getComponent(2);
        ((CardLayout) panel.getLayout()).show(panel, "p1");
        Bandit b = partie.getJoueurs()[partie.getJoueurAct()].getPionAct();
        JPanel panel2 = (JPanel) panel.getComponent(0);
        for (int i = 0; i < Partie.DEFAULT_HP; i++) {
            JLabel p = ((JLabel) panel2.getComponent(i));
            p.setIcon(null);
            p.setText("");
            p.revalidate();
            if (i >= b.get_hitPoints()) {
                p.setIcon(sprites[2]);
            } else {
                Action a = partie.getMatrice_action()[b.get_id()][i];
                if (a != null) {
                    p.setText(a.toString());
                } else {
                    p.setText("Action N°" + (i + 1));
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        paintStats();
        if(!execution) paintP1();
    }


    public void update() {
        if(partie.getNumeroTour()>=Partie.NB_MANCHES){ //La partie est terminé, on affiche le podium
            new Podium(partie,this);
        }
        else {
            execution = false;
            repaint();
        }
    }

    /**
     * @see Observer
     */
    @Override
    public void update(String str) {
            if (!disableButtons) {
                JPanel boutons = (JPanel) this.getComponent(1);
                for (Component c : ((JPanel) boutons.getComponent(0)).getComponents())
                    c.setEnabled(false);
                disableButtons = true;
            }
            JPanel panel = (JPanel) this.getComponent(2);
            ((CardLayout) panel.getLayout()).show(panel, "p2");
            JPanel panel2 = (JPanel) panel.getComponent(1);
            JLabel textPrompt = (JLabel) panel2.getComponent(0);
            textPrompt.setForeground(Color.WHITE);
            textPrompt.setText(str);
            execution = true;
    }
}