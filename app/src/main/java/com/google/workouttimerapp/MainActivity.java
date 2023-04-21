package com.google.workouttimerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.workouttimerapp.utils.MediaPlayerUtils;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

public class MainActivity extends AppCompatActivity {
    private EditText workoutTime, restTime, timesSet;
    private Button startBtn, stopBtn;
    private TextView state, timeTv;
    private ProgressBar progressBar;

    private String workout, rest, timeNum;
    private boolean isWorkOut = false;
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private CountDownTimer countDownTimer;

    private void initView() {
        startBtn = findViewById(R.id.stateBtn);
        stopBtn = findViewById(R.id.stopBtn);
        state = findViewById(R.id.state);
        timeTv = findViewById(R.id.time);
        workoutTime = findViewById(R.id.workoutTime);
        progressBar = findViewById(R.id.progressBar);
        restTime = findViewById(R.id.restTime);
        timesSet = findViewById(R.id.timesSet);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workout = workoutTime.getText().toString();
                rest = restTime.getText().toString();
                timeNum = timesSet.getText().toString();

                if (workout.isEmpty() || rest.isEmpty() || timeNum.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter the number of workout sets, workout time, and rest time", Toast.LENGTH_SHORT).show();
                    return;
                }

                MessageDialog.show("Start " + timeNum + " sets of workout?", "", "Yes", "No")
                        .setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {
                                times = 0;
                                isWorkOut = true;
                                startWork(Long.parseLong(workout));
                                progressBar.setMax(Integer.parseInt(timeNum));
                                return false;
                            }
                        }).setCancelButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {
                                return false;
                            }
                        });
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    timeTv.setText("0s");
                    progressBar.setProgress(0);
                    times = 0;
                    state.setText("Workout complete");
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void startWork(long time) {
        state.setText("The " + (times + 1) + "set workout");
        MediaPlayerUtils.release();
        state.setTextColor(0xFF018786);
        times++;
        progressBar.setProgress(times);
        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("Remain time", "seconds remaining: " + millisUntilFinished / 1000);
                timeTv.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                if (times == Integer.parseInt(timeNum)) {
                    state.setText("Workout complete");
                    state.setTextColor(0xFF6E6E6E);
                    times = 0;
                    countDownTimer.cancel();
                    MessageDialog.show("Workout complete", "", "Yes")
                            .setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                                @Override
                                public boolean onClick(MessageDialog dialog, View v) {
                                    return false;
                                }
                            });
                } else {
                    if (isWorkOut) {
                        isWorkOut = false;
                        startRest(Long.parseLong(rest));
                    }
                }
                Log.e("Complete", "done!");
            }
        };

        countDownTimer.start();
    }

    private void startRest(long time) {
        MediaPlayerUtils.playSound(this, R.raw.rest);
        state.setText("Resting");
        state.setTextColor(0xFFFF0000);
        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("Remain time", "seconds remaining: " + millisUntilFinished / 1000);
                timeTv.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                Log.e("Complete", "done!");
                if (!isWorkOut) {
                    isWorkOut = true;
                    startWork(Long.parseLong(workout));
                }
            }
        };

        countDownTimer.start();
    }
}