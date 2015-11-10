package com.example.braeden.waveswap_app.audio.input;

/**
 * Created by Braeden on 11/9/2015.
 */
public class Peter {
    private static Peter instance = null;
    private int[] data;
    private int counter;
    private Peter() {
        data = new int[1000];
    }

    public static Peter getInstance () {
        if (instance == null) {
            instance = new Peter();
        }
        return instance;
    }

    public void addValue(int value) {
        data[counter++] = value;
        if (counter == data.length) {
            //Send to matt
            counter = 0;
        }
    }

}
