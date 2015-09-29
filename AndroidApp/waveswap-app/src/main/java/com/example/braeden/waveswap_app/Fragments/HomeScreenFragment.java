package com.example.braeden.waveswap_app.Fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.braeden.waveswap_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeScreenFragment extends Fragment {

    private Button _buttonListen;
    private Button _buttonPlay;
    private Button _buttonLogo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeScreenFragment newInstance() {
        HomeScreenFragment fragment = new HomeScreenFragment();
        return fragment;
    }

    public HomeScreenFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        SetupButtons(view);

        return view;
    }

    /**
     * Set up all of the buttons
     * @param view the view...
     */
    private void SetupButtons(View view) {
        this._buttonListen = (Button)view.findViewById(R.id.homescreen_listenbutton);
        this._buttonPlay = (Button)view.findViewById(R.id.homescreen_playbutton);
        this._buttonLogo = (Button)view.findViewById(R.id.homepage_logobutton);

        // add the click listeners
        this.RegisterButtonCallback(this._buttonListen, ListenFragment.newInstance());
        this.RegisterButtonCallback(this._buttonPlay, PlaySoundFragment.newInstance());
        this.RegisterButtonCallback(this._buttonLogo, LogoFragment.newInstance());

    }

    /**
     * Register a button on click listener
     * @param button the button
     * @param fragment the fragment to open
     */
    private void RegisterButtonCallback(Button button, final Fragment fragment) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transactionManager = getFragmentManager().beginTransaction();
                transactionManager.replace(R.id.MainActivityFrameLayout, fragment);
                transactionManager.commit();
            }
        });
    }

}
