import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.codingame.gameengine.runner.CommandLinePlayerAgent;
import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.AgentData;
import com.codingame.gameengine.runner.simulate.GameResult;

public class Spring2021 {

    static String[] DEFAULT_AI = new String[] {
        "python3", "config/Boss.py"
    };
    static String[] BOSS_WOOD2 = new String[] {
        "python3", "config/level1/Boss.py"
    };
    static String[] BOSS_WOOD1 = new String[] {
        "python3", "config/level2/Boss.py"
    };
    static String[] MY_AI = new String[] {
        "java", "config/MyAi/IaEvaluatingBoard.java"
    };
    public static void main(String[] args) throws IOException, InterruptedException {

        boolean repeat=true;
        int nb_game_total=500;
        int total_score=0;
        if(repeat){
            int nb_win=0;
            int nb_lose=0;
            List<String> bs=new ArrayList<>();
            bs.add("null");
            bs.add(" ");
            for(int nb_game=1;nb_game<=nb_game_total;nb_game++){
                System.out.println("partie numero "+nb_game);
                GameResult result=launchGame(repeat);
                /*
                List<String> output=result.outputs.get("0"); // 0 = bot jeu ; 1 = bot perso
                List<String> errors=result.errors.get("0");
               // System.out.println(output);
               // System.out.println(errors);
                System.out.println(result.summaries);

                
                for(int i=0;i<result.summaries.size();i++){

                    System.out.println(" SUMMARIES ---------------------- \n"+result.summaries.get(i));
                    System.out.println(" OUTPUTS ---------------------- \n"+output.get(i));
                    System.out.println(" ERRORS ---------------------- \n"+errors.get(i));
                }
                */
                for(int i=0;i<result.agents.size();i++){
                    System.out.println(result.agents.get(i).name+" -> "+result.scores.get(i));
                    if(i==1) total_score+=result.scores.get(i);
                }
                if(result.scores.get(1)>result.scores.get(0))nb_win++;
                else nb_lose++;
            }
            System.out.println(nb_win+" wins & "+nb_lose+" loses");
            float moy=(float)total_score/nb_game_total;
            System.out.println("moyenne :"+moy);
        }
        else launchGame(repeat);
    }

    public static GameResult launchGame(boolean repeat) throws IOException, InterruptedException {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setLeagueLevel(3);
        Properties gameParameters = new Properties();
        gameRunner.setGameParameters(gameParameters);

        gameRunner.addAgent(
            BOSS_WOOD1,
            "Tororo",
            "https://static.codingame.com/servlet/fileservlet?id=61910307869345"
        );
        gameRunner.addAgent(
            MY_AI,
            "My Ai",
            "https://static.codingame.com/servlet/fileservlet?id=61910307869345"
        );

        long leftLimit = 1L;
        long rightLimit = 10L;
        long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
        gameRunner.setSeed(generatedLong);

        if(repeat){
            GameResult result=gameRunner.simulate();
            return result;
        }
        gameRunner.start(8888);
        return null;
        
    }
}
