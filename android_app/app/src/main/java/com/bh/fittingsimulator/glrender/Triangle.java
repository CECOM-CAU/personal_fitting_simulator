package com.bh.fittingsimulator.glrender;

import android.content.SharedPreferences;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    /***********/
    public static float values[]={
            1630.0f,//height
            0f,//armhole
            0f,//chest
            0f,//shoulder to chest
            0f,//waist
            0f//chest to waist
    };

    static float RATIO= (float) (1.5/values[0]);
    static float GROUND= (float) -0.75;
    static float shoulder_width=values[2];
    static float upperBody_len=values[3]+values[5];
    static float head_len= (float) (upperBody_len*0.6);
    static float neck_len= (float) (head_len*0.5);
    static float Y_len=neck_len;
    static float leg_len=upperBody_len*2-Y_len;

    //float buffer 타입으로 vertexBuffer를 선언합니다.
    private FloatBuffer vertexBuffer;

    //0. float 배열에 삼각형의 vertex를 위한 좌표를 넣습니다.
    static final int COORDS_PER_VERTEX = 3;
    public static float triangleCoords[] = {   //넣는 순서는 반시계 방향입니다.
            //shoulder to chest
            -shoulder_width/2*RATIO,GROUND+(leg_len+Y_len+upperBody_len)*RATIO,0.0f,
            -values[2]/2*RATIO,GROUND+(leg_len+Y_len+values[5])*RATIO,0.0f,
            values[2]/2*RATIO,GROUND+(leg_len+Y_len+values[5])*RATIO,0.0f,
            -shoulder_width/2*RATIO,GROUND+(leg_len+Y_len+upperBody_len)*RATIO,0.0f,
            values[2]/2*RATIO,GROUND+(leg_len+Y_len+values[5])*RATIO,0.0f,
            shoulder_width/2*RATIO,GROUND+(leg_len+Y_len+upperBody_len)*RATIO,0.0f,

            //chest to waist
            -values[2]/2*RATIO,GROUND+(leg_len+Y_len+values[5])*RATIO,0.0f,
            -values[4]/2*RATIO,GROUND+(leg_len+Y_len)*RATIO,0.0f,
            values[4]/2*RATIO,GROUND+(leg_len+Y_len)*RATIO,0.0f,
            -values[2]/2*RATIO,GROUND+(leg_len+Y_len+values[5])*RATIO,0.0f,
            values[4]/2*RATIO,GROUND+(leg_len+Y_len)*RATIO,0.0f,
            values[2]/2*RATIO,GROUND+(leg_len+Y_len+values[5])*RATIO,0.0f,
    };

    //red, green, blue, alpha 값을 float 배열 color에 넣습니다.
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    private final int mProgram;

    public Triangle() {


        //1.ByteBuffer를 할당 받습니다.
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);

        //2. ByteBuffer에서 사용할 엔디안을 지정합니다.
        //버퍼의 byte order로써 디바이스 하드웨어의 native byte order를 사용
        bb.order(ByteOrder.nativeOrder());

        //3. ByteBuffer를 FloatBuffer로 변환합니다.
        vertexBuffer = bb.asFloatBuffer();

        //4. float 배열에 정의된 좌표들을 FloatBuffer에 저장합니다.
        vertexBuffer.put(triangleCoords);

        //5. 읽어올 버퍼의 위치를 0으로 설정한다. 첫번째 좌표부터 읽어오게됨
        vertexBuffer.position(0);

        //vertex shader 타입의 객체를 생성하여 vertexShaderCode에 저장된 소스코드를 로드한 후,
        //   컴파일합니다.
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);

        //fragment shader 타입의 객체를 생성하여 fragmentShaderCode에 저장된 소스코드를 로드한 후,
        //  컴파일합니다.
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // Program 객체를 생성한다.
        mProgram = GLES20.glCreateProgram();

        // vertex shader를 program 객체에 추가
        GLES20.glAttachShader(mProgram, vertexShader);

        // fragment shader를 program 객체에 추가
        GLES20.glAttachShader(mProgram, fragmentShader);

        // program객체를 OpenGL에 연결한다. program에 추가된 shader들이 OpenGL에 연결된다.
        GLES20.glLinkProgram(mProgram);

    }

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw() {
        //렌더링 상태(Rendering State)의 일부분으로 program을 추가한다.
        GLES20.glUseProgram(mProgram);

        // program 객체로부터 vertex shader의'vPosition 멤버에 대한 핸들을 가져옴
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        //triangle vertex 속성을 활성화 시켜야 렌더링시 반영되서 그려짐
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // triangle vertex 속성을 vertexBuffer에 저장되어 있는 vertex 좌표들로 정의한다.
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);


        // program 객체로부터 fragment shader의 vColor 멤버에 대한 핸들을 가져옴
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        //triangle 렌더링시 사용할 색으로 color변수에 정의한 값을 사용한다.
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);



        //vertex 갯수만큼 tiangle을 렌더링한다.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        //vertex 속성을 비활성화 한다.
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}