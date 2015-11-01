package com.example.braeden.waveswap_app.audio.process;

import com.example.braeden.waveswap_app.audio.input.FFTListener;

/**
 * Created by peter on 11/1/2015.
 */
public class FFTParser implements FFTListener
{

    public float[] getWeights()
    {
        return weights;
    }

    private float[] weights;
    private int sampleRate;

    public float getSensitivity()
    {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity)
    {
        this.sensitivity = sensitivity;
    }


    private float sensitivity = 100;

    public FFTParser(int length, int sampleRate)
    {
        weights = new float[length];
        this.sampleRate = sampleRate;
    }

    @Override
    public void FFTReady(float[] fft)
    {
        for(int i = 0; i < this.weights.length; i++)
        {
            weights[i] *= 0.85f;
            if(fft[i] >= sensitivity)
            {
                weights[i] += sensitivity/3;
            }
        }
    }
}
