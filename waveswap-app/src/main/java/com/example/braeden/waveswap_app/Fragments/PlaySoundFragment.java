package com.example.braeden.waveswap_app.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.braeden.waveswap_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaySoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaySoundFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlaySoundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaySoundFragment newInstance() {
        PlaySoundFragment fragment = new PlaySoundFragment();
        return fragment;
    }

    public PlaySoundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_sound, container, false);
    }
}
