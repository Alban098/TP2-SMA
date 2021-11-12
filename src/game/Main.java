package game;

import engine.Engine;
import engine.ILogic;
 
public class Main {
 
    public static void main(String[] args) {
        try {
            ILogic logic = new Simulation();
            Engine engine = new Engine("GAME", 600, 480, true, logic);
            engine.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}