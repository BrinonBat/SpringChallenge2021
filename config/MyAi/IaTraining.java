import java.util.*;
import java.io.*;
import java.math.*;
public class IaTraining{

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        Game game = new Game();

        int numberOfCells = in.nextInt();
        for (int i = 0; i < numberOfCells; i++) {
            int index = in.nextInt();
            int richness = in.nextInt();
            int neigh0 = in.nextInt();
            int neigh1 = in.nextInt();
            int neigh2 = in.nextInt();
            int neigh3 = in.nextInt();
            int neigh4 = in.nextInt();
            int neigh5 = in.nextInt();
            int neighs[] = new int[] { neigh0, neigh1, neigh2, neigh3, neigh4, neigh5 };
            Cell cell = new Cell(index, richness, neighs);
            game.board.add(cell);
        }
    
        long total_duration=0;
        int nb_turns=0;
        
        while (true) {
            long startTime=System.currentTimeMillis();
            ///////////récupération des données
            game.day = in.nextInt();
            game.nutrients = in.nextInt();
            game.mySun = in.nextInt();
            game.myScore = in.nextInt();
            game.opponentSun = in.nextInt();
            game.opponentScore = in.nextInt();
            game.opponentIsWaiting = in.nextInt() != 0;
            game.trees.clear();
            game.li_ally_trees.clear();
            game.li_ennemy_trees.clear();
            int numberOfTrees = in.nextInt();

            
            //récupération des cellules et des arbres
            for (int i = 0; i < numberOfTrees; i++) {
                int cellIndex = in.nextInt(); // location of this tree
                int size = in.nextInt(); // size of this tree: 0-3
                boolean isMine = in.nextInt() != 0; // 1 if this is your tree
                boolean isDormant = in.nextInt() != 0; // 1 if this tree is dormant
                Tree tree = new Tree(cellIndex, size, isMine, isDormant,game);
                game.trees.add(tree);
                if(isMine) game.li_ally_trees.add(tree);
                else game.li_ennemy_trees.add(tree);
            }
    
            //prise de décision
            game.possibleActions.clear();
            int numberOfPossibleActions = in.nextInt();
            System.err.println(" cette ligne éait ignorée "+in.nextLine());
            for (int i = 0; i < numberOfPossibleActions; i++) {
                String possibleAction = in.nextLine();
                game.possibleActions.add(Action.parse(possibleAction));
            }
            
            Action action = game.getNextAction();

            //retourne la durée que ça à pris
            long duration=System.currentTimeMillis()-startTime;
            System.err.println("le tour à duré "+duration+" ms");
            total_duration+=duration;
            nb_turns++;
            System.err.println("les tours ont en moyenne duré "+(total_duration/nb_turns));

            //envoie l'action
            System.out.println(action);
        }
    }
}

class SortActions implements Comparator<Action>{
    //utilisé pour le tri descendant par coef
    public int compare(Action a,Action b){
        if(a.score<b.score) return 1;
        if(b.score<a.score) return -1;
        return 0;
    }
}

class Tree{

    int cellIndex;
    int size;
    boolean isMine;
    boolean isDormant;

    public Tree(int cellIndex, int size, boolean isMine, boolean isDormant,Game game) {
        this.cellIndex = cellIndex;
        this.size = size;
        this.isMine = isMine;
        this.isDormant = isDormant;
    }
    
    public Tree(Tree tree){
        this.cellIndex = tree.cellIndex;
        this.size = tree.size;
        this.isMine = tree.isMine;
        this.isDormant = tree.isDormant;
    }
    
    /*
    public int dist(Game game,Tree target){
        int from_me=0;
        int from_target=0;
        Cell my_cell=game.board.get(cellIndex);
        Cell target_cell=game.board.get(target.cellIndex);
        int[] my_neighbours=my_cell.neighbours;
        int[] target_neighbours=target_cell.neighbours;
        for(;from_me<=4;from_me++){
            int[]
        }
    }
    */
}

class Cell {
    int index;
    int richess;
    int[] neighbours;
   // Tree tree;

    public Cell(int index, int richess, int[] neighbours) {
        this.index = index;
        this.richess = richess;
        this.neighbours = neighbours;
        
     //   this.tree=null;
    }
    public Cell(Cell cell){
        this.index = cell.index;
        this.richess = cell.richess;
        this.neighbours = cell.neighbours;
    }
    /*
    public Cell getNeighbour(List<Cell> board,int dir,int dist){
        Cell result=board.get(neighbours[dir]);
        for(;dist>1;dist--){
            result=board.get(result.neighbours)
        }
    }
    */
}

class Action {
    static final String WAIT = "WAIT";
    static final String SEED = "SEED";
    static final String GROW = "GROW";
    static final String COMPLETE = "COMPLETE";
    float score;

