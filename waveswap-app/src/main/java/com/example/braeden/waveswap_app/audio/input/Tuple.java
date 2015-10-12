package com.example.braeden.waveswap_app.audio.input;

/**
 * Created by peter on 10/7/2015.
 */
public class Tuple {
    private float x;
    private float y;

    public Tuple(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Tuple add(Tuple arg){
        return new Tuple(this.x + arg.x, this.y + arg.y);
    }

    public Tuple subtract(Tuple arg){
        return new Tuple(this.x - arg.x, this.y - arg.y);
    }

    public Tuple times(Tuple arg){
        return new Tuple(this.x* arg.x - this.y*arg.y, this.x*arg.y + this.y*arg.y);
    }

    public float GetX(){
        return this.x;
    }

    public float GetY(){
        return this.y;
    }
}
