package com.example.lab2.Service;

import android.app.Service;
import android.content.Intent;
import android.media.SoundPool;
import android.os.IBinder;

import com.example.lab2.R;
import com.example.lab2.WorkoutActivity;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {
    private ScheduledExecutorService service;
    private SoundPool soundPool;

    private int soundPreparation;
    private int soundFinal;
    private int currentTime;

    private String name;
    private ScheduledFuture<?> scheduledFuture;

    public void onCreate() {
        super.onCreate();
        soundPool = new SoundPool.Builder().setMaxStreams(5).build();

        soundPreparation = soundPool.load(this, R.raw.censore_preview, 1);
        soundFinal = soundPool.load(this, R.raw.final_sound, 1);
        service = Executors.newScheduledThreadPool(1);
    }

    public void onDestroy() {
        service.shutdownNow();
        scheduledFuture.cancel(true);

        Intent intent = new Intent(WorkoutActivity.BROADCAST_ACTION);
        intent.putExtra(WorkoutActivity.CURRENT_ACTION, "pause");
        intent.putExtra(WorkoutActivity.NAME_ACTION, name);
        intent.putExtra(WorkoutActivity.TIME_ACTION, Integer.toString(currentTime));
        sendBroadcast(intent);

        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        int time = Integer.parseInt(intent.getStringExtra(WorkoutActivity.PARAM_START_TIME));
        name = intent.getStringExtra(WorkoutActivity.NAME_ACTION);
        ServiceTimerTask mr = new ServiceTimerTask(time, name);
        if (scheduledFuture != null) {
            service.schedule(() -> {
                scheduledFuture.cancel(true);
                scheduledFuture = service.scheduleAtFixedRate(mr, 0, time + 1, TimeUnit.SECONDS);
            }, 1000, TimeUnit.MILLISECONDS);
        }
        else {
            scheduledFuture = service.scheduleAtFixedRate(mr, 0, time + 1, TimeUnit.SECONDS);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class ServiceTimerTask extends TimerTask {

        private final int time;
        private final String name;

        public ServiceTimerTask(int time, String name) {
            this.time = time;
            this.name = name;
        }

        public void run() {
            Intent intent = new Intent(WorkoutActivity.BROADCAST_ACTION);
            if (name.equals(getResources().getString(R.string.finish))) {
                intent.putExtra(WorkoutActivity.CURRENT_ACTION, "work");
                intent.putExtra(WorkoutActivity.NAME_ACTION, name);
                intent.putExtra(WorkoutActivity.TIME_ACTION, "");
                sendBroadcast(intent);
            }
            try {
                for (currentTime = time; currentTime > 0; currentTime--) {
                    intent.putExtra(WorkoutActivity.CURRENT_ACTION, "work");
                    intent.putExtra(WorkoutActivity.NAME_ACTION, name);
                    intent.putExtra(WorkoutActivity.TIME_ACTION, Integer.toString(currentTime));
                    sendBroadcast(intent);
                    TimeUnit.SECONDS.sleep(1);
                    signalSound(currentTime);
                }
                intent = new Intent(WorkoutActivity.BROADCAST_ACTION);
                intent.putExtra(WorkoutActivity.CURRENT_ACTION, "clear");
                sendBroadcast(intent);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void signalSound(int time) {
            if (time <= 4) {
                if (time == 1) {
                    soundPool.play(soundFinal, 1, 1, 0, 0, 1);
                }
                else {
                    soundPool.play(soundPreparation, 1, 1, 0, 0, 1);
                }
            }
        }
    }
}
