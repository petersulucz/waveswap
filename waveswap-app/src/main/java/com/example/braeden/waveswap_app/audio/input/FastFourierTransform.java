package com.example.braeden.waveswap_app.audio.input;

import com.example.braeden.waveswap_app.audio.process.Butterworth;

/**
 * Created by peter on 10/7/2015.
 */
public class FastFourierTransform {

        int lengthOfTransform, m;


        float[] PrecomputedCosine;
        float[] PrecomputedSin;

    /**
     *
     * @param datalength the length of the input
     */
        public FastFourierTransform(int datalength) {
            this.lengthOfTransform = datalength;
            this.m = (int) (Math.log(datalength) / Math.log(2));

            // precompute tables
            PrecomputedCosine = new float[datalength / 2];
            PrecomputedSin = new float[datalength / 2];

            for (int i = 0; i < datalength / 2; i++) {
                PrecomputedCosine[i] = (float)Math.cos(-2 * Math.PI * i / datalength);
                PrecomputedSin[i] = (float)Math.sin(-2 * Math.PI * i / datalength);
            }
        }


    /**
     * Magical fft code which I copied off of the internet. Do not touch. Literallly no idea how it works
     * @param realvalues input values
     * @param imaginaryValues array of zeros...
     */
        public void fft(float[] realvalues, float[] imaginaryValues) {
            int i, k, n1, lengthOver2, a;
            float t1, t2;


            // this looks really complicated
            // Bit-reverse
            int j = 0;
            lengthOver2 = lengthOfTransform / 2;
            for (i = 1; i < lengthOfTransform - 1; i++) {
                n1 = lengthOver2;
                while (j >= n1) {
                    j = j - n1;
                    n1 = n1 / 2;
                }
                j = j + n1;

                if (i < j) {
                    t1 = realvalues[i];
                    realvalues[i] = realvalues[j];
                    realvalues[j] = t1;
                    t1 = imaginaryValues[i];
                    imaginaryValues[i] = imaginaryValues[j];
                    imaginaryValues[j] = t1;
                }
            }


            // im sure this does some important stuff
            // FFT
            n1 = 0;
            lengthOver2 = 1;

            for (i = 0; i < m; i++) {
                n1 = lengthOver2;
                lengthOver2 = lengthOver2 + lengthOver2;
                a = 0;

                for (int l = 0; l < n1; l++) {
                    float cos = PrecomputedCosine[a];
                    float sin = PrecomputedSin[a];
                    a += 1 << (m - i - 1);

                    for (k = l; k < lengthOfTransform; k = k + lengthOver2) {
                        t1 = cos * realvalues[k + n1] - sin * imaginaryValues[k + n1];
                        t2 = sin * realvalues[k + n1] + cos * imaginaryValues[k + n1];
                        realvalues[k + n1] = realvalues[k] - t1;
                        imaginaryValues[k + n1] = imaginaryValues[k] - t2;
                        realvalues[k] = realvalues[k] + t1;
                        imaginaryValues[k] = imaginaryValues[k] + t2;
                    }
                }
            }
        }

        public static void frequencyTranslate(float[] data, float[] real, float[] imaginary, float centerFrequency, float sampleRate){
            float delta = 1f/sampleRate;

            for(int i = 0; i < data.length; i++){
                real[i] = data[i]*(float)Math.cos(2*Math.PI*(double)centerFrequency*(double)i*(double)delta);
                imaginary[i] = -data[i]*(float)Math.sin(2*Math.PI*(double)centerFrequency*(double)i*(double)delta);
            }
        }

        public void ZoomFFT(float[] realvalues, float[] imaginaryValues)
        {
            float centerFrequency = 17000;
            frequencyTranslate(realvalues, realvalues, imaginaryValues, centerFrequency, 44100);
            Butterworth.ButterWorth(realvalues);
            Butterworth.ButterWorth(imaginaryValues);
            for(int i = 0; i < realvalues.length/2; i++)
            {
                realvalues[i] = 16*realvalues[2*i];
                imaginaryValues[i] = 16*imaginaryValues[2*i];
            }

//            for(int i = 0; i< realvalues.length/8;i++){
//                realvalues[i] = 16*realvalues[4*i];
//                imaginaryValues[i] = 16*imaginaryValues[4*i];
//            }
            fft(realvalues, imaginaryValues);
        }
}
