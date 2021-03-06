package com.bh.fittingsimulator.glrender;

import java.lang.*;

import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.util.Log;

import com.bh.fittingsimulator.LoadingActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.content.Context.MODE_PRIVATE;


public class Triangle {
    static private float LIMIT=1.6f;

    //0.키, 1.머리길이, 2.목길이, 3.목부터 어깨까지 수직길이, 4.어깨 길이, 5.팔 폭, 6.팔 길이, 7.겨드랑이 사이 길이, 8.배부분 폭, 9.가슴 폭, 10.어깨에서 골반까지, 11. 가슴에서 골반까지, 12. 배꼽에서 골반까지, 13.겨드랑이에서 가슴까지
    //static float[] fv ={1630.0f,205.0f,75.0f,70.0f,385.0f,70.0f,530.0f,280.0f,250.0f,270.0f,460.0f,285.0f,105.0f,130.0f};
    //new_minju

    float[] fv ={1630.0f,205.0f,75.0f,60.7f,339.8f,72.35f,542.1f,289.26f,240.3f,315.4f,483.0f,338.0f,278.4f,130.0f};
    //new_sanmin
    //static float[] fv ={1730.0f,205.0f,75.0f,68.3f,567.7f,146.0f,533.5f,520.5f,357.9f,309.1f,543.0f,305.8f,299.3f,130.0f};
    float[] sv={1630.0f,95.0f,5.0f,10.0f,180.0f,140.0f,135.0f,120.0f,205.0f};
    //0.null, 1.소매길이, 2. 총 길이, 3.어께, 4.소매폭 5.가슴폭
    //static float[] upper_c={0.0f,542.1f,338.0f,350.8f,90.0f,330.f};
    //static float c_arm_len =(upper_c[3]-fv[4])/2.0f+upper_c[1];
    private float RATIO;
    private float leg_len;
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    // Use to access and set the view transformation
    private int mMVPMatrixHandle;
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    //float buffer 타입으로 vertexBuffer를 선언합니다.
    private FloatBuffer vertexBuffer;
    //0. float 배열에 삼각형의 vertex를 위한 좌표를 넣습니다.
    final int COORDS_PER_VERTEX = 3;

