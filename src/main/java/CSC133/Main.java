

package CSC133;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;


public class Main {
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWFramebufferSizeCallback fbCallback;
    private long window;
    private static int WIN_WIDTH = 720, WIN_HEIGHT = 720;
    private int WIN_POS_X = 50, WIN_POS_Y = 100;
    private static final int OGL_MATRIX_SIZE = 16;
    // call glCreateProgram() here - we have no gl-context here
    private int shader_program;
    private Matrix4f viewProjMatrix = new Matrix4f();
    private FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
    private int vpMatLocation = 0, renderColorLocation = 0;
    public static void main(String[] args) {
//        new csc133.slWindow().slWindow(WIN_WIDTH, WIN_HEIGHT);
        new Main().render();
    } // public static void main(String[] args)
    void render() {
        try {
            window = slWindow.get(WIN_WIDTH, WIN_HEIGHT, WIN_POS_X, WIN_POS_Y);
            renderLoop();
            glfwDestroyWindow(window);
            keyCallback.free();
            fbCallback.free();
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    } // void render()
    void renderLoop() {
        glfwPollEvents();
        initOpenGL();
        renderObjects();
        /* Process window messages in the main thread */
        while (!glfwWindowShouldClose(window)) {
            glfwWaitEvents();
        }
    } // void renderLoop()
    void initOpenGL() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        float clearRed = 0.0f, clearGreen = 0.0f, clearBlue = 1.0f, clearAlpha = 1.0f;
        glClearColor(clearRed, clearGreen, clearBlue, clearAlpha);
        this.shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs,
                "uniform mat4 viewProjMatrix;" +
                        "void main(void) {" +
                        " gl_Position = viewProjMatrix * gl_Vertex;" +
                        "}");
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs,
                "uniform vec3 color;" +
                        "void main(void) {" +
                        " gl_FragColor = vec4(0.1607843137f, 0.6705882353f, 0.5294117647f, 1.0f);" +
                        "}");
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
        glUseProgram(shader_program);
        String uniformVarName = "viewProjMatrix";
        vpMatLocation = glGetUniformLocation(shader_program, uniformVarName);
        return;
    } // void initOpenGL()
    void renderObjects() {
        int length = 10;
        int offset = 10;
        int xmin = offset, xmax = xmin + length;
        int padding = 5;
        int ymax = WIN_HEIGHT - offset, ymin = ymax - length;
        int vps = 4; // vertices per square
        int fpv = 2; // floats / vertices
        int maxRows = 7, maxCols = 5;
        float[] vertices = new float[maxRows * maxCols * vps * fpv];

        int index = 0;

        for (int i = 0; i < maxRows; i++) {
            for (int j = 0; j < maxCols; j++) {
                vertices[index++] = xmin;   // bottom left X
                vertices[index++] = ymin;   // bottom left Y
                vertices[index++] = xmax;   // bottom right X
                vertices[index++] = ymin;   // bottom right Y
                vertices[index++] = xmax;   // top right X
                vertices[index++] = ymax;   // top right Y
                vertices[index++] = xmin;   // top left X
                vertices[index++] = ymax;   // top left Y
                xmin = xmax + padding;
                xmax = xmin + length;
            }
            xmin = offset;
            xmax = xmin + length;
            ymax = ymin - padding;
            ymin = ymax - length;
        }

        int ips = 6; // indices per square

        int[] indices = new int[maxRows * maxCols * ips];


        int vIndex = 0, myI = 0;

        while (myI < indices.length) {
            indices[myI++] = vIndex;
            indices[myI++] = vIndex+1;
            indices[myI++] = vIndex+2;
            indices[myI++] = vIndex;
            indices[myI++] = vIndex+2;
            indices[myI++] = vIndex+3;
            vIndex += vps;
        }

        while (!glfwWindowShouldClose(window)) {

            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            int vbo = glGenBuffers();
            int ibo = glGenBuffers();
//            float botLeftX = -50f, botLeftY = -50f, botRightX = 50f, botRightY = -50f,
//                topRightX = 50f, topRightY = 50f, topLeftX = -50f, topLeftY = 50f;

//            float[] vertices = {-20f, -20f, 20f, -20f, 20f, 20f, -20f, 20f};
//            int[] indices = {0, 1, 2, 0, 2, 3};
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                    createFloatBuffer(vertices.length).
                    put(vertices).flip(), GL_STATIC_DRAW);
            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                    createIntBuffer(indices.length).
                    put(indices).flip(), GL_STATIC_DRAW);
            int vertexSize = 2, vertexStride = 0;
            long vertexPointer = 0L;
            glVertexPointer(vertexSize, GL_FLOAT, vertexStride, vertexPointer);
            // -100, 100, -100, 100, 0, 10
            int orthPosLeft = 0, orthPosRight = WIN_WIDTH, orthPosBot = 0, orthPosTop = WIN_HEIGHT, orthPosZNear = 0, orthPosZFar = 10;
            viewProjMatrix.setOrtho(orthPosLeft, orthPosRight, orthPosBot, orthPosTop, orthPosZNear, orthPosZFar);
            boolean unifMatrix4fvTranspose = false;
            glUniformMatrix4fv(vpMatLocation, unifMatrix4fvTranspose,
                    viewProjMatrix.get(myFloatBuffer));
            float uniformX = 1.0f, uniformY = 0.498f, uniformZ = 0.153f;
            glUniform3f(renderColorLocation, uniformX, uniformY, uniformZ);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            int VTD = 6 * maxRows * maxCols; // need to process 6 Vertices To Draw 2 triangles
            long indicesNum = 0L;
            glDrawElements(GL_TRIANGLES, VTD, GL_UNSIGNED_INT, indicesNum);
            glfwSwapBuffers(window);
        }
    } // renderObjects
}
