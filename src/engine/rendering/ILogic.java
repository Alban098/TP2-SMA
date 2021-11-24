package engine.rendering;

import engine.utils.MouseInput;

public interface ILogic {

    void init(Window window) throws Exception;
    
    void input(Window window, MouseInput mouseInput);

    void update(Window window, float interval, MouseInput mouseInput);

    void updateCamera(Window window, float percent, MouseInput mouseInput);
    
    void render(Window window);
    
    void cleanup();

    void pause();

    void resume();

    void reset();
}