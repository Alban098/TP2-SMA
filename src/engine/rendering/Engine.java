package engine.rendering;

import engine.utils.MouseInput;
import engine.utils.Timer;
import settings.SettingsInterface;
import imgui.ImGuiLayer;

public class Engine implements Runnable {

    private final Window window;

    private final Timer timer;
    private final ILogic gameLogic;
    private final MouseInput mouseInput;

    public Engine(String windowTitle, int width, int height, ILogic gameLogic, ImGuiLayer imGuiLayer) {
        window = new Window(windowTitle, width, height, imGuiLayer);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
        imGuiLayer.linkEngine(this);
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

    public void prepareImGuiTexture() {
        window.prepareImGuiTexture();
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval;
        int nbUpdate;
        while (!window.windowShouldClose()) {
            interval = 1f / SettingsInterface.TARGET_UPS;
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            window.prepareImGui();

            input();
            nbUpdate = 0;
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
                nbUpdate++;
            }

            gameLogic.setPercent(Math.min(accumulator / interval, 1));
            render();

            window.updateImGui(elapsedTime, nbUpdate);
            window.update();

            if (!SettingsInterface.VSYNC) {
                sync();
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();
        window.cleanup();
    }
    
    private void sync() {
        float loopSlot = 1f / SettingsInterface.TARGET_FPS;
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
        gameLogic.update(window, interval);
    }

    protected void render() {
        gameLogic.updateCamera(window, mouseInput);
        gameLogic.render(window);
    }

    public void resetFrameTimer() {
        timer.getElapsedTime();
    }
}
