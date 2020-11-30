package com.example.lab2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.Models.TimerModel;
import com.example.lab2.Service.TimerService;

import java.util.ArrayList;
import java.util.Objects;

public class WorkoutActivity extends AppCompatActivity {

    private TimerModel timerModel;

    private BroadcastReceiver receiver;
    private ListView listTraining;
    private ImageButton startButton;
    private ArrayList<String> WorkoutSteps = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public final static String PARAM_START_TIME = "start_time";
    public final static String NAME_ACTION = "name";
    public final static String TIME_ACTION = "time";
    public final static String CURRENT_ACTION = "pause";
    public final static String BROADCAST_ACTION = "com.example.lab2";

    private int element = 0;
    private boolean checkLastSec = false;
    private String valueStatusPause = "";
    private String valueTimePause = "";
    private int valueElementPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int itemId = (int) bundle.get("timerId");
        timerModel = App.getInstance().getDatabase().timerDao().getById(itemId);

        setSupportActionBar(findViewById(R.id.main_toolbar));
        getSupportActionBar().setTitle(timerModel.Name);
        findViewById(R.id.main_toolbar).setBackgroundColor(timerModel.Color);

        startButton = findViewById(R.id.button_start);
        listTraining = findViewById(R.id.items);

        receiver = createReceiver();

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(receiver, intentFilter);

        fillAdapter(timerModel);

