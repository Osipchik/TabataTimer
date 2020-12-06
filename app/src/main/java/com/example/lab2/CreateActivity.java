package com.example.lab2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.lab2.DataBase.AppDatabase;
import com.example.lab2.Models.TimerModel;

import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar;
import codes.side.andcolorpicker.model.IntegerHSLColor;

public class CreateActivity extends AppCompatActivity {
    private EditText inputName;
    private EditText inputPrep;
    private EditText inputWork;
    private EditText inputRest;
    private EditText inputCycle;
    private EditText inputSet;
    private EditText inputCalm;
    private HSLColorPickerSeekBar colorPicker;

    private AppDatabase database;
    private CreateViewModel viewModel;
    private TimerModel timerModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        database = App.getInstance().getDatabase();
        viewModel = ViewModelProviders.of(this).get(CreateViewModel.class);

        setInputs();
        setButtonObserver();
        setInputObservers();

        initInputs(getIds());

        inputName.setOnKeyListener(this::onNameKeyListener);
    }

    private boolean onNameKeyListener(View view, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            viewModel.setName(inputName.getText().toString());
            if(keyCode == 4)
            {
                Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backIntent);
                finish();
                return true;
            }
            return true;
        }
        return false;
    }

    private int[] getIds() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        return  (int[])bundle.get("timerId");
    }

    private void initInputs(int[] ids) {
        if(ids[1] == 1){
            timerModel = database.timerDao().getById(ids[0]);
            viewModel.setName(timerModel.Name);
            viewModel.setPrep(timerModel.Preparation);
            viewModel.setWork(timerModel.WorkTime);
            viewModel.setRest(timerModel.RestTime);
            viewModel.setCycle(timerModel.Cycles);
            viewModel.setSets(timerModel.Sets);
            viewModel.setRestSets(timerModel.RestSets);
            viewModel.setColor(timerModel.Color);
            colorPicker.setPickedColor(convertToIntegerHSLColor(timerModel.Color));
        }
    }

    private void setButtonObserver() {
        findViewById(R.id.btn_prepare_plus).setOnClickListener(i -> viewModel.setIncrementPreparation());
        findViewById(R.id.btn_prepare_minus).setOnClickListener(i -> viewModel.setDecrementPreparation());

        findViewById(R.id.btn_work_plus).setOnClickListener(i -> viewModel.setIncrementWorkTime());
        findViewById(R.id.btn_work_minus).setOnClickListener(i -> viewModel.setDecrementWorkTime());

        findViewById(R.id.btn_rest_plus).setOnClickListener(i -> viewModel.setIncrementRestTime());
        findViewById(R.id.btn_rest_minus).setOnClickListener(i -> viewModel.setDecrementRestTime());

        findViewById(R.id.btn_cycle_plus).setOnClickListener(i -> viewModel.setIncrementCycle());
        findViewById(R.id.btn_cycle_minus).setOnClickListener(i -> viewModel.setDecrementCycle());

        findViewById(R.id.btn_set_plus).setOnClickListener(i -> viewModel.setIncrementSets());
        findViewById(R.id.btn_set_minus).setOnClickListener(i -> viewModel.setDecrementSets());

        findViewById(R.id.btn_calm_plus).setOnClickListener(i -> viewModel.setIncrementRestSets());
        findViewById(R.id.btn_calm_minus).setOnClickListener(i -> viewModel.setDecrementRestSets());

        findViewById(R.id.btn_cancel).setOnClickListener(i -> quitHandler());
        findViewById(R.id.btn_save).setOnClickListener(i -> quitSave());
    }

    private void setInputs(){
        inputName = findViewById(R.id.input_name);
        inputPrep = findViewById(R.id.input_prep);
        inputWork = findViewById(R.id.input_work);
        inputRest = findViewById(R.id.input_rest);
        inputCycle = findViewById(R.id.input_cycle);
        inputSet = findViewById(R.id.input_set);
        inputCalm = findViewById(R.id.input_calm);
        colorPicker = findViewById(R.id.color_picker);
    }

    private void setInputObservers(){
        viewModel.getName().observe(this, val -> inputName.setText(val));
        viewModel.getPreparation().observe(this, val -> inputPrep.setText(val.toString()));
        viewModel.getWorkTime().observe(this, val -> inputWork.setText(val.toString()));
        viewModel.getRestTime().observe(this, val -> inputRest.setText(val.toString()));
        viewModel.getCycles().observe(this, val -> inputCycle.setText(val.toString()));
        viewModel.getSets().observe(this, val -> inputSet.setText(val.toString()));
        viewModel.getRestSets().observe(this, val -> inputCalm.setText(val.toString()));
    }

    private void quitHandler() {
        Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backIntent);
        finish();
    }

    private void initModel(TimerModel timerModel) {
        timerModel.Name = inputName.getText().toString();
        timerModel.Preparation = Integer.parseInt(inputPrep.getText().toString());
        timerModel.WorkTime = Integer.parseInt(inputWork.getText().toString());
        timerModel.RestTime = Integer.parseInt(inputRest.getText().toString());
        timerModel.Cycles = Integer.parseInt(inputCycle.getText().toString());
        timerModel.Sets = Integer.parseInt(inputSet.getText().toString());
        timerModel.RestSets = Integer.parseInt(inputCalm.getText().toString());

        IntegerHSLColor ii = colorPicker.getPickedColor();
        timerModel.Color = Color.HSVToColor(new float[]{ii.getFloatH(), ii.getFloatL(), ii.getFloatS()});
    }

    private void quitSave() {
        saveDialogHandler();

        quitHandler();
    }

    private void saveDialogHandler() {
        int[] ids = getIds();

        if (ids[1] != 1) {
            TimerModel model = new TimerModel();
            initModel(model);
            database.timerDao().insert(model);
        }
        else {
            initModel(timerModel);
            database.timerDao().update(timerModel);
        }

        Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backIntent);
        finish();
    }


    private IntegerHSLColor convertToIntegerHSLColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        IntegerHSLColor integerHSLColor = new IntegerHSLColor();
        integerHSLColor.setFloatH(hsv[0]);
        integerHSLColor.setFloatL(hsv[1]);
        integerHSLColor.setFloatS(hsv[2]);
        return integerHSLColor;
    }
}