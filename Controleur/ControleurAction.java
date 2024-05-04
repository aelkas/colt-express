package Controleur;

import Modele.Direction;
import Modele.Partie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Des Controleur pour les actions qui nécéssite le choix d'une direction (qui ne peuvent donc pas se regler avec une lambda expression)<br>
 * Classe ControleurAction<br>
 * Gère le passage des boutons d'actions dans la vue aux boutons de directions
 */
public abstract class ControleurAction implements ActionListener {

    protected Partie partie;
    private final JPanel VueBoutons;
    private final CardLayout switcher;
    public ControleurAction(Partie p , CardLayout switcher , JPanel j) { this.partie = p; this.switcher=switcher ; this.VueBoutons = j;}


    public void actionPerformed(ActionEvent e) {
        switcher.next(VueBoutons); //changement de panel
        JPanel panelDirections = (JPanel) VueBoutons.getComponent(1);
        JButton up = (JButton)  panelDirections.getComponent(1);
        JButton left = (JButton) panelDirections.getComponent(3);
        JButton right = (JButton)  panelDirections.getComponent(5);
        JButton down = (JButton)  panelDirections.getComponent(7);

        down.setEnabled(false);
        up.setEnabled(false);
        left.setEnabled(false);
        right.setEnabled(false);

        for (Direction d : partie.mouvementsPossiblesPostPlan()){ //active que les boutons qui offrent des possibilités valide
            switch (d){
                case BAS: down.setEnabled(true); break;
                case HAUT: up.setEnabled(true);break;
                case AVANT: left.setEnabled(true);break;
                case ARRIERE: right.setEnabled(true);break;
                case ICI:break;
            }
        }

    }
}


