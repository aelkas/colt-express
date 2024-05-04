package Modele;
import java.util.*;


/**
 * Classe Partie <br>
 * Contient tous les parametres liée à l'initialisation et au déroulement d'une partie<br>
 * Intéragit avec la Vue et le Controleur
 */
public class Partie extends Observable {
    private static final int DELAY = 800 ;
    public static int NB_MUNITIONS = 6;
    public static double DEFAULT_PRECISION = 0.9;
    public static int DEFAULT_HP = 3;
    public static int NB_JOUEURS = 4;
    public static int NB_MANCHES = 5;   //Revoir le Caractere public de certaines
    public static int NB_BANDITS_JOUEUR = 1;
    public static int NB_WAGON ;
    public static double NEVROSITE_MARSHALL = 0.9;
    public static final int NB_PASSAGER_PAR_WAGON_MAX = 4;
    public static final double PROBA_PERTE_LOOT_TOIT = 0.05;

    //Permet de créer des instances d'action à partir d'une liste indéxée  (emprunt)
    public static List<Class<? extends Action>> Actions = new ArrayList<>();
    private Train train;
    private int numeroManche;
    /**
     *  Lignes -> Bandit <br>
     *  Colonnes -> tempo (action) <br>
     *  La matrice est remplit ligne par ligne lors de la planification<br>
     *  puis executer colonne par colonne de haut en bas
     * */
    private Action[][] matrice_action;
    private Joueur[] joueurs; //Array des joueurs de la partie
    private int joueurAct = 0;
    private int tempo = 0;

    //Attribut liée à la gestion de la planification en graphique
    private int actionChoisie = -1;
    private Direction directionChoisie ;



    //Main avec afffichage textuelle
    public static void main(String[] args) {
        System.out.println("\n\n");
        Partie partie = new Partie(false , null);
        partie.run(NB_MANCHES);
        partie.displayRankings();
    }


    public Partie(Boolean gui , String[][] noms) {
        //Initialisation de la liste d'action
        Actions.add(Deplacement.class);
        Actions.add(Tir.class);
        Actions.add(Braquage.class);
        Actions.add(Frappe.class);

        if(gui) initialisation_partie_gui(noms); //mode graphique
        else initialisation_partie();

    }


