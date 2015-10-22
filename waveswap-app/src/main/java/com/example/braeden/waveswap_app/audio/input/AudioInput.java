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
    private int bufferSize = 8192;
    private short[] buffer;
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

            Butterworth.ButterWorth(real);

            transform.fft(real, imaginary);
            //transform.ZoomFFT(real, imaginary);
            this.listener.FFTReady(real);

        }while(false == this.cancel);
        CaptainsLog.Info("Stopped recording gracefully");
        this.recorder.stop();
        // there better be a better way to do this
        return null;
    }


}
