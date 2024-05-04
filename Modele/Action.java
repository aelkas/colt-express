package Modele;

/** Classe Action
 * Permet d'encapsuler les différente actions planifié dans un même type d'objet
 * @param <T> moralement peut-être un Bandit ou un Movable
 */
public abstract class Action<T> {
    protected Train train;
    protected T acteur;

    Action(T act , Train t){
        acteur = act;
        train = t;
    }

    /** Lance l'action associée
     * @return Un message contextuelle à afficher dans la Vue
     */
    public abstract String executer();
}


class Braquage extends Action<Bandit>{

    public Braquage(Bandit b , Train T){
        super(b,T);
    }

    @Override
    public String executer() {
        return acteur.braque(train);
    }


    @Override
    public String toString(){
        return "Braque";
    }


}


/**
 * Sous classe abstraite pour gérer les actions nécéssitant le choix d'une direction
 */
abstract class ActionDir extends Action{
    protected final Direction dir;
    ActionDir(Object act, Train t,Direction d) {
        super(act, t);
        dir = d;
    }
    public Direction getDir(){return dir;}
}
class Deplacement extends ActionDir{

    public Deplacement(Movable b , Train T , Direction d){
        super(b,T,d);
    }

    @Override
    public String executer() {
        return ((Movable)acteur).move(train, dir);
    }

    @Override
    public String toString() {return "Deplacement " + dir;}
}

class Tir extends ActionDir{

    public Tir(Bandit b , Train T , Direction d){
        super(b,T,d);
    }

    @Override
    public String executer() {
        return ((Bandit) acteur).tir(train, dir);
    }

    @Override
    public String toString() {return "Tir " + dir;}
}


class Frappe extends Action<Bandit>{

    public Frappe(Bandit b, Train T){
        super(b,T);
    }

    @Override
    public String executer() {
        return acteur.frappe(train);
    }

    @Override
    public String toString() {return "Frappe";}
}


