package com.bh.fittingsimulator.glrender;


import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyFittingSurfaceView extends GLSurfaceView {

    private final com.bh.fittingsimulator.glrender.MyFittingRenderer mRenderer;

    public MyFittingSurfaceView(Context context,float[] c_data){
        super(context);

        // OpenGL ES 2.0 context를 생성합니다.
        setEGLContextClientVersion(2);
        System.out.println("!!!!!!!!!!!!! c_data at surfaceview "+c_data[0]);
        mRenderer = new com.bh.fittingsimulator.glrender.MyFittingRenderer(c_data);

        // GLSurfaceView에 그래픽 객체를 그리는 처리를 하는 renderer를 설정합니다.
        setRenderer(mRenderer);


        //Surface가 생성될때와 GLSurfaceView클래스의 requestRender 메소드가 호출될때에만
        //화면을 다시 그리게 됩니다.
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}