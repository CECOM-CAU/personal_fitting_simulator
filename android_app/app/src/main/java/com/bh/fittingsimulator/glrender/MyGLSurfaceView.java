package com.bh.fittingsimulator.glrender;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.bh.fittingsimulator.MainActivity;

public class MyGLSurfaceView extends GLSurfaceView {

    private final com.bh.fittingsimulator.glrender.MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // OpenGL ES 2.0 context를 생성합니다.
        setEGLContextClientVersion(2);

        mRenderer = new com.bh.fittingsimulator.glrender.MyGLRenderer((MainActivity)context);

        // GLSurfaceView에 그래픽 객체를 그리는 처리를 하는 renderer를 설정합니다.
        setRenderer(mRenderer);


        //Surface가 생성될때와 GLSurfaceView클래스의 requestRender 메소드가 호출될때에만
        //화면을 다시 그리게 됩니다.
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        Log.d("arm:", String.valueOf(mRenderer.mTriangle.values[1]));
        Log.d("chest:", String.valueOf(mRenderer.mTriangle.values[2]));
        Log.d("shoulder to chest:", String.valueOf(mRenderer.mTriangle.values[3]));
        Log.d("waist:", String.valueOf(mRenderer.mTriangle.values[4]));
        Log.d("chest to waist:", String.valueOf(mRenderer.mTriangle.values[5]));
        requestRender();
        return true;
    }
}