    static Action parse(String action) {
        String[] parts = action.split(" ");
        switch (parts[0]) {
        case WAIT:
            return new Action(WAIT);
        case SEED:
            return new Action(SEED, Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
        case GROW:
        case COMPLETE:
        default:
            return new Action(parts[0], Integer.valueOf(parts[1]));
        }
    }

    String type;
    Integer targetCellIdx; // utilisé pour seed
    Integer sourceCellIdx; //cible de l'action

    public Action(String type, Integer sourceCellIdx, Integer targetCellIdx) {
        this.type = type;
        this.targetCellIdx = targetCellIdx;
        this.sourceCellIdx = sourceCellIdx;
    }

    public Action(String type, Integer targetCellIdx) {
        this(type, null, targetCellIdx);
    }

    public Action(String type) {
        this(type, null, null);
    }

    @Override
    public String toString() {
        if (type == WAIT) {
            return Action.WAIT;
        }
        if (type == SEED) {
            return String.format("%s %d %d", SEED, sourceCellIdx, targetCellIdx);
        }
        return String.format("%s %d", type, targetCellIdx);
    }

    Tree TargetHasTree(Game game){
        for(int i=0;i<game.trees.size();i++){
            if(game.trees.get(i).cellIndex==targetCellIdx) return game.trees.get(i);
        }
        return null;
    }

    //retourne l'évaluation du coup
    void setScore(Game game){
        int cost=0;
        Game next_turn_state=new Game(game);
        for (Tree t : game.li_ally_trees) {
            System.err.println(t.cellIndex+" a un arbre de taille "+t.size);
        }
        switch(this.type){
            case(WAIT): System.err.println("============= "+this.type+" =============");break;
            case(SEED):{

                System.err.println("============= "+this.type+" "+this.sourceCellIdx+" "+targetCellIdx+" =============");

                //ajout de l'arbre au jeu
                boolean isDormant=true;
                Tree new_tree=new Tree(targetCellIdx,0,true,isDormant,next_turn_state);
                next_turn_state.trees.add(new_tree);
                next_turn_state.li_ally_trees.add(new_tree);

                //calcul du coût
                for(int i=0; i<game.li_ally_trees.size();i++){
                    if(game.li_ally_trees.get(i).size==0) cost++;
                }
                next_turn_state.mySun-=cost;
                
                break;
            }
            case(COMPLETE):{


                System.err.println("============= "+this.type+" "+this.targetCellIdx+" =============");

                //calcul du coût et du gain
                cost=4;
                float value= (game.board.get(targetCellIdx).richess-1)*2;
                value=value+game.nutrients;

                //mise à jour de l'état suivant
                next_turn_state.mySun-=cost;
                next_turn_state.myScore+=value;
                next_turn_state.nutrients--;
                next_turn_state.trees.remove(TargetHasTree(game));
                break;
            } 
            case(GROW):{

                System.err.println("============= "+this.type+" "+this.targetCellIdx+" =============");
                
                //modification de l'arbre
                Tree modified_tree=TargetHasTree(next_turn_state);
                modified_tree.isDormant=true;
                modified_tree.size++;

    
                //calcul du coût ((taille!-taille)+nb_arbres_nouvelle_taille)
                switch(modified_tree.size){
                    case(0): cost=0; break;
                    case(1): cost=1; break;
                    case(2): cost=3; break;
                    case(3): cost=7; break;
                    default: System.err.println("TAILLE NON RECONNUE"); break;
                }
                for(int i=0; i<game.li_ally_trees.size();i++){
                    if(game.li_ally_trees.get(i).size==modified_tree.size) cost++;
                }
                next_turn_state.mySun-=cost;

                break;

            }
            default:{
                System.err.println("erreur lors du calcul du coût de l'action "+this);
                score=-1;
            }
              
        }
        
        //evaluation de l'état resultant
        score=next_turn_state.evaluate();
       // if(this.type==GROW){

        //}
    }

}

class Game{
    int day;
    int nbMaxDay;
    int nutrients;
    List<Cell> board;
    List<Action> possibleActions;
    List<Tree> trees;
    List<Tree> li_ally_trees;
    List<Tree> li_ennemy_trees;
    int mySun, opponentSun;
    int myScore, opponentScore;
    boolean opponentIsWaiting;

    public Game() {
        board = new ArrayList<>();
        possibleActions = new ArrayList<>();
        trees = new ArrayList<>();
        li_ally_trees = new ArrayList<>();
        li_ennemy_trees = new ArrayList<>();
        nbMaxDay=24;
    }