    float triangleCoords[]; /* = {   //넣는 순서는 반시계 방향입니다.

         //정면

        -fv[1]/2.0f*RATIO, fv[0]*RATIO-LIMIT/2.0f, 0.0f,    //a
        -fv[1]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,  //b
        fv[1]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,   //u
        -fv[1]/2.0f*RATIO, fv[0]*RATIO-LIMIT/2.0f, 0.0f,                    //a
        fv[1]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,   //u
        fv[1]/2.0f*RATIO, fv[0]*RATIO-LIMIT/2.0f , 0.0f,                    //v

        -fv[2]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,  //c
        -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,  //d
        fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,   //s
        -fv[2]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,  //c
        fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,   //s
        fv[2]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f , 0.0f,   //t

        -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,    //d
        -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
        fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
        -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//d
        fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
        fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//s

        -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
        //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//h
            (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h
        //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//o
            (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
        -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//e
        //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//o
            (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
        fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r

        //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//h
            (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h
        -fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//i
        fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//n
        //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,   //h
            (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h
        fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//n
       // fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//o
            (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o

        -fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//i
        -fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//j
        fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//m
        -fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//i
        fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//m
        fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//n

        -fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//j
        -fv[8]/2.0f*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//k
        fv[8]/2.0f*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//l
        -fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//j
        fv[8]/2.0f*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//l
        fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//m

        -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//e
        (-fv[4]/2.0f-3.0f/5.0f* fv[6])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6])*RATIO-LIMIT/2.0f,0.0f,//f
        (-fv[4]/2.0f-3.0f/5.0f* fv[6]+4.0f/5.0f* fv[5])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//g
        -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//e
        (-fv[4]/2.0f-3.0f/5.0f* fv[6]+4.0f/5.0f* fv[5])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//g
        //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//h
            (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h


        //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,    //o
            (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
        -(-fv[4]/2.0f-3.0f/5.0f* fv[6]+4.0f/5.0f* fv[5])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,    //p
        -(-fv[4]/2.0f-3.0f/5.0f* fv[6])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6])*RATIO-LIMIT/2.0f,0.0f,//q
        //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,    //o
            (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
        -(-fv[4]/2.0f-3.0f/5.0f* fv[6])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6])*RATIO-LIMIT/2.0f,0.0f,//q
        fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
        */
/*
            //upperbody clothes
            //ders
            -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,    //d
            -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
            fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
            -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//d
            fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
            fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//s

            //h"k'l'o"
            -upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h"
            -upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-upper_c[2])*RATIO-LIMIT/2.0f,0.0f,//k'
            upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-upper_c[2])*RATIO-LIMIT/2.0f,0.0f,//l'
            -upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h"
            upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-upper_c[2])*RATIO-LIMIT/2.0f,0.0f,//l'
            upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//o"

        //ef'g'h'
            -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
            (-fv[4]/2.0f-3.0f/5.0f* c_arm_len)*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len)*RATIO-LIMIT/2.0f,0.0f,//f'
            (-fv[4]/2.0f-3.0f/5.0f* c_arm_len +4.0f/5.0f* upper_c[4])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len -3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,//g'
            -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
            (-fv[4]/2.0f-3.0f/5.0f* c_arm_len +4.0f/5.0f* upper_c[4])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len -3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,//g'
            (-fv[4]/2.0f+4.0f/5.0f* upper_c[4])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,//h'

            //o'p'q'r
            (fv[4]/2.0f-4.0f/5.0f* upper_c[4])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f, //o'
            -(-fv[4]/2.0f-3.0f/5.0f* c_arm_len +4.0f/5.0f* upper_c[4])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len -3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,    //p'
            -(-fv[4]/2.0f-3.0f/5.0f* c_arm_len )*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len )*RATIO-LIMIT/2.0f,0.0f,//q'
            (fv[4]/2.0f-4.0f/5.0f* upper_c[4])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f, //o'
            -(-fv[4]/2.0f-3.0f/5.0f* c_arm_len )*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len )*RATIO-LIMIT/2.0f,0.0f,//q'
            fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
*/
            //측면
/*
        -(sv[1]+sv[2])*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,//a
        -(sv[1]+sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,  //b
        -(sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,    //i
        -(sv[1]+sv[2])*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,   //a
        -(sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,   //i
        -(sv[2])*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,    //j

            -(sv[1]+sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,  //b
          0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f, //h
            -(sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f, //i

            -(sv[1]+sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f, //b
            -sv[4]*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//c
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[4]*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//c
            -sv[5]*RATIO,(leg_len+sv[8])*RATIO-LIMIT/2.0f,0.0f,//d
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[5]*RATIO,(leg_len+sv[8])*RATIO-LIMIT/2.0f,0.0f,//d
            -sv[6]*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//e
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[6]*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f, //e
            -sv[7]*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//f
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[7]*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//f
            0.0f,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//g
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

*/
   // };


