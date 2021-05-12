/////////////////////////////////////////////// 1 //////////////////////////////////////////////////
public float evaluate(){
    //définition de coefficients.
    int COEF_TREES=day-1; //valeur du positionnement et niveau des arbres
    int COEF_SCORE=day; //score officiel obtenu
    int COEF_NB_ACTIONS=nbMaxDay; //nombre probable de coups joués au prochain jour
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
        generated_sun+=size;;
    }

    //{0-100}
    int sun_next_day=this.mySun+generated_sun;
    
    //{0-50}
    float nb_actions=nb_trees_level[1]+nb_trees_level[2]+nb_trees_level[3]*2+(nb_trees_level[1]*6+nb_trees_level[2]*12+nb_trees_level[3]*18);
    float total_actions_cost=0;
    //calcule de la moyenne d'un coup
    int grow_cost=nb_trees_level[1]+nb_trees_level[2]*3+nb_trees_level[3]*7;
    int seed_cost=nb_trees_level[0]*(nb_trees_level[1]*6+nb_trees_level[2]*12+nb_trees_level[3]*18);
    total_actions_cost=grow_cost+seed_cost+nb_trees_level[3]*4;
    float median_cost=total_actions_cost/nb_actions;
    System.err.println("median cost :"+median_cost);
    float quantity_of_actions=sun_next_day/median_cost;

    //normalisation
    quantity_of_actions=quantity_of_actions*2;

    //affichage de verification
    if (!(quantity_of_actions<=100 && sun_next_day<=100 && generated_sun<=100 && trees_score<=100)){
        System.err.println("!!!!!!!!!!!!!!! DEPASSEMENT DE LIMITE !!!!!!!!!!!!");
        System.err.println(myScore+" "+quantity_of_actions+" "+generated_sun+" "+trees_score);
        System.err.println("sun next day"+sun_next_day);
    }
   //calcul et envoi du score
    float score=COEF_SCORE*this.myScore + COEF_NB_ACTIONS*quantity_of_actions + COEF_GENERATED_SUN*generated_sun*4 + COEF_TREES*trees_score ;
    System.err.println("score weight: "+myScore*COEF_SCORE);
    System.err.println("nb_coup Weight: "+COEF_NB_ACTIONS*quantity_of_actions);
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