    //constructeur par copie
    public Game(Game game){
        this.day=Integer.valueOf(game.day);
        this.nbMaxDay=Integer.valueOf(game.nbMaxDay);
        this.nutrients=Integer.valueOf(game.nutrients);
        this.board=new ArrayList<>();
        for (Cell cell : game.board) {
            board.add(new Cell(cell));
        }
        this.possibleActions=new ArrayList<>(game.possibleActions);
        this.trees=new ArrayList<>();
        this.li_ally_trees=new ArrayList<>();
        this.li_ennemy_trees=new ArrayList<>();
        for (Tree tree : game.trees) {
            Tree t=new Tree(tree);
            trees.add(t);
            if(t.isMine) li_ally_trees.add(t);
            else li_ennemy_trees.add(t);
        }
        this.mySun=Integer.valueOf(game.mySun);
        this.opponentSun=Integer.valueOf(game.opponentSun);
        this.myScore=Integer.valueOf(game.myScore);
        this.opponentScore=Integer.valueOf(game.opponentScore);
        this.opponentIsWaiting=game.opponentIsWaiting;
    }

    Action getNextAction() {
        if(day<20){
            for(int i=0;i<possibleActions.size();i++){
                possibleActions.get(i).setScore(this);
            }
        }
        else{
            //TODO methode predict
            for(int i=0;i<possibleActions.size();i++){
                possibleActions.get(i).setScore(this); // sera remplacée par une methode de prediction
            }
        }

        //tri des actions en fonction du score
        Collections.sort(possibleActions, new SortActions());
        for(int i=0;i<possibleActions.size();i++){
            System.err.println(possibleActions.get(i)+" : "+possibleActions.get(i).score);
        }

        //envoi du meilleur coup
        return possibleActions.get(0);
    }

    public float evaluate(){
        //définition de coefficients.
        int COEF_TREES=day-1; //valeur du positionnement et niveau des arbres
        int COEF_SCORE=day; //score officiel obtenu
        //int COEF_NB_ACTIONS=nbMaxDay; //nombre probable de coups joués au prochain jour. ERASED
        int COEF_GENERATED_SUN=nbMaxDay-day;


        //CHAQUE SCORE DOIT AVOIR LE MEME MIN ET LE MEME MAX

        //prise en compte de l'évaluation de positionnement des arbres (level,valeur_case,nombre de fois ombragé par semaine,)
        float trees_score=this.evaluateTreePosition();

        
        //prise en compte du nombre de couts possibles (soleil généré, cout de l'action,)
        //soleil généré
        int generated_sun=0;
        int[] nb_trees_level= new int[4];
        for(int i=0;i<li_ally_trees.size();i++){
            int size=li_ally_trees.get(i).size;
            nb_trees_level[size]++;
            generated_sun+=size;
        }
        //{0-100}
        int sun_next_day=this.mySun+generated_sun;

        //affichage de verification
        if (!( sun_next_day<=100 && generated_sun<=100 && trees_score<=100)){
            System.err.println("!!!!!!!!!!!!!!! DEPASSEMENT DE LIMITE !!!!!!!!!!!!");
            System.err.println(myScore+" "+generated_sun+" "+trees_score);
            System.err.println("sun next day "+sun_next_day);
        }
       //calcul et envoi du score
        float score=COEF_SCORE*this.myScore + COEF_GENERATED_SUN*generated_sun*4 + COEF_TREES*trees_score ;
        System.err.println("score weight: "+myScore*COEF_SCORE);
        System.err.println("generated_sun weight: "+COEF_GENERATED_SUN*generated_sun);
        System.err.println("trees_score weight : "+COEF_TREES*trees_score);
        System.err.println("total :"+score);
        return score;
    }

    private float evaluateTreePosition(){
        float tree_global_score=0;

        //initialisation des coefs
        int COEF_LEVEL_VALUE=Math.max(nbMaxDay/2,day);
        int COEF_CELL_VALUE=nbMaxDay;
        int COEF_NB_SHADES=1;

        //calcul de la valeur de chaque arbre 
        for(int tree_num=0;tree_num<li_ally_trees.size();tree_num++){
            Tree tree=li_ally_trees.get(tree_num);

            //level /!\ dépend du tour (augmente au fur et à mesure) {0-4}
            int level_value=tree.size;

            //valeur de la case {0-24}
            int cell_value=(this.board.get(tree.cellIndex).richess-1*2)+this.nutrients;

            //nb_shades /!\ dépend du tour (diminue au fur et à mesure) {0-6}
            int nb_shades=0; // implémenté plus tard

            //{0-672}
            int tree_score=COEF_CELL_VALUE*cell_value + COEF_NB_SHADES*nb_shades + COEF_LEVEL_VALUE*level_value;
            tree_global_score+=tree_score;
        }
        //{0-100000}
        //normalisation par 10 0000
        tree_global_score=tree_global_score/1000;
        return tree_global_score;
    }

}