    /** Initalise les parametres de la partie reçu par la Vue à la création d'une instance
     * @param names Liste de {@code String} pour les noms des bandits
     */
    public void initialisation_partie_gui(String[][] names){

        if(NB_JOUEURS>= 2){
            NB_WAGON= NB_JOUEURS*NB_BANDITS_JOUEUR+1;
            this.joueurs = new Joueur[NB_JOUEURS];
            this.matrice_action = new Action[NB_JOUEURS*NB_BANDITS_JOUEUR][DEFAULT_HP];
            train = new Train();
            if(NB_JOUEURS>2) {
                for (int i = 0; i < NB_JOUEURS; i++) {
                    List<Bandit> pions = new ArrayList<>();
                    Bandit bandit = new Bandit(names[i][0], Partie.NB_WAGON - 1 - i % 2);
                    pions.add(bandit);
                    train.get_Wagon()[bandit.position].toit.add(bandit);
                    joueurs[i] = new Joueur(train, pions);
                }
            }
            else if(NB_JOUEURS==2){  //2 joueurs
                List<Bandit> p1 = new ArrayList<>();
                List<Bandit> p2 = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    Bandit b1 = new Bandit(names[0][j], Partie.NB_WAGON - 1 );
                    p1.add(b1);
                    Bandit b2 = new Bandit(names[1][j], Partie.NB_WAGON - 2);
                    p2.add(b2);
                    train.get_Wagon()[b1.position].toit.add(b1);
                    train.get_Wagon()[b2.position].toit.add(b2);
                }
                joueurs[0] = new Joueur(train, p1);
                joueurs[1] = new Joueur(train, p2);
            }
        }
        else {
            NB_JOUEURS = 4;
            NB_BANDITS_JOUEUR = 1;
            NB_WAGON= NB_JOUEURS*NB_BANDITS_JOUEUR+1;
            this.joueurs = new Joueur[NB_JOUEURS];
            this.matrice_action = new Action[NB_JOUEURS*NB_BANDITS_JOUEUR][DEFAULT_HP];
            train = new Train();
                List<Bandit> p1 = new ArrayList<>();
                List<Bandit> p2 = new ArrayList<>();
                List<Bandit> p3 = new ArrayList<>();
                List<Bandit> p4 = new ArrayList<>();
                    Bandit b1 = new Bandit(names[0][0], Partie.NB_WAGON - 1);
                    p1.add(b1);
                    Bandit b2 = Bandit_Bot.create_Bandit_Bot(this, Partie.NB_WAGON - 1);
                    p2.add(b2);
                    Bandit b3 = Bandit_Bot.create_Bandit_Bot(this, Partie.NB_WAGON - 2);
                    p3.add(b3);
                    Bandit b4 = Bandit_Bot.create_Bandit_Bot(this, Partie.NB_WAGON - 2);
                    p4.add(b4);
                    train.get_Wagon()[b1.position].toit.add(b1);
                    train.get_Wagon()[b2.position].toit.add(b2);
                    train.get_Wagon()[b3.position].toit.add(b3);
                    train.get_Wagon()[b4.position].toit.add(b4);

                joueurs[0] = new Joueur(train, p1);
                joueurs[1] = new Bot(train, p2);
                joueurs[2] = new Bot(train, p3);
                joueurs[3] = new Bot(train, p4);
                for (Joueur j : joueurs) {
                    for (Bandit i : j.pions) {
                        if (i instanceof Blood_thirsty_Bot) ((Blood_thirsty_Bot) i).targets_initialisation();
                    }
                }
        }

    }


    /**
     * Initalise les parametres de la partie à la création d'une instance pour l'afficahge textuelle
     */
    public void initialisation_partie() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\n");
        System.out.println("Bien le bonjour à toi, jeune aventurier, tentant de t'immiscer dans le cruel monde du Far West.\n");
        System.out.print("Entrez le nombre de joueurs (par défaut 4) : ");
        int numOfPlayers;
        do {
             numOfPlayers= scanner.nextInt();
            scanner.nextLine();
        }while(numOfPlayers < 0);
        NB_JOUEURS = numOfPlayers;
        NB_BANDITS_JOUEUR = (NB_JOUEURS <=2 ? 2 : 1);
        NB_WAGON = NB_JOUEURS*NB_BANDITS_JOUEUR + 1;
        this.joueurs = new Joueur[NB_JOUEURS];
        System.out.print("Entrez le nombre de tours (par défaut 5, minimum 3) : ");
        int tours;
        do {
            tours = scanner.nextInt();
            scanner.nextLine();
        }while(tours <3);
        NB_MANCHES = tours;
        this.matrice_action = new Action[numOfPlayers * NB_BANDITS_JOUEUR][DEFAULT_HP];
        train = new Train();
            if(NB_JOUEURS >=2) {
                for (int i = 0; i < numOfPlayers; i++) {
                    System.out.print("Entrez votre nom J" + (i + 1) + " puis celui de vos bandits: ");
                    String nameJ = scanner.nextLine();
                    List<Bandit> pions = new ArrayList<>();
                    for (int j = 0; j < NB_BANDITS_JOUEUR; j++) {
                        System.out.print("Bandit N°" + (j + 1) + " : ");
                        String name = scanner.nextLine();
                        Bandit bandit = new Bandit(name, Partie.NB_WAGON - 1 - i % 2);
                        pions.add(bandit);
                        System.out.print("Bienvenue à bord, " + name + "\n");
                        train.get_Wagon()[bandit.position].toit.add(bandit);
                    }
                    Joueur j = new Joueur(train, pions);
                    j.setNom(nameJ);
                    joueurs[i] = j;
                }
            }
            else{
                NB_JOUEURS = 2;
                NB_WAGON= NB_JOUEURS*NB_BANDITS_JOUEUR+1;
                this.joueurs = new Joueur[NB_JOUEURS];
                this.matrice_action = new Action[NB_JOUEURS*NB_BANDITS_JOUEUR][DEFAULT_HP];
                train = new Train();
                this.joueurs = new Joueur[2];

                System.out.print("Entrez votre nom J1 puis celui de vos bandits: ");
                String nameJ = scanner.nextLine();
                List<Bandit> pions = new ArrayList<>();
                for (int j = 0; j < NB_BANDITS_JOUEUR; j++) {
                    System.out.print("Bandit N°" + (j + 1) + " : ");
                    String name = scanner.nextLine();
                    Bandit bandit = new Bandit(name, Partie.NB_WAGON - 1);
                    pions.add(bandit);
                    System.out.print("Bienvenue à bord, " + name + "\n");
                    train.get_Wagon()[bandit.position].toit.add(bandit);
                }
                Joueur j = new Joueur(train, pions);
                j.setNom(nameJ);
                joueurs[0] = j;

                List<Bandit> pbot = new ArrayList<>();
                for (int k = 0; k < NB_BANDITS_JOUEUR; k++) {
                    Bandit b2 = Bandit_Bot.create_Bandit_Bot(this, Partie.NB_WAGON - 2);
                    pbot.add(b2);
                }

                Joueur j2 = new Bot(train, pbot);
                j2.setNom("Bot");
                joueurs[1] = j2;
            }
            for(Bandit i : joueurs[1].pions){
                if (i instanceof Blood_thirsty_Bot) ((Blood_thirsty_Bot)i).targets_initialisation();
            }
    }

        /** Fonction principale pour le mode textuelle
         * @param nb_manches nombre de manches à jouer avant la fin de la partie
         */
    private void run(int nb_manches) {
        for (int numeroManche = 0; numeroManche < nb_manches; numeroManche++) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println(train);
            // Planification
            for (Joueur j : joueurs) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("C'est au tour du joueur " + (j.getId()+1) + " : \n");
                j.joue_manche(matrice_action);
            }

            // Exécution

            for (int i = 0; i < DEFAULT_HP; i++) {
                for (int j = 0; j < NB_JOUEURS; j++) {
                    if (matrice_action[j][i] != null) {
                        matrice_action[j][i].executer();
                    }
                }
            }
            String r = "";
            for(Joueur j:joueur_en_tete()){

                r += j.toString()+" ";
            }
            System.out.println(r+" en tête pour ce tour.\n");
            evenementsPassifs(true);
        }

    }


    /**Créer l'action planifier par le joueur dans la vue apres qu'il l'est spécifier son type et éventuellement uen direction
     * @return l'action que le joueur a planifié (pas encore confirmé)
     */
    public Action creeActionFinale(){  //code résultant de recherche dans les docs/forums
        if(actionChoisie == 2 || actionChoisie == 3 ){
            try {
                return Actions.get(actionChoisie).getConstructor(Bandit.class , Train.class).newInstance(joueurs[joueurAct].getPionAct() , train);
            } catch (Exception e) {
                return null;
            }
        }
        else if(directionChoisie != null){
            if (actionChoisie == 0) {
                try {
                    return Actions.get(actionChoisie).getConstructor(Movable.class, Train.class, Direction.class).newInstance(joueurs[joueurAct].getPionAct(), train, directionChoisie);
                } catch (Exception e) {
                    return null;
                }
            } else {
                try {
                    return Actions.get(actionChoisie).getConstructor(Bandit.class, Train.class, Direction.class).newInstance(joueurs[joueurAct].getPionAct(), train, directionChoisie);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Ajoute l'action planifié à la matrice d'action et passe la main au pion/joueur suivant si besoin
     */
    public void confirmeAction() {
        Action actionFinale = creeActionFinale(); //création de l'instance
        if(actionFinale != null) {
            Bandit pionActuelle = joueurs[joueurAct].getPionAct();
            matrice_action[pionActuelle.get_id()][tempo] = actionFinale;
            tempo++;
            if (tempo > pionActuelle.get_hitPoints() - 1) {
                //si HP<DefaultHP le bandit perd des actions
                for (int i = tempo; i < matrice_action[pionActuelle.get_id()].length; i++) {
                    matrice_action[pionActuelle.get_id()][i] = null;
                }
                //le joueur a entré toutes les actions du pion qu'il joue
                tempo = 0;
                if (!joueurs[joueurAct].getNextPion()) { //plus de pions, passage au joueur suivant
                    getNextJoueur();
                    if (joueurAct == 0) { //plus de joueur,la phase de planification est terminé , on execute
                        executerMatrice();
                    }
                }
            }

            //passage à l'action suivante
            actionChoisie = -1;
            directionChoisie = null;
            notifyObservers();
        }

    }

    public void sleep(){
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute toutes les actions planifié pour la manche en cours en notifiant la vue
     */
    public void executerMatrice(){
        notifyObservers("Au tour de "+getJoueurs()[0].getPions().get(0)+" ( J1 )");
        //Créer un thread à part pour ajouter du delais de l'affichage (emprunt)
        Thread actionThread = new Thread(() -> {
            for (int j = 0; j < matrice_action[0].length; j++) {
                for (int i = 0; i < matrice_action.length; i++) {
                    //Message indiquant le bandit qui va mener l'action
                    joueurAct = (NB_BANDITS_JOUEUR == 2 ? i%2 : i);  //switch les joueurs et les pions pour VueCommandes
                    Bandit b = joueurs[joueurAct].getPions().get((NB_BANDITS_JOUEUR == 2 ? i/NB_BANDITS_JOUEUR : 0));
                    joueurs[joueurAct].setPionAct(b);
                    notifyObservers("Au tour de "+b+" ( J"+(joueurAct+1)+" )");
                    sleep();
                    //Execution
                    if (matrice_action[i][j] != null) {
                        String message = matrice_action[i][j].executer();
                        notifyObservers(message);
                        matrice_action[i][j] = null;
                        sleep();
                        notifyObservers(evenementsPassifs(false)); //Execution d'evenements intermédiares aux actions
                        sleep();
                    }
                }
            }
            String r = "";
            for(Joueur j:joueur_en_tete()){

                r += j.toString()+" ";
            }
            System.out.println(r+" en tête pour ce tour.\n");
            notifyObservers(evenementsPassifs(true)); //Execution des évenements apres que tous les bandits est joué
            numeroManche++;
            joueurAct = 0;
            //Reset les pions actuelles pour partie à deux pions
            if (NB_BANDITS_JOUEUR == 2) {
                for (Joueur joueur : joueurs) {
                    joueur.setPionAct(joueur.getPions().get(0));
                }
            }
            notifyObservers(); //Réinitialisation de la vue
        });

        actionThread.start();

    }

    /** Détermine les joueurs menent la partie à la manche actuelle
     * @return Renvoie la liste des joueurs qui ont ammasé le plus gros butin (plusieurs si égalité il y a)
     */
    public List<Joueur> joueur_en_tete(){
        int max = joueurs[0].compte_argent();
        List<Joueur> premiers = new LinkedList<>();
        for (Joueur j : joueurs){
            int v = j.compte_argent();
            if(v > max){
                premiers = new LinkedList<>();
                premiers.add(j);
                max = v;
            }
            else if(v == max){
                premiers.add(j);
            }
        }
        return premiers;
    }

    public void displayRankings() {
        List<Joueur> premiers = joueur_en_tete();
        System.out.println("Classement :");
        System.out.println("A la premiere position nous avons *ROULEMENT DE TAMBOUR* : " + premiers.get(0).getNom() + " GG T'ES UNE MASTERCLASS ");
        for (int i = 1; i < premiers.size(); i++) {

            System.out.println("À la  " + (i + 1) + " - ieme position  nous avons  " + premiers.get(i).getNom());

        }
    }


    private String evenementsPassifs(boolean endTurn) {
        if(endTurn) {
            //Gère tous ce qui se passe entre les manches comme les deplacements du marshall
            for (Wagon w : train.get_Wagon()) { //perte de loot sur le toit
                w.perte_loot_toit();
            }
            //Mouvement du marshall
            Random r = new Random();
            Marchall m = train.getMarchall();
            List<Direction> dirs = m.mouvementsPossibles(train);
            String message = m.move(train, dirs.get(r.nextInt(dirs.size())));
            //Fuite éventuelle
            Wagon wagonMarshallAct = train.get_Wagon()[m.position];
            for (Bandit b : wagonMarshallAct.liste_bandits_int()) {
                b.fuit_marshall(wagonMarshallAct);
            }
            return message;
        }
        else{//Gère les evenements entre deux tempo (ex: un bandit un peu trop courageux qui rentre dans la cabine du marshall)
            Wagon wagonMarshall = train.get_Wagon()[train.getMarchall().getPosition()];
            for(Bandit b : wagonMarshall.liste_bandits_int()){
                return b.fuit_marshall(wagonMarshall);
            }
            return " ";
        }
    }

    /**
     * Permet à l'utilisateur de la vue de revenir sur ses pas lors de la planification tant
     * qu'il n'a pas cliqué sur le bouton "Action" à sa dernière action
     */
    public void annuleAction() {
        if(tempo>0){ //revient au tempo précédent
            tempo--;
        }
        else{ //Si plusieurs le joueurs a plusieurs pions
            if(joueurs[joueurAct].getPrevPion()){  //revient au pions précédent
                tempo = matrice_action[joueurs[joueurAct].getPionAct().get_id()].length-1;
            }
        }
        matrice_action[joueurs[joueurAct].getPionAct().get_id()][tempo] = null;
        actionChoisie=-1;
        directionChoisie = null;
        notifyObservers();
    }

    /**Fonction qui permet de connaitre les mouvements possibles pour un bandit durant toute la planification
     * @return Liste de direction des mouvements possibles à la position ou le bandit sera à ce stade de la planification
     */
    public List<Direction> mouvementsPossiblesPostPlan(){
        Bandit b = joueurs[joueurAct].getPionAct();
        int positionDep = b.getPosition() +  (b.getToit()? 0 :1)*NB_WAGON; //position de départ

        //calcul de la position d'arrivé à ce stade
        for (int i = 0; i <tempo; i++) {
            if(matrice_action[b.get_id()][i] instanceof Deplacement){
                positionDep += ((Deplacement)matrice_action[b.get_id()][i]).getDir().dir();
            }
        }
        return train.mouvementPossibles(true,positionDep%NB_WAGON,positionDep < NB_WAGON);
    }


    /* Getters et Setter */

    public void getNextJoueur(){
        joueurAct  = (joueurAct+1)%NB_JOUEURS;
        if(joueurs[joueurAct] instanceof Bot){
            List<Bandit> bots = joueurs[joueurAct].getPions();
            for (Bandit b : bots) {
                List<Action> actionsPlanifie = ((Bandit_Bot) b).actions_bot();
                for (int i = 0; i < actionsPlanifie.size(); i++) {
                    matrice_action[b.get_id()][i] = actionsPlanifie.get(i);
                }
            }
            getNextJoueur();
        }
    }

    public Joueur[] getJoueurs() {
        return joueurs;
    }

    public int getNumeroTour() {
        return this.numeroManche;
    }
    public Train getTrain(){return this.train;}

    public int getJoueurAct() {
        return joueurAct;
    }
    public Action[][] getMatrice_action(){return matrice_action;}
    public void setActionChoisie(int actionChoisie) {
        assert  actionChoisie>=0 && actionChoisie<4 ;


        this.actionChoisie = actionChoisie;
    }
    public void setDirectionChoisie(Direction directionChoisie) {
        this.directionChoisie = directionChoisie;
    }

    public static void reinitialise(){DEFAULT_HP=6 ;NB_JOUEURS=4;NB_MANCHES=5;DEFAULT_PRECISION=0.9;NEVROSITE_MARSHALL=0.3;NB_MUNITIONS=6;}

    public static void resetAll(){Partie.reinitialise();Wagon.reinitialise();Personne.reinitialise();Joueur.reintialise();}
}