import java.io.File;
import java.io.IOException;
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
        "java", "config/MyAi/IaTraining.java"
    };
    public static void main(String[] args) throws IOException, InterruptedException {

        boolean repeat=true;
        if(repeat){
            int nb_win=0;
            int nb_lose=0;
            for(int nb_game=0;nb_game<100;nb_game++){
                GameResult result=launchGame(repeat);
                for(int i=0;i<result.agents.size();i++){
                    System.out.println(result.agents.get(i).name+" -> "+result.scores.get(i));
                }
                if(result.scores.get(1)>result.scores.get(0))nb_win++;
                else nb_lose++;
            }
            System.out.println(nb_win+" wins & "+nb_lose+" loses");
        }
        else launchGame(repeat);
    }

    public static GameResult launchGame(boolean repeat) throws IOException, InterruptedException {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setLeagueLevel(3);
        Properties gameParameters = new Properties();
        gameRunner.setGameParameters(gameParameters);

        gameRunner.addAgent(
            DEFAULT_AI,
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
