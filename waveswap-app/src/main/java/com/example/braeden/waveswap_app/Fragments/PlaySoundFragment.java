package com.example.braeden.waveswap_app.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.braeden.waveswap_app.R;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaySoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaySoundFragment extends Fragment {
    static final int GET_FILE = 8;

    private ImageButton _imageButton;
    private Button _transferButton;
    private Button _browseButton;

    private String _filePath = null;
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
        View view = inflater.inflate(R.layout.fragment_play_sound, container, false);
        SetupButtons(view);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FILE && resultCode == getActivity().RESULT_OK) {
            if (data == null) {
                Toast.makeText(getActivity().getApplicationContext(), "Image could not be loaded",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap bitmap = null;
            Uri imageUri = data.getData();
            _filePath = imageUri.getPath();

            Toast.makeText(getActivity().getApplicationContext(), _filePath,
                    Toast.LENGTH_LONG).show();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                _imageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Image could not be loaded",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    private void SetupButtons(View view) {
        _imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        _browseButton = (Button) view.findViewById(R.id.browseButton);
        _transferButton = (Button) view.findViewById(R.id.transferButton);

        // Set browse onClick
        _browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseClicked();
            }
        });

        // Set transfer onClick
        _transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferClicked();
            }
        });
    }

    private void browseClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GET_FILE);
    }

    public void transferClicked() {
        /** Currently implementing */
    }

}
