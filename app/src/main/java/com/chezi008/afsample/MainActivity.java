package com.chezi008.afsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.chezi008.afplayersp.R;

public class MainActivity extends AppCompatActivity {

    private Button btnAFPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAFPlayer = findViewById(R.id.btnAFPlayer);
        btnAFPlayer.setOnClickListener(v->{
            AFPlayerActivity.start(this);
        });
    }
}