    //red, green, blue, alpha 값을 float 배열 color에 넣습니다.
    float color[] = { 0.22671875f, 0.45953125f, 0.82265625f, 1.0f };
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Triangle(Activity activity) {
        SharedPreferences test = activity.getSharedPreferences("point", MODE_PRIVATE);
        fv[0] = Float.parseFloat(test.getString("height","1630.0"));
        fv[3] = Float.parseFloat(test.getString("data0","1.0"));
        Log.d("fv0",String.valueOf(fv[3]));
        fv[4] = Float.parseFloat(test.getString("data1","1.0"));
        Log.d("fv1",String.valueOf(fv[4]));
        fv[5] = Float.parseFloat(test.getString("data2","1.0"));
        Log.d("fv2",String.valueOf(fv[5]));
        fv[6] = Float.parseFloat(test.getString("data3","1.0"));
        Log.d("fv3",String.valueOf(fv[6]));

        fv[7] = Float.parseFloat(test.getString("data4","1.0"));
        Log.d("fv4",String.valueOf(fv[7]));
        fv[8] = Float.parseFloat(test.getString("data5","1.0"));
        Log.d("fv5",String.valueOf(fv[8]));
        fv[9] = Float.parseFloat(test.getString("data6","1.0"));
        Log.d("fv6",String.valueOf(fv[9]));
        fv[10] = Float.parseFloat(test.getString("data7","1.0"));
        Log.d("fv7",String.valueOf(fv[10]));
        fv[11] = Float.parseFloat(test.getString("data8","1.0"));
        Log.d("fv8",String.valueOf(fv[11]));
        fv[12] = Float.parseFloat(test.getString("data9","1.0"));
        Log.d("fv9",String.valueOf(fv[12]));

        RATIO=  LIMIT/ fv[0];
        leg_len= fv[0]- fv[1]- fv[2]- fv[3]- fv[10];

        triangleCoords = new float[]{   //넣는 순서는 반시계 방향입니다.

            //정면

            -fv[1]/2.0f*RATIO, fv[0]*RATIO-LIMIT/2.0f, 0.0f,    //a
                    -fv[1]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,  //b
                    fv[1]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,   //u
                    -fv[1]/2.0f*RATIO, fv[0]*RATIO-LIMIT/2.0f, 0.0f,                    //a
                    fv[1]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,   //u
                    fv[1]/2.0f*RATIO, fv[0]*RATIO-LIMIT/2.0f , 0.0f,                    //v

                    -fv[2]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,  //c
                    -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,  //d
                    fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,   //s
                    -fv[2]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,  //c
                    fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,   //s
                    fv[2]/2.0f*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f , 0.0f,   //t

                    -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,    //d
                    -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
                    fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
                    -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//d
                    fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
                    fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//s

                    -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
                    //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//h
                    (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h
                    //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//o
                    (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
                    -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//e
                    //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//o
                    (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
                    fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r

                    //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//h
                    (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h
                    -fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//i
                    fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//n
                    //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,   //h
                    (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h
                    fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//n
                    // fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//o
                    (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o

                    -fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//i
                    -fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//j
                    fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//m
                    -fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//i
                    fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//m
                    fv[9]/2.0f*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//n

                    -fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//j
                    -fv[8]/2.0f*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//k
                    fv[8]/2.0f*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//l
                    -fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//j
                    fv[8]/2.0f*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//l
                    fv[8]/2.0f*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//m

                    -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//e
                    (-fv[4]/2.0f-3.0f/5.0f* fv[6])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6])*RATIO-LIMIT/2.0f,0.0f,//f
                    (-fv[4]/2.0f-3.0f/5.0f* fv[6]+4.0f/5.0f* fv[5])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//g
                    -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//e
                    (-fv[4]/2.0f-3.0f/5.0f* fv[6]+4.0f/5.0f* fv[5])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//g
                    //-fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,//h
                    (-fv[4]/2.0f+4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,//h


                    //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,    //o
                    (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
                    -(-fv[4]/2.0f-3.0f/5.0f* fv[6]+4.0f/5.0f* fv[5])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f,    //p
                    -(-fv[4]/2.0f-3.0f/5.0f* fv[6])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6])*RATIO-LIMIT/2.0f,0.0f,//q
                    //fv[7]/2.0f*RATIO,(leg_len+ fv[11]+ fv[13])*RATIO-LIMIT/2.0f,0.0f,    //o
                    (fv[4]/2.0f-4.0f/5.0f* fv[5])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* fv[5])*RATIO-LIMIT/2.0f,0.0f, //o
                    -(-fv[4]/2.0f-3.0f/5.0f* fv[6])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* fv[6])*RATIO-LIMIT/2.0f,0.0f,//q
                    fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
/*
            //upperbody clothes
            //ders
            -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,    //d
            -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
            fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
            -fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//d
            fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
            fv[2]/2.0f*RATIO, (fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,//s

            //h"k'l'o"
            -upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h"
            -upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-upper_c[2])*RATIO-LIMIT/2.0f,0.0f,//k'
            upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-upper_c[2])*RATIO-LIMIT/2.0f,0.0f,//l'
            -upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h"
            upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-upper_c[2])*RATIO-LIMIT/2.0f,0.0f,//l'
            upper_c[5]/2.0f*RATIO,(fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//o"

        //ef'g'h'
            -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
            (-fv[4]/2.0f-3.0f/5.0f* c_arm_len)*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len)*RATIO-LIMIT/2.0f,0.0f,//f'
            (-fv[4]/2.0f-3.0f/5.0f* c_arm_len +4.0f/5.0f* upper_c[4])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len -3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,//g'
            -fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,    //e
            (-fv[4]/2.0f-3.0f/5.0f* c_arm_len +4.0f/5.0f* upper_c[4])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len -3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,//g'
            (-fv[4]/2.0f+4.0f/5.0f* upper_c[4])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,//h'

            //o'p'q'r
            (fv[4]/2.0f-4.0f/5.0f* upper_c[4])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f, //o'
            -(-fv[4]/2.0f-3.0f/5.0f* c_arm_len +4.0f/5.0f* upper_c[4])*RATIO,(fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len -3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f,    //p'
            -(-fv[4]/2.0f-3.0f/5.0f* c_arm_len )*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len )*RATIO-LIMIT/2.0f,0.0f,//q'
            (fv[4]/2.0f-4.0f/5.0f* upper_c[4])*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-3.0f/5.0f* upper_c[4])*RATIO-LIMIT/2.0f,0.0f, //o'
            -(-fv[4]/2.0f-3.0f/5.0f* c_arm_len )*RATIO, (fv[0]- fv[1]- fv[2]- fv[3]-4.0f/5.0f* c_arm_len )*RATIO-LIMIT/2.0f,0.0f,//q'
            fv[4]/2.0f*RATIO, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//r
*/
            //측면
/*
        -(sv[1]+sv[2])*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,//a
        -(sv[1]+sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,  //b
        -(sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,    //i
        -(sv[1]+sv[2])*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,   //a
        -(sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,   //i
        -(sv[2])*RATIO, (fv[0]- fv[1])*RATIO-LIMIT/2.0f, 0.0f,    //j

            -(sv[1]+sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f,  //b
          0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f, //h
            -(sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f, //i

            -(sv[1]+sv[3])*RATIO,(fv[0]- fv[1]- fv[2])*RATIO-LIMIT/2.0f, 0.0f, //b
            -sv[4]*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//c
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[4]*RATIO,(leg_len+ fv[11])*RATIO-LIMIT/2.0f,0.0f,//c
            -sv[5]*RATIO,(leg_len+sv[8])*RATIO-LIMIT/2.0f,0.0f,//d
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[5]*RATIO,(leg_len+sv[8])*RATIO-LIMIT/2.0f,0.0f,//d
            -sv[6]*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f,//e
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[6]*RATIO,(leg_len+ fv[12])*RATIO-LIMIT/2.0f,0.0f, //e
            -sv[7]*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//f
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

            -sv[7]*RATIO,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//f
            0.0f,(leg_len)*RATIO-LIMIT/2.0f,0.0f,//g
            0.0f, (fv[0]- fv[1]- fv[2]- fv[3])*RATIO-LIMIT/2.0f,0.0f,//h

*/
        };
        vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
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



    public void draw(float[] mvpMatrix) {
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

        //program 객체로부터 vertex shader 타입의 객체에 정의된 uMVPMatrix에 대한 핸들을 획득합니다.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        //projection matrix와 camera view matrix를 곱하여 얻어진  mMVPMatrix 변수의 값을
        // vertex shader 객체에 선언된 uMVPMatrix 멤버에게로 넘겨줍니다.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        //vertex 갯수만큼 tiangle을 렌더링한다.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        //vertex 속성을 비활성화 한다.
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
