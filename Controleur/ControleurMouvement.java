package Controleur;

import Modele.Partie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Class ControleurMouvement<br>
 * set l'action choisie dans la partie en cours à 0 (deplacement) puis passe à au panel de choix de direction
 */
public class ControleurMouvement extends ControleurAction {
    
    public ControleurMouvement(Partie p , CardLayout switcher , JPanel j) {
        super(p,switcher,j);
    }


    public void actionPerformed(ActionEvent e) {
        partie.setActionChoisie(0);
        super.actionPerformed(e);
    }
}
