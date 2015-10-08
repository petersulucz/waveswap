package com.example.braeden.waveswap_app.audio.input;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.braeden.waveswap_app.audio.input.Tuple;

/**
 * Created by peter on 10/7/2015.
 */
public class FFTBitmap implements FFTListener {
    private Bitmap bitmap;
    private int resultion;
    private BitmapChangeListener listener;
    private float sensitivity = 8192;
    private int width = 100;

    int column = 0;
    public FFTBitmap(int dimension){
        this.resultion = dimension;
        this.bitmap = Bitmap.createBitmap(width, dimension, Bitmap.Config.ARGB_8888);

        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.resultion; y++){
                this.bitmap.setPixel(x, y, Color.BLACK);
            }
        }
    }

    public void Update(float[] values){
        for(int i = 0; i < values.length; i++){
            int value = (int)(values[i] / this.sensitivity * 255);
            this.bitmap.setPixel(column, this.resultion - i - 1, Color.rgb(value, value, value));
        }
        column++;
        column = column % this.width;
    }

    public void SetSensitivity(float sensitivity){
        this.sensitivity = sensitivity;
    }

    public Bitmap GetBitmap(){
        return this.bitmap;
    }

    public void SetListener(BitmapChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void FFTReady(float[] fft) {
        this.Update(fft);

        if(null != listener){
            this.listener.Update();
        }
    }
}
