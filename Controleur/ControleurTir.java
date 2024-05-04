package Controleur;

import Modele.Partie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Class ControleurMouvement <br>set l'action choisie dans la partie en cours à 1 (Tir)
 * puis passe à au panel de choix de direction
 */
public class ControleurTir extends ControleurAction {

    public ControleurTir(Partie p , CardLayout switcher , JPanel j) {
        super(p,switcher,j);
    }


    public void actionPerformed(ActionEvent e) {
        partie.setActionChoisie(1);
        super.actionPerformed(e);
    }
}
