package com.example.braeden.waveswap_app.audio.input;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import com.example.braeden.waveswap_app.CaptainsLog;
import com.example.braeden.waveswap_app.audio.process.Butterworth;

/**
 * Created by peter on 10/7/2015.
 */
public class AudioInput extends AsyncTask<Void, Void,  Void> {

    private AudioRecord recorder;
    private int bufferSize = 256;
    private short[] buffer;
    private boolean[] values;
    private volatile boolean cancel = false;
    private FFTListener listener;

    public AudioInput()
    {
        //this.bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    public void SetFFTListener(FFTListener listener)
    {
        this.listener = listener;
    }

    public int GetResoution()
    {
        return this.bufferSize;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        this.recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, this.bufferSize);
        this.buffer = new short[this.bufferSize];
    }

    public void Cancel(){
        this.cancel = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // start recording
        this.recorder.startRecording();
        FastFourierTransform transform = new FastFourierTransform(this.GetResoution());

        do{

            float[] imaginary = new float[this.bufferSize];
            float[] real = new float[this.bufferSize];

            int samplesRead =  this.recorder.read(this.buffer, 0, this.bufferSize);

            for (int i = 0; i < real.length; i++){
                real[i] = (float)this.buffer[i];
            }

            //Butterworth.ButterWorth(real);

            transform.fft(real, imaginary);
            //transform.ZoomFFT(real, imaginary);
            this.listener.FFTReady(real);
            //12, 12.5, 13, 13.5
            int count = 0;
            values = new boolean[4];

            // we are looking at 3 frequencies
            float[] frequencyWeights = new float[4];

            count = 0;
            for (int freq = 15500; freq >= 14000; freq -= 500) {
                // get the weights of our 3 frequencies
                frequencyWeights[count++] = Math.abs(real[getIndex(freq, bufferSize)]);
            }

            // get the freq weights into a boolean array
            boolean[] values = ValueNormalizer.NormalizeValues(frequencyWeights, FFTBitmap.sensitivity);

            int summary = convertArray(values);
            //if(summary != 0)
                BraedensFFT.getInstance().addValue(summary);

        } while(false == this.cancel);
        CaptainsLog.Info("Stopped recording gracefully");
        this.recorder.stop();
        // there better be a better way to do this
        return null;
    }

    private int getIndex(int frequency, int bufferSize) {
        return (int)((double)frequency / (22050.0 / ((double)bufferSize / 2.0)));
    }

    private int convertArray(boolean[] arr) {
        int value = 0;
        for (int index = 0; index < arr.length; index++) {
            if (arr[index]) {
                value += 1;
            }
            value <<= 1;
        }
        return value>>1;
    }
}
