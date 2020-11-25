package com.example.lab2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar;
import codes.side.andcolorpicker.model.IntegerHSLColor;

public class CreateActivity extends AppCompatActivity {

    private CreateViewModel viewModel;

    private EditText inputName;
    private EditText inputPrep;
    private EditText inputWork;
    private EditText inputRest;
    private EditText inputCycle;
    private EditText inputSet;
    private EditText inputCalm;
    private HSLColorPickerSeekBar colorPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        viewModel = ViewModelProviders.of(this).get(CreateViewModel.class);

        initInputs();
        setButtonObserver();
        setInputObservers();
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

        findViewById(R.id.btn_cancel).setOnClickListener(i -> openQuitDialogCancel());
        findViewById(R.id.btn_save).setOnClickListener(i -> openQuitDialogSave());
    }

    private void initInputs(){
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

    private void openQuitDialogCancel() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(CreateActivity.this);
        quitDialog.setTitle(getResources().getString(R.string.cancel));
        quitDialog.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
            Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(backIntent);
            finish();
        });
        quitDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {});
        quitDialog.show();
    }

    private void openQuitDialogSave() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(CreateActivity.this);
        quitDialog.setTitle(getResources().getString(R.string.save_message));
        quitDialog.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
            Intent intent = getIntent();
            Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(backIntent);
            finish();
        });
        quitDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
            onBackPressed();
        });
        quitDialog.show();
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