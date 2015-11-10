package com.example.braeden.waveswap_app.audio.input;

import java.util.HashMap;

/**
 * Created by Braeden on 11/9/2015.
 */
public class BraedensFFT {
    private static BraedensFFT braeden = null;
    HashMap<Integer, Integer> map;

    private BraedensFFT() {
    }

    public static BraedensFFT getInstance() {
        if (braeden == null) {
            braeden = new BraedensFFT();
        }
        return braeden;
    }

    public void addValue(int given) {
        if (map.containsKey(given)) {
            int count = map.get(given);
            map.put(given, count + 1);
            if (count > 5) {
                // Do something
                Peter.getInstance().addValue(given);
                map = new HashMap<>();
            }
        } else {
            map.put(given, 1);
        }
    }


}