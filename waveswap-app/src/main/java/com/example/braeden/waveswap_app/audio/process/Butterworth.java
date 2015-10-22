package com.example.braeden.waveswap_app.audio.process;

/**
 * Created by peter on 10/22/2015.
 */
public class Butterworth {

    private static final int NZEROS  = 10;
    private static final int NPOLES = 10;
    private static final float GAIN = 2.148974939e+02f;


    /**
     * Bandpass from 14kHz to 20kHz
     * @param values
     */
    public static void ButterWorth(float[] values)
    {
        float[] xv = new float[NZEROS +1];
        float[] yv = new float[NPOLES +1];

        for(int i = 0; i < values.length; i++) {
            xv[0] = xv[1];
            xv[1] = xv[2];
            xv[2] = xv[3];
            xv[3] = xv[4];
            xv[4] = xv[5];
            xv[5] = xv[6];
            xv[6] = xv[7];
            xv[7] = xv[8];
            xv[8] = xv[9];
            xv[9] = xv[10];
            xv[10] = values[i]/GAIN;
            yv[0] = yv[1];
            yv[1] = yv[2];
            yv[2] = yv[3];
            yv[3] = yv[4];
            yv[4] = yv[5];
            yv[5] = yv[6];
            yv[6] = yv[7];
            yv[7] = yv[8];
            yv[8] = yv[9];
            yv[9] = yv[10];
            yv[10] = (xv[10] - xv[0]) + 5 * (xv[2] - xv[8]) + 10 * (xv[6] - xv[4])
                    + (-0.0562832751f * yv[0]) + (-0.5932842001f * yv[1])
                    + (-2.9779539684f * yv[2]) + (-9.3393690617f * yv[3])
                    + (-20.2238946430f * yv[4]) + (-31.5715351170f * yv[5])
                    + (-36.0122593860f * yv[6]) + (-29.6752947170f * yv[7])
                    + (-16.9053708590f * yv[8]) + (-5.9950848605f * yv[9]);
            values[i] = yv[10];
        }
    }

}
