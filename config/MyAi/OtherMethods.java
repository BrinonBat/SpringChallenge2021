import java.util.*;
import java.io.*;
import java.math.*;
class Player{

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
            int numberOfTrees = in.nextInt();

            
            //récupération des cellules et des arbres
            for (int i = 0; i < numberOfTrees; i++) {
                int cellIndex = in.nextInt(); // location of this tree
                int size = in.nextInt(); // size of this tree: 0-3
                boolean isMine = in.nextInt() != 0; // 1 if this is your tree
                boolean isDormant = in.nextInt() != 0; // 1 if this tree is dormant
                Tree tree = new Tree(cellIndex, size, isMine, isDormant,game);
                game.trees.add(tree);
            }
    
            //prise de décision
            game.possibleActions.clear();
            int numberOfPossibleActions = in.nextInt();
            System.err.println(" cette ligne éait ignorée: "+in.nextLine());
            for (int i = 0; i < numberOfPossibleActions; i++) {
                String possibleAction = in.nextLine();
                game.possibleActions.add(Action.parse(possibleAction));
            }
            
            if(game.day==23) System.err.println(" mon score est "+game.myScore+" contre "+game.opponentScore);
            Action action = game.getNextAction();

            //retourne la durée que ça à pris
            System.err.println("le tour à duré "+(System.currentTimeMillis()-startTime));
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
    Tree tree;

    public Cell(int index, int richess, int[] neighbours) {
        this.index = index;
        this.richess = richess;
        this.neighbours = neighbours;
        
        this.tree=null;
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
        int cost;
        System.err.println("my type is "+this.type);
        switch(this.type){
            case(WAIT):{
                score=0;
                break;
            }
            case(SEED):{
                if (game.day>20) score=0;
                else{

                }
                break;
            }
            case(COMPLETE):{
                cost=4;
                float value= (game.board.get(targetCellIdx).richess-1)*2;
                value=value+game.nutrients;
                score=(value/cost);
                break;
            } 
            case(GROW):{

                //gather trees data
                Tree target=null;
                int nb_level_1=0;
                int nb_level_2=0;
                int nb_level_3=0;
                //List<Tree> my_trees=new ArrayList<>();
                for(int i=0;i<game.trees.size();i++){
                    if(game.trees.get(i).cellIndex==targetCellIdx) target=game.trees.get(i);
                    if(game.trees.get(i).isMine){ //on ne compte que nos arbres
                        //my_trees.add(game.trees.get(i));
                        switch(game.trees.get(i).size){
                            case(1):{
                                nb_level_1++;
                                break;
                            }
                            case(2):{
                                nb_level_2++;
                                break;
                            }
                            case(3):{
                                nb_level_3++;
                                break;
                            }
                            default:{
                                System.err.println("arbre incorrect, taille dépassée");
                                score=-1;
                            }
                        }
                    } 
                    
                }

                //calcule de la moyenne d'un coup
                int nb_actions=nb_level_1+nb_level_2+nb_level_3*2;
                System.err.println("nb_level_1 :"+nb_level_1);
                System.err.println("nb_level_2 :"+nb_level_2);
                System.err.println("nb_level_3 :"+nb_level_3);
                int actions_total_cost=nb_level_1+nb_level_2*3+nb_level_3*7+nb_level_3*4;
                System.err.println("actions_total_cost :"+actions_total_cost);
                System.err.println("nb_actions :"+nb_actions);
                float median_cost=actions_total_cost/nb_actions;

                //calcule du nombre de coups à venir
                float nb_turns_this_day=game.mySun/median_cost;
                float nb_turns_per_day=(nb_level_3*3+nb_level_2*2+nb_level_1)/median_cost;
                if(nb_turns_per_day>6) nb_turns_per_day=6;
                if(nb_turns_this_day>6) nb_turns_this_day=6;
                float nb_future_turns=nb_turns_this_day+(nb_turns_per_day*(game.nbMaxDay-game.day)); 
                
                float value=game.board.get(targetCellIdx).richess;
                nb_future_turns+=value;
                switch(target.size){
                    case(1):{
                        cost=nb_level_2*3;
                        score=nb_future_turns-cost;
                        System.err.println("nb_future_turns:"+nb_future_turns+" cost:"+cost);
                        break;
                    }
                    case(2):{
                        cost=nb_level_3*7;
                        score=nb_future_turns-cost;
                        System.err.println("nb_future_turns:"+nb_future_turns+" cost:"+cost);
                        break;
                    }
                    default:{
                        System.err.println("arbre incorrect, taille dépassée");
                       score=-1;
                    }
                }
                if(game.nutrients>0) score--;
                break;

            }
            default:{
                System.err.println("erreur lors du calcul du coût de l'action "+this);
                score=-1;
            } 
        }
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
        nbMaxDay=6;
    }

    //constructeur par copie
    public Game(Game game){
        this.day=game.day;
        this.nbMaxDay=game.nbMaxDay;
        this.nutrients=game.nutrients;
        this.board=game.board;
        this.possibleActions=game.possibleActions;
        this.trees=game.trees;
        this.li_ally_trees=game.li_ally_trees;
        this.li_ennemy_trees=game.li_ennemy_trees;
        this.mySun=game.mySun;
        this.opponentSun=game.opponentSun;
        this.myScore=game.myScore;
        this.opponentScore=game.opponentScore;
        this.opponentIsWaiting=game.opponentIsWaiting;
    }

    Action getNextAction() {
        for(int i=0;i<possibleActions.size();i++){
            possibleActions.get(i).setScore(this);
        }
        Collections.sort(possibleActions, new SortActions());
        for(int i=0;i<possibleActions.size();i++){
            System.err.println(possibleActions.get(i)+" : "+possibleActions.get(i).score);
        }

        // GROW cellIdx | SEED sourceIdx targetIdx | COMPLETE cellIdx | WAIT <message>
        return possibleActions.get(0);
    }
    /*
    void setShades(){
        for(int i=0;i<trees.size();i++){
            Tree t=trees.get(i);
            for(int dir=0;dir<6;dir++){
                if()
            }
        }
    }
    */

}
