package Modele;

import java.util.*;

public class Bot extends Joueur{

    public Bot(Train t, List<Bandit> b) {
        super(t, b);
    }

    @Override
    public void joue_manche(Action[][] mat_manche){
        // ca c'est pour partie et non pour GUI
        for (Bandit b : pions){ //si il joue plusieurs pions en meme temps (par équipe)
            List<Action> actions = ((Bandit_Bot) b).actions_bot();
            int k = 0;
            for (Action j : actions) {
                System.out.println("Action N°" + (k+1) + "\n");
                System.out.println("braque / bouge / Tir / frappe\n");
                //Version switch
                System.out.println(j);
                mat_manche[b.get_id()][k] = j;
            }
            for (int i = b.get_hitPoints(); i < Partie.DEFAULT_HP; i++) {
                mat_manche[b.get_id()][i] = null; //blessures
            }
        }
    }
}

class Random_Bot extends Bandit_Bot{

    //choisi un direction random pour bouger
    public Direction choisie_dir(Bandit b){
        Random random = new Random();
        int choice;
        do {
            choice = random.nextInt(4);
        }while(!b.mouvementsPossibles(partie.getTrain()).contains(Direction.values()[choice]));
        return Direction.values()[choice];
    }


    public Random_Bot(int pos, Partie partie) {
        super("Randy", pos, partie);
    }

    @Override
    void target(Train train, LinkedList<Action> acts, Bandit src) {
        return;
    }

    @Override
    List<Action> actions_bot() {
        // remplit une list d'action qui servira dans matrice action
        LinkedList<Action> acts = new LinkedList<>();
        Train train = partie.getTrain();
        for (int i = 0; i < this.get_hitPoints(); i++) {
            Random random = new Random();
            int choice = random.nextInt(4);
            List<Direction> dirs = this.mouvementsPossibles(train);

            if(choice == 0){ //Braquage
                acts.add( new Braquage(this,train));
            }
            else if(choice == 1){ //Mouvement
                acts.add( new Deplacement(this,train , choisie_dir(this)));
            }
            else if(choice == 2){ // tir
                acts.add( new Tir(this,train , choisie_dir(this)) );
            }
            else{//Frappe
                acts.add( new Frappe(this, train));
            }
        }
        for (int i = this.get_hitPoints(); i <Partie.DEFAULT_HP; i++) {
            acts.add(null);
        }
        return acts;
    }
}

class Blood_thirsty_Bot extends Bandit_Bot{

    //list des autres bandits, targets.
    LinkedList<Bandit> targets;

    public Blood_thirsty_Bot(int pos, Partie partie) {

        super("Lucia", pos, partie);
    }

    public void targets_initialisation(){
        //name speaks for itself
        targets = new LinkedList<>();
        for (Joueur i : partie.getJoueurs()) {
            if(!i.getPions().contains(this)) targets.addAll(i.getPions());
        }
        Collections.shuffle(targets);
    }

    public void target(Train train, LinkedList<Action> acts, Bandit src){
        //targets 1 bandit out of all bandits
        //essaye de suivre un bandit et le frapper, remplit la liste d'action de cette maniere
        Bandit banditProche = targets.get(0);
        targets.remove(0);
        targets.add(banditProche);
        //fait des targets circulaire until la liste d'action devient full
        int dist = dist(src,banditProche);
        boolean frappe = true;
        //on calcul la distance entre deux bandits, donc les sources(position +toit) doivent etre updated
        //pour qu'il ne repete pas ses actions s'il a trop d'actions restantes.
        while(acts.size()<this.get_hitPoints()){
            if(dist !=0 ){
                acts.add(new Deplacement(this,train, get_direction(src.position,banditProche.position,
                        src.getToit(),banditProche.getToit()) ) );
                dist--;
            }
            else if(frappe){
                src = banditProche;
                acts.add(new Frappe(this,train));
                frappe = false;
            }
            else{
                target(train,acts,src);
            }
        }
    }

