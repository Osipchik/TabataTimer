package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.main_toolbar));

        findViewById(R.id.settings_button).setOnClickListener(i -> {
            Intent intent = new Intent(this, Settings.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    public void onCreateButtonClick(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }
}