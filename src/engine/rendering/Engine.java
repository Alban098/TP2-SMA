package engine.rendering;

import engine.utils.MouseInput;
import engine.utils.Timer;
import settings.SettingsInterface;
import imgui.ImGuiLayer;

/**
 * This class represent the core of the Engine
 * It handles draw-call timings, game updates timing
 * it runs an implemented ILogic
 */
public class Engine implements Runnable {

    private final Window window;

    private final Timer timer;
    private final ILogic gameLogic;
    private final MouseInput mouseInput;

    /**
     * Create a new instance of an Engine
     * @param windowTitle the Window title
     * @param width window width in pixels
     * @param height window height in pixels
     * @param gameLogic the ILogic to run
     * @param imGuiLayer the ImGui Main Layer to render
     */
    public Engine(String windowTitle, int width, int height, ILogic gameLogic, ImGuiLayer imGuiLayer) {
        window = new Window(windowTitle, width, height, imGuiLayer);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
        imGuiLayer.linkEngine(this);
    }

    /**
     * The core code of the engine
     * initialize window and all
     * then run the game loop
     */
    @Override
    public void run() {
        try {
            init();
            loop();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /**
     * Initialize the Engine
     * @throws Exception if the ILogic fail to initialize
     */
    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    /**
     * Initialize the ImGui Layer's texture if it contains some
     */
    public void prepareImGuiTexture() {
        window.prepareImGuiTexture();
    }

    /**
     * The main Engine loop
     */
    protected void loop() {
        double elapsedTime;
        double accumulator = 0f;
        double interval;
        int nbUpdate;
        //While the engine is running
        while (!window.windowShouldClose()) {
            //Calculate an update duration and get the elapsed time since last loop
            interval = 1f / SettingsInterface.TARGET_UPS;
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            //Prepare ImGui for rendering
            window.prepareImGui();

            //Handle user inputs
            input();
            nbUpdate = 0;

            //Update the logic as many times as needed to respect the number of updates per second
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
                nbUpdate++;
            }

            //Set the current update percentage used for animation purposes (only relevant if TARGET_UPS < TARGET_FPS)
            gameLogic.setPercent(Math.min(accumulator / interval, 1.0));
            render();

            //Render ImGUi and draw the frame
            window.updateImGui(elapsedTime, nbUpdate);
            window.update();

            //If VSync is disabled, wait to sync framerate with TARGET_FPS
            if (!SettingsInterface.VSYNC)
                sync();
        }
    }

    /**
     * Cleanup the Engine and its modules from memory
     */
    protected void cleanup() {
        gameLogic.cleanup();
        window.cleanup();
    }

    /**
     * Sync the framerate with TARGET_FPS
     */
    private void sync() {
        float loopSlot = 1f / SettingsInterface.TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Handle user inputs
     */
    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    /**
     * Update the ILogic, called once every update
     * @param interval elapsed time in seconds
     */
    protected void update(double interval) {
        gameLogic.update(window, interval);
    }

    /**
     * Render the frame, called once every frame
     */
    protected void render() {
        gameLogic.updateCamera(window, mouseInput);
        gameLogic.render(window);
    }

    /**
     * Reset the frame timer
     */
    public void resetFrameTimer() {
        timer.getElapsedTime();
    }
}
