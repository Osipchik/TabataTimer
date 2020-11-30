package com.example.lab2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.DataBase.AppDatabase;
import com.example.lab2.Models.TimerModel;

import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final int layout;
    private final List<TimerModel> timerModelList;
    private final AppDatabase database;
    private Context context;

    TimerAdapter(Context context, int resource, List<TimerModel> timerModels, AppDatabase database){
        this.timerModelList = timerModels;
        this.layout = resource;
        this.database = database;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TimerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(this.layout, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerAdapter.ViewHolder holder, int position) {
        TimerModel timerModel = timerModelList.get(position);
        holder.nameView.setText(timerModel.Name);
        holder.idView.setText((Integer.toString(timerModel.Id)));
        holder.layout.setBackgroundColor(timerModel.Color);

        holder.layout.setOnClickListener(v -> viewOnClickListener(timerModel.Id));
        holder.removeButton.setOnClickListener(i -> startTimer(timerModel, position));
        holder.editButton.setOnClickListener(i -> editTimer(timerModel));
    }

    private void viewOnClickListener(int itemId){
        Intent intent = new Intent(context, WorkoutActivity.class);
        intent.putExtra("timerId", itemId);
        context.startActivity(intent);
    }

    private void startTimer(TimerModel timerModel, int position) {
        database.timerDao().delete(timerModelList.get(position));
        timerModelList.remove(timerModel);
        notifyDataSetChanged();
    }

    private void editTimer(TimerModel timerModel) {
        Intent intent = new Intent(context, CreateActivity.class);
        intent.putExtra("timerId", new int[]{timerModel.Id, 1});
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return timerModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageButton removeButton, editButton;
        final TextView nameView, idView;
        final LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            removeButton = view.findViewById(R.id.item_remove_button);
            editButton = view.findViewById(R.id.item_edit_button);
            nameView = view.findViewById(R.id.item_name_view);
            idView = view.findViewById(R.id.item_id_view);
            layout = view.findViewById(R.id.timer_list_item);
        }
    }
}
