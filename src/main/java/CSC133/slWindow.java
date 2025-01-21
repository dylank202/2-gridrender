
package CSC133;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class slWindow {
    private static long myOglWin = NULL;
    private static int width = 200, height = 200, posX = 10, posY = 10;


    private static GLFWErrorCallback errorCallback;
    private static GLFWKeyCallback keyCallback;
    private static GLFWFramebufferSizeCallback fbCallback;

    private slWindow() {}

    public static long get() {
        glfwSetErrorCallback(errorCallback =
                GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        int glfwWinHintVal = 8;
        glfwWindowHint(GLFW_SAMPLES, glfwWinHintVal);
        myOglWin = glfwCreateWindow(width, height, "CSC 133", NULL, NULL);
        if (myOglWin == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        glfwSetKeyCallback(myOglWin, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long myOglWin, int key, int scancode, int action, int
                    mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(myOglWin, true);
            }
        });
        glfwSetFramebufferSizeCallback(myOglWin, fbCallback = new
                GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(long myOglWin, int w, int h) {
                        if (w > 0 && h > 0) {
                            width = w;
                            height = h;
                        }
                    }
                });
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(myOglWin, posX, posY);
        glfwMakeContextCurrent(myOglWin);
        int VSYNC_INTERVAL = 1;
        glfwSwapInterval(VSYNC_INTERVAL);
        glfwShowWindow(myOglWin);
        return myOglWin;
    }

    public static long get(int myWidth, int myHeight, int myPosX, int myPosY) {
        width = myWidth;
        height = myHeight;
        posX = myPosX;
        posY = myPosY;
//        get(); // now creates window with passed values which have been overwritten instead of default values
        return get();
    }

//    void slWindow(int win_width, int win_height) {
//        System.out.println("Call to slWindow:: (width, height) == ("
//                        + win_width + ", " + win_height +") received!");
//    }
}
