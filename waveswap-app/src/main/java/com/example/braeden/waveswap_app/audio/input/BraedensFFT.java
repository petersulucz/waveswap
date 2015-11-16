package com.example.braeden.waveswap_app.audio.input;

import java.util.HashMap;

/**
 * Created by Braeden on 11/9/2015.
 */
public class BraedensFFT {
    private static BraedensFFT braeden = null;
    int[] map = new int[32];
    private int count = 0;


    private BraedensFFT()
    {
        this.map = new int[32];
    }

    public static BraedensFFT getInstance() {
        if (braeden == null) {
            braeden = new BraedensFFT();
        }
        return braeden;
    }

    public synchronized void addValue(int given) {
        map[given]++;
        count++;

        if(count == 10)
        {
            int maxIndex = 0;
            int max = this.map[0];
            for(int i = 1; i < this.map.length; i++)
            {
                if(this.map[i] > max)
                {
                    maxIndex = i;
                }
            }

            Peter.getInstance().addValue(maxIndex);
            this.map = new int[32];
            count = 0;
        }
    }
}