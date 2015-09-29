package com.example.braeden.waveswap_app;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.braeden.waveswap_app.Fragments.HomeScreenFragment;

public class MainActivity extends FragmentActivity {

    private Button _goHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = this.getLayoutInflater().inflate(R.layout.activity_main,  null);
        this.setContentView(root);

        this._goHomeButton = (Button)root.findViewById(R.id.activity_gohomebutton);
        this._goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.MainActivityFrameLayout, HomeScreenFragment.newInstance());
                transaction.commit();
            }
        });


        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.MainActivityFrameLayout, HomeScreenFragment.newInstance());
        transaction.commit();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_home) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.MainActivityFrameLayout, HomeScreenFragment.newInstance());
            transaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }
}