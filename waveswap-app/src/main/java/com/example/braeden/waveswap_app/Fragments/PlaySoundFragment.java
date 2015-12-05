package com.example.braeden.waveswap_app.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.braeden.waveswap_app.R;
import com.example.braeden.waveswap_app.waveswapAPI.SenderParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaySoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaySoundFragment extends Fragment {
    static final int BROWSE_IMAGE = 8;
    static final int CAPTURE_IMAGE = 9;

    private int count = 0;

    private ImageButton _imageButton;
    private Button _transferButton;
    private Button _browseButton;
    private EditText _editText;

    private String _filePath = null;
    private String outputFile = null;

    private Uri imageUri;
    private MediaPlayer player;

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
        player = new MediaPlayer();
        SetupButtons(view);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE && resultCode == getActivity().RESULT_OK) {
            Bitmap bitmap = retrieveBitmap(true);
            _imageButton.setImageBitmap(bitmap);
        } else if (requestCode == BROWSE_IMAGE && resultCode == getActivity().RESULT_OK) {
            imageUri = data.getData();
            Bitmap bitmap = retrieveBitmap(false);
            _imageButton.setImageBitmap(bitmap);
        } else {
            return;
        }

        // Retrieve and display _filePath
        _filePath = imageUri.getPath();
        displayToast(_filePath);
    }

    private void SetupButtons(View view) {
        _imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        _browseButton = (Button) view.findViewById(R.id.browseButton);
        _transferButton = (Button) view.findViewById(R.id.transferButton);
        _editText = (EditText) view.findViewById(R.id.editText);

        _browseButton.setText("Enter Text");

        // Set browse onClick
        _browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //browseClicked();
                textClicked();
            }
        });

        // Set transfer onClick
        _transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferClicked();
            }
        });

        // Set transfer onClick
        _imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonClicked();
            }
        });
    }

    private void browseClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, BROWSE_IMAGE);
    }

    private void textClicked() {
        if (_editText.getText().toString().equals("")) {
            displayToast("Please enter a phrase!");
            return;
        }
        String text = _editText.getText().toString();
        //createAudio(text.getBytes());
        //playAudio(outputFile);
        playSound(text.getBytes());
    }

    private void playSound(byte[] text) {
        // AudioTrack definition
        int mBufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);


        float[] phis = new float[4];
        float[] frequencies = new float[]{14000, 14500, 15000, 15500};

        // Sine wave
        double[] mSound = new double[2*44100];
        short[] mBuffer = new short[2*44100];
        for (int i = 0; i < mSound.length; i++) {
            float freq = frequencies[i / (mSound.length / frequencies.length)];
            phis[0] += (2.0*Math.PI * freq/44100.0);
            mSound[i] = Math.sin(phis[0]);
            mBuffer[i] = (short) (mSound[i]*Short.MAX_VALUE);
        }

        //mAudioTrack.setStereoVolume(1f, 11f);
        mAudioTrack.play();

        mAudioTrack.write(mBuffer, 0, mSound.length);
        mAudioTrack.stop();
        mAudioTrack.release();

    }

    private void transferClicked() {
        if (_transferButton.getText().equals("Stop")){
            player.stop();
            _transferButton.setText("Play");
            return;
        } else if (_transferButton.getText().equals("Play")) {
            playAudio(outputFile);
            return;
        }
        if (imageUri == null || _filePath == null) {
            displayToast("No file to transfer");
            return;
        }

        File transferFile = new File(imageUri.getPath());
        createAudio(parseFile(transferFile));
    }

    public void createAudio(byte[] fileData) {
        if (fileData == null) return;
        SenderParser sender = new SenderParser();

        // Create path to output file
        outputFile = getActivity().getFilesDir() + "/file";
        sender.createAudioFile(fileData, outputFile, 2, 8000, 2000, 4000, SenderParser.BIT_BY_BIT);
        displayToast("Playing audio");
        playAudio(outputFile);
    }

    private void playAudio(String path) {
        if (path == null) {
            displayToast("Improper audio file");
            return;
        }
        try {
            if (count == 0) {
                player.setDataSource(path);
                player.setLooping(true);
                count++;
            }
            player.prepare();
            player.start();
            _transferButton.setText("Stop");
        } catch (IOException e) {
            e.printStackTrace();
            displayToast("Error playing audio");
            return;
        }

    }

    private byte[] parseFile(File file) {
        byte[] fileData = null;
        try {
            // Put file bytes into fileData byte array
            fileData = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            displayToast("Could not parse file");
        }
        return fileData;
    }

    private void imageButtonClicked() {
        Intent getPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getPicture.resolveActivity(getActivity().getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                displayToast("File could not be loaded");
                return;
            }

            if (imageFile != null) {
                imageUri = Uri.fromFile(imageFile);
                getPicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(getPicture, CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private Bitmap retrieveBitmap (boolean shouldRotate) {
        try {
            getActivity().getContentResolver().notifyChange(imageUri, null);
            Matrix mat = new Matrix();
            if (shouldRotate) {
                mat.postRotate(90);
            }
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
            bitmap = Bitmap.createScaledBitmap(bitmap, _imageButton.getWidth() - 50, _imageButton.getHeight() - 50, true);
            return bitmap;
        } catch (Exception exception) {
            return null;
        }
    }

    private void displayToast(String text) {
        Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
