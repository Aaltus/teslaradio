package com.ar4android.vuforiaJME.java;

import com.qualcomm.vuforia.Matrix44F;

/**
 * Created by jimbojd72 on 10/22/2014.
 */
public class MathUtil {

    public static Matrix44F Matrix44FTranspose(Matrix44F m) {
        Matrix44F r = new Matrix44F();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.getData()[i * 4 + j] = m.getData()[i + 4 * j];
        return r;
    }

}
