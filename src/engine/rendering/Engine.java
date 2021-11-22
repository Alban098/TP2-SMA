package engine.rendering;

import engine.utils.MouseInput;
import engine.utils.Timer;

public class Engine implements Runnable {

    public static final int TARGET_FPS = 60;
    public static final int TARGET_UPS = 60;

    private final Window window;

    private final Timer timer;

    private final ILogic gameLogic;

    private final MouseInput mouseInput;

    public Engine(String windowTitle, int width, int height, boolean vSync, ILogic gameLogic) {
        window = new Window(windowTitle, width, height, vSync);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        while (!window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render(Math.min(accumulator / interval, 1));

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();                
    }
    
    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameLogic.update(window, interval, mouseInput);
    }

    protected void render(float updatePercent) {
        gameLogic.updateCamera(window, updatePercent, mouseInput);
        gameLogic.render(window);
        window.update();
    }
}