    @Override
    List<Action> actions_bot() {
        //remplit la liste d'action avec soit des vols soit des targets
        LinkedList<Action> acts= new LinkedList<>();
        Train train = this.partie.getTrain();
        //il est greedy until il est le premier, apres il focus sur quelqu'un
        if(this.compte_butins() <= this.targets.get(0).compte_butins()){
            //essaye de trouver un butin
            int pos = this.butinproche_position();

            Wagon[] waggs= train.get_Wagon();
            boolean toit2 = false;
            if(!waggs[pos].loot_toit.isEmpty()){
                toit2 = true;
            }
            int dist = abs_substraction(this.position,pos);
            if(this.getToit()&&!toit2 || toit2 && !this.getToit()){
                // car ici cette dist ne prends pas en compte les toits
                Deplacement dep = new Deplacement(this,train, get_direction(this.position,pos,
                        this.getToit(),toit2) );
                acts.add(dep );
            }
            boolean braque = true;
            while(acts.size()<this.get_hitPoints()){
                if(dist >0 ){
                    acts.add(new Deplacement(this,train, get_direction(this.position,pos,
                            true,true) ) );
                    dist--;
                }
                else if (braque){
                    acts.add(new Braquage(this,train));
                    braque = false;
                }
                else {
                    // si il a finit de chopper un butin il recommence a target des bandits
                    // la fonction remplirait acts ce qui terminerait la boucle while
                    target(train,acts,this);
                }
            }
        }
        else{
            //target quelquun
            target(train,acts,this);
        }
        for (int i = this.get_hitPoints(); i <Partie.DEFAULT_HP; i++) {
            acts.add(null);
        }
        return acts;
    }
}

class Goblin_Bot extends Bandit_Bot{
    public Goblin_Bot(int pos, Partie partie) {
        super("Goblin", pos, partie);
    }



    @Override
    List<Action> actions_bot() {
        LinkedList<Action> acts= new LinkedList<>();
        Train train = this.partie.getTrain();
        target(train,acts,this);
        return acts;
    }

    public void vol(Train train, LinkedList<Action> acts,int position,boolean toit){
        //vol un des bandits d'un des joueurs en tete autree que lui meme
        // rempli la matrice d'action
        int pos = this.butinproche_position();
        Wagon[] waggs= train.get_Wagon();
        boolean toit2 = false;
        if(!waggs[pos].loot_toit.isEmpty()){
            toit2 = true;
        }
        int dist = abs_substraction(position,pos);
        if(toit&&!toit2 || toit2 && !toit){
            // car ici cette dist ne prends pas en compte les toits
            Deplacement dep = new Deplacement(this,train, get_direction(position,pos,
                    toit,toit2) );
            acts.add(dep );
        }
        boolean braque = true;
        while(acts.size()<this.get_hitPoints()){
            if(dist >0 ){
                acts.add(new Deplacement(this,train, get_direction(position,pos,
                        true,true) ) );
                dist--;
            }
            else if (braque){
                acts.add(new Braquage(this,train));
                braque = false;
            }
            else{
                //on creer un bandit pour faciliter l'utilisation de target au lieu de refaire tout target
                //avec pos et toit au lieu de bandit
                Bandit band_pour_target =new Bandit("",pos);
                band_pour_target.setToit(toit2);
                target(train,acts,band_pour_target);
            }
        }
    }

    public List<Joueur> autre_joueur_en_tete(Joueur[] joueurs){
        //rend la liste des joueurs en tete autre que lui meme, ou le deuxieme joueur
        int max = (joueurs[0].pions.contains(this)?joueurs[1].compte_argent():joueurs[0].compte_argent());
        List<Joueur> premiers = new LinkedList<>();
        for (Joueur j : joueurs){
            if(!j.pions.contains(this)){
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
        }
        return premiers;
    }

    public void target(Train train, LinkedList<Action> acts, Bandit src){
        //targets 1 bandit out of all bandits (best bandit of best player)
        //rempli la list acts de cette facon
        List<Joueur> joue = this.autre_joueur_en_tete(this.partie.getJoueurs());
        Collections.shuffle(joue);
        List<Bandit> bestBandit = joue.get(0).pions;
        Collections.shuffle(bestBandit);
        Bandit banditProche = bestBandit.get(0);
        //target le meilleur bandit du meilleur joueur
        int dist = dist(src,banditProche);
        boolean frappe = true, braque = true;
        //on calcul la distance entre deux bandits, donc les sources(position +toit) doivent etre updated
        //pour qu'il ne repete pas ses actions s'il a trop d'actions restantes.
        while(acts.size()<this.get_hitPoints()){
            if(dist !=0 ){
                acts.add(new Deplacement(this,train, get_direction(src.position,banditProche.position,
                        src.getToit(),banditProche.getToit()) ) );
                dist--;
            }
            else if(frappe){
                src = banditProche;
                acts.add(new Frappe(this,train));
                frappe = false;
            }
            else if(braque){
                acts.add(new Braquage(this,train));
                braque = false;
            }
            else{
                vol(train,acts,this.position,this.getToit());
            }
        }
    }
}