package com.example.braeden.waveswap_app.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

import com.example.braeden.waveswap_app.audio.input.BitmapChangeListener;

/**
 * Created by peter on 10/7/2015.
 */
public class BitmapView extends View implements BitmapChangeListener {
    private Bitmap bitmap;

    public BitmapView(Context context) {
        super(context);
    }

    private Matrix scaleMatrix;

    public void SetBitMap(Bitmap bitmap){
        this.bitmap = bitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) getWidth()) / width;
        float scaleHeight = ((float) getHeight()) / height;
        this.scaleMatrix = new Matrix();
        this.scaleMatrix.postScale(scaleWidth, scaleHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null != bitmap) {

            canvas.drawBitmap(bitmap, this.scaleMatrix, null);

        }
    }

    @Override
    public void Update() {
        postInvalidate();
    }
}
