package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.DataBase.AppDatabase;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listView;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.main_toolbar));

        database = App.getInstance().getDatabase();


        TimerAdapter adapter = new TimerAdapter(this, R.layout.timer_list_item, database.timerDao().getAll(), database);


        listView = (RecyclerView)findViewById(R.id.timers);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
//            {
////                TimerModel training = (TimerModel) parent.getItemAtPosition(position);
////                Intent intent = new Intent(getApplicationContext(), TrainingTimer.class);
////                intent.putExtra("timerId", training.Id);
////                startActivity(intent);
//            }
//        });

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
        Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
        intent.putExtra("timerId", new int[]{0,0});
        startActivity(intent);
    }
}