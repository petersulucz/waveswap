package com.example.braeden.waveswap_app.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.example.braeden.waveswap_app.R;
import com.example.braeden.waveswap_app.Views.BitmapView;
import com.example.braeden.waveswap_app.audio.input.AudioInput;
import com.example.braeden.waveswap_app.audio.input.FFTBitmap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListenFragment extends Fragment {


    private Button listenbutton;
    private AudioInput audioInput;
    private FFTBitmap fftBitmap;
    private boolean listening = false;
    private FrameLayout frame;
    private BitmapView bitmapView;
    private SeekBar seekbar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListenFragment.
     */
    public static ListenFragment newInstance() {
        ListenFragment fragment = new ListenFragment();
        return fragment;
    }

    public ListenFragment() {
        // Required empty public constructor


    }

    private void InitListener(){
        this.audioInput = new AudioInput();
        this.fftBitmap = new FFTBitmap(this.audioInput.GetResoution());
        this.audioInput.SetFFTListener(this.fftBitmap);
        this.bitmapView.SetBitMap(this.fftBitmap.GetBitmap());
        this.fftBitmap.SetListener(this.bitmapView);
        this.fftBitmap.SetSensitivity(this.seekbar.getProgress());
        this.audioInput.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listen, container, false);


        this.seekbar = (SeekBar)view.findViewById(R.id.listenfragment_seekbar);
        this.seekbar.setProgress(10000);
        this.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fftBitmap != null){
                    fftBitmap.SetSensitivity(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.listenbutton = (Button)view.findViewById(R.id.listenfragment_listen_button);
        this.frame = (FrameLayout)view.findViewById(R.id.listen_fragment_frame);
        this.bitmapView = new BitmapView(this.getActivity());


        this.frame.addView(this.bitmapView);

        this.listenbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listening = !listening;
                if(true == listening){
                    listenbutton.setText("Listening...");
                    InitListener();
                }else {
                    audioInput.Cancel();
                    listenbutton.setText("Begin");
                }
            }
        });
        return view;
    }
}