        startButton.setOnClickListener(this::onStartClick);
        startButton.setBackground(getResources().getDrawable(R.drawable.ic_start));


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, WorkoutSteps);
        listTraining.setAdapter(adapter);
        listTraining.setOnItemClickListener((parent, view, position, id) -> ChangeFieldListView(view, position));
        listTraining.setOnScrollListener(createOnScrollListener());
    }

    private AbsListView.OnScrollListener createOnScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for (int i = 0; i < visibleItemCount; i++) {
                    listTraining.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorPrimary,getTheme()));
                }

                if (element >= firstVisibleItem && element < firstVisibleItem + visibleItemCount && !checkLastSec) {
                    listTraining.getChildAt(element - firstVisibleItem).setBackgroundColor(getResources().getColor(R.color.colorAccent,getTheme()));
                }
                if (element - 1 >= firstVisibleItem && element - 1 < firstVisibleItem + visibleItemCount && checkLastSec) {
                    listTraining.getChildAt(element - 1 - firstVisibleItem).setBackgroundColor(getResources().getColor(R.color.colorAccent,getTheme()));
                }
            }
        };
    }

    private BroadcastReceiver createReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getStringExtra(CURRENT_ACTION), "work")) {
                    String task = intent.getStringExtra(NAME_ACTION);
                    String status = intent.getStringExtra(TIME_ACTION);

                    if (status.equals("1")) {
                        workLastSec();
                    }
                    else {
                        workInProgress(task);
                    }
                    ((TextView) findViewById(R.id.part_name)).setText(task);
                    ((TextView) findViewById(R.id.part_time)).setText(status);
                }
                else if (Objects.equals(intent.getStringExtra(CURRENT_ACTION), "clear")) {
                    clear();
                }
                else {
                    valueStatusPause = intent.getStringExtra(NAME_ACTION);
                    valueTimePause = intent.getStringExtra(TIME_ACTION);
                    startPause(valueTimePause);
                }
            }
        };
    }

    public void clear() {
        int index = element - listTraining.getFirstVisiblePosition();

        if (element != 0 && index - 1 < 14 && index - 1 >= 0 && element != 0) {
            listTraining.getChildAt(index - 1).setBackgroundColor(getResources().getColor(R.color.colorPrimary,getTheme()));
        }

        if (index < 14 && index - 1 >= 0) {
            listTraining.getChildAt(index).setBackgroundColor(getResources().getColor(R.color.colorAccent,getTheme()));
        }
    }

    public void workInProgress(String task) {
        if (checkLastSec) {
            checkLastSec = false;
        }

        valueStatusPause = "";
        if (task.equals(getResources().getString(R.string.finish))) {
            startButton.setOnClickListener(WorkoutActivity.this::onStartClick);
            startButton.setBackground(getResources().getDrawable(R.drawable.ic_start));
        }
    }

    public void workLastSec() {
        element++;
        checkLastSec = true;
        if (element < adapter.getCount()) {
            String[] words = Objects.requireNonNull(adapter.getItem(element)).split(" : ");

            String time = words.length == 2 ? "0" : words[2];
            AddNewService(words[1], time);
        }
    }

    public void AddNewService(String name, String time) {
        startService(new Intent(this, TimerService.class)
                .putExtra(PARAM_START_TIME, time)
                .putExtra(NAME_ACTION, name));
    }

    public void startPause(String status) {
        if (status.equals("1")) {
            if (!checkLastSec) {
                element--;
            }
            else {
                checkLastSec = false;
            }

            String[] words = Objects.requireNonNull(adapter.getItem(element)).split(" : ");
            valueStatusPause = words[1];
        }
        valueElementPause = element;
    }

    public void fillAdapter(TimerModel workout) {
        int number = 1;
        int set = workout.Sets;
        if (workout.Preparation != 0) {
            WorkoutSteps.add(StringForTimer(number++, getResources().getString(R.string.preparation), workout.Preparation));
        }

        while (set > 0) {
            for (int cycle = workout.Cycles; cycle > 0; cycle--) {
                WorkoutSteps.add(StringForTimer(number++, getResources().getString(R.string.work), workout.WorkTime));
                WorkoutSteps.add(StringForTimer(number++, getResources().getString(R.string.rest), workout.RestTime));
            }

            set--;

            if (set != 0 && workout.RestSets != 0) {
                WorkoutSteps.add(StringForTimer(number++, getResources().getString(R.string.rest_between), workout.RestSets));
            }
        }

        WorkoutSteps.add(number + " : " + getResources().getString(R.string.finish));
    }

    public String StringForTimer(int number, String name, Integer time) {
        return number + " : " + name + " : " + time;
    }

    public void ChangeFieldListView(View view, int position) {
        for (int i = 0; i < listTraining.getLastVisiblePosition() - listTraining.getFirstVisiblePosition() + 1; i++) {
            listTraining.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorPrimary,getTheme()));
        }

        element = position;
        view.setBackgroundColor(getResources().getColor(R.color.colorAccent,getTheme()));

        stopService(new Intent(this, TimerService.class));

        String[] words = Objects.requireNonNull(adapter.getItem(position)).split(" : ");
        if (words.length == 2) {
            startButton.setBackground(getResources().getDrawable(R.drawable.ic_start));
            startButton.setOnClickListener(this::onStartClick);
            AddNewService(words[1], "0");
        }
        else {
            startButton.setOnClickListener(this::onResetClick);
            startButton.setBackground(getResources().getDrawable(R.drawable.ic_pause));
            AddNewService(words[1], words[2]);
        }
    }

    public void onStartClick(View view) {
        if (valueStatusPause.isEmpty() || valueStatusPause.equals(getResources().getString(R.string.finish))) {
            element = 0;
            checkLastSec = false;

            stopService(new Intent(this, TimerService.class));
            AddNewService(getResources().getString(R.string.preparation), String.valueOf(timerModel.Preparation));

            int childIndex = listTraining.getLastVisiblePosition() - listTraining.getFirstVisiblePosition();
            listTraining.getChildAt(childIndex)
                    .setBackgroundColor(getResources().getColor(R.color.colorPrimary,getTheme()));

            if (element >= listTraining.getFirstVisiblePosition() && element <= listTraining.getLastVisiblePosition()) {
                listTraining.getChildAt(element)
                        .setBackgroundColor(getResources().getColor(R.color.colorAccent,getTheme()));
            }
        }
        else {
            element = valueElementPause;
            AddNewService(valueStatusPause, valueTimePause);
        }

        startButton.setOnClickListener(this::onResetClick);
        startButton.setBackground(getResources().getDrawable(R.drawable.ic_pause));
    }

    public void onResetClick(View view) {
        startButton.setOnClickListener(this::onStartClick);
        startButton.setBackground(getResources().getDrawable(R.drawable.ic_start));
        stopService(new Intent(this, TimerService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, TimerService.class));
    }

    @Override
    public void onBackPressed() {
        unregisterReceiver(receiver);
        stopService(new Intent(this, TimerService.class));
        finish();
    }
}