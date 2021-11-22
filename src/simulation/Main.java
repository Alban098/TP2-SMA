package simulation;

import engine.rendering.Engine;
import engine.rendering.ILogic;
 
public class Main {
 
    public static void main(String[] args) {
        try {
            ILogic logic = new Simulation();
            Engine engine = new Engine("SMA", 600, 480, true, logic);
            engine.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}