package Modele;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Bandit extends Personne implements Movable, Hitable{

    private boolean toit;
    private int ammo;
    private final double precision;

    private int hitPoints;
    private final List<Butin> poches = new LinkedList<>();


    //Sprites
    private static final String[] sprites = {"../Vue/Images/banditHomme.png","../Vue/Images/banditFemme.png"};


    public Bandit(String name,int pos){
        super(name);
        id = current_id_bandit++ ;
        position = pos;
        ammo = Partie.NB_MUNITIONS;
        toit = true;
        hitPoints = Partie.DEFAULT_HP;
        precision = Partie.DEFAULT_PRECISION;

        Random r = new Random();
        sprite = sprites[r.nextInt(sprites.length)];
    }


    public void ajoute_butin(Butin b){
        poches.add(b);
    }

    /** Drop Un butin du bandit aléatoirement au sol à sa position
     * @param w le wagon contenant le bandit
     */
    public void drop_butin(Wagon w){
        assert this.position == w.position;
        if (!this.poches.isEmpty()){
            Random random = new Random();
            int randomIndex = random.nextInt(this.poches.size());
            Butin dropped_loot = this.poches.get(randomIndex);
            this.poches.remove(randomIndex);
            if(toit)
                w.loot_toit.add(dropped_loot);
            else
                w.loot_int.add(dropped_loot);
        }
    }

    /**
     * @see Hitable
     */
    @Override
    public void est_vise(Wagon wagon){
        if(this.hitPoints>2)
            this.hitPoints--;
        drop_butin(wagon);
    }


    /** Méthode qui permet au bandit de braquer un passager choisie aléatoirement à sa position ou de voler le magot
     * ou un butin laissé au sol
     * @param T une référence vers le train qui contient le bandit
     * @return Un message contextuelle qui varie suivant le résultat de l'action
     */
    public String braque(Train T) {
        Random r = new Random();
        Wagon wagon_actuelle = T.get_Wagon()[position];
        int index_butin;
        if (toit) {
            if(!wagon_actuelle.loot_toit.isEmpty()) {
                index_butin = r.nextInt(wagon_actuelle.loot_toit.size());
                poches.add(wagon_actuelle.loot_toit.get(index_butin));
                wagon_actuelle.loot_toit.remove(index_butin);
                return this + " récupère un butin au sol.";
            }
            else{
                return this + " n'a personne a braqué perché sur le toit, dommage."; //toit vide
            }
        }
        else {
            if (position == 0 && ((Locomotive) T.get_Wagon()[0]).magot_dispo()) {
                //vole le magot
                ((Locomotive) T.get_Wagon()[0]).magot_vole(this);
                return this + " a choppé le magot.";
            }
            else if ((position==0 || wagon_actuelle.liste_passagers().isEmpty() || r.nextBoolean()) && !wagon_actuelle.loot_int.isEmpty()) {
                //prend un loot au sol
                index_butin = r.nextInt(wagon_actuelle.loot_int.size());
                poches.add(wagon_actuelle.loot_int.get(index_butin));
                wagon_actuelle.loot_int.remove(index_butin);
                return this + " récupère un butin au sol.";
            }
            else if(!wagon_actuelle.liste_passagers().isEmpty()){
                //Braque un passager
                index_butin = r.nextInt(wagon_actuelle.liste_passagers().size());
                wagon_actuelle.liste_passagers().get(index_butin).cede(this);
                return this + " braque un passager.";
            }
            else{
                return this + " n'a rien a braquer.";
            }
        }

    }


    /**
     * @see Movable
     */
    @Override
    public List<Direction> mouvementsPossibles(Train t) {
       return t.mouvementPossibles(true,position,toit);
    }

    /**
     * @see Movable
     */
    @Override
    public String move(Train T , Direction d){
        if(!this.mouvementsPossibles(T).contains(d)){
            System.out.println("Mouvement Invalide");
            return this + " a essayé de sauter du train , mauvaise idée.";
        }
        Wagon[] wagons = T.get_Wagon();
        wagons[position].enleve_personne(this);
        if(d.dir()==-Partie.NB_WAGON) toit = true;
        else if (d.dir()==Partie.NB_WAGON) toit = false;
        else {
            position += d.dir();
        }
        wagons[position].ajoute_personne(this,toit);
        return this + " se déplace.";
    }

    /** Permet au bandit de tirer dans une des quatres directions , tire un bandit à la position visé au hasard et lui fait droper un butin
     * @param train une référence vers le train contenant le bandit
     * @param dir la direction du tir
     * @return un message contextuelle suivant le résultat de l'action
     */
    public String tir(Train train, Direction dir) {
        Random random = new Random();
        if(this.ammo>0) {
            assert mouvementsPossibles(train).contains(dir);
            this.ammo--;
            int d = dir.dir();
            if (d == -Partie.NB_WAGON) {  // tir vers le toit d'un wagon du bas
                Wagon current_wagg = train.get_Wagon()[this.position];
                List<Bandit> list = current_wagg.toit;
                if(!list.isEmpty()) {
                    int randomIndex = random.nextInt(list.size());
                    Bandit b_cible = list.get(randomIndex);
                    b_cible.est_vise(current_wagg);
                    b_cible.setTargeted(true);
                    return this + " tire sur " + b_cible;
                }
                return this + " tire vers le toit du wagon.";
            }
            else if(d == Partie.NB_WAGON || !toit){ //tir vers un wagon : tir venant du toit ou tir venant d'un coté
                int numWagon = this.position + (d<0? d :  d%Partie.NB_WAGON);
                Wagon current_wagg = train.get_Wagon()[numWagon];
                List<Passager> passagers_cibles = current_wagg.liste_passagers();
                List<Bandit> bandits_cibles = current_wagg.liste_bandits_int();
                if ((random.nextDouble() > precision || bandits_cibles.isEmpty())&& !passagers_cibles.isEmpty()) { //Le bandit rate son tire et touche un passager
                    int randomIndex = random.nextInt(passagers_cibles.size());
                    Passager p = passagers_cibles.get(randomIndex);
                    p.est_vise(current_wagg);
                    p.setTargeted(true);
                    return this + "tir sur un passager.";
                }
                else if(current_wagg.interieur.isEmpty()){
                    return this + "a tire sur un wagon vide, dommage!";
                }else if(bandits_cibles.isEmpty()) {
                    return this+ "tir sur le marchall! Attention à lui.";
                }
                else{ //Le bandit touche bien un autre bandit
                    int randomIndex = random.nextInt(bandits_cibles.size());
                    Bandit b_cible = bandits_cibles.get(randomIndex);
                    b_cible.est_vise(current_wagg);
                    b_cible.setTargeted(true);
                    return this + " tire sur " + b_cible;
                }

            }
            else { //tir d'un toit vers un autre
                Wagon current_wagg = train.get_Wagon()[this.position + d];
                List<Bandit> list = current_wagg.toit;
                while (list.isEmpty() && !(current_wagg.position == 0 || current_wagg.position == Partie.NB_WAGON - 1)) {
                    current_wagg = train.get_Wagon()[current_wagg.position + d];
                    list = current_wagg.toit;
                }
                if (!list.isEmpty()) {
                    int size_bound = list.size();
                    int randomIndex = random.nextInt(size_bound);
                    Bandit bandit = list.get(randomIndex);
                    bandit.est_vise(current_wagg);
                    bandit.setTargeted(true);
                    return this + " tire sur " + bandit;
                }
                return this + " tire vers l'" + dir;
            }
        }
        return "*Click* ,*Click*";   //out of ammo
    }

    /** Permet au bandit de farpper un autre bandit choisie aléatoirement sur sa position et de lui faire dropé un butin
     * @param train une référence vers le train qui contient le bandit
     * @return une message contextuelle suivant le résultat de l'action
     */
    public String frappe(Train train) {
        Random rand = new Random();
        Wagon w = train.get_Wagon()[position];
        Bandit vise = null;
        if(toit){ //Sur le toit
            if(w.toit.size()>1){
                w.toit.remove(this);
                int rand_index = rand.nextInt(w.toit.size());
                vise = w.toit.get(rand_index);
                w.toit.add(this);
            }
        }
        else { //Dans une cabine
            List<Bandit> bandits_cibles = w.liste_bandits_int();
            bandits_cibles.remove(this);
            if(!bandits_cibles.isEmpty()) {
                int rand_index = rand.nextInt(bandits_cibles.size());
                vise = bandits_cibles.get(rand_index);
            }
        }
        if(vise != null) {
            vise.est_vise(w);
            vise.setTargeted(true);
            return this + " frappe " + vise;
        }
        else{
            return this + " brasse de l'air";
        }
    }

    /** Fonction appélé lorsque le bandit et le marshall sont au même endroit, fait furie le bandit vers le toit en lui faisant perdre 1HP
     * @param w le wagon contenant le bandit et le marshall
     * @return Un message à afficher dans la Vue
     */
    public String fuit_marshall(Wagon w) {

        if(this.hitPoints>2) {
            this.hitPoints--;
        }
        this.drop_butin(w);
        this.toit = true;
        w.getInterieur().remove(this);
        w.getToit().add(this);

        return "*PAN*, "+ this + " a pris la fuite en se prenant une balle du Marshall.";
    }


    /**Compte la valeur totale des butins du bandit
     * @return un entier réprésenter la valeur totale de {@code poche}
     */
    public int compte_butins(){
        int somme = 0;
        for (Butin b : poches){
            somme += b.valeur();
        }
        return somme;
    }

    /*Getters and Setters*/
    public boolean getToit(){return toit;}
    public int get_hitPoints(){
        return this.hitPoints;
    }
    public int get_ammo(){return this.ammo;}

    public List<Butin> getPoches() { return this.poches;}

    public void setToit(boolean toit) {
        this.toit = toit;
    }


}