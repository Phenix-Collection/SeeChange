package com.thecirkel.seechange.activities;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.thecirkel.seechange.R;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, ConnectCheckerRtmp {


    private RtmpCamera1 camera;
    private SurfaceView cameraPreview;
    private Fragment chatFragment;

    private TextView liveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraPreview = findViewById(R.id.cameraView);
        camera = new RtmpCamera1(cameraPreview, this);

        liveText = findViewById(R.id.liveText);
        liveText.setVisibility(View.INVISIBLE);

        chatFragment = getFragmentManager().findFragmentById(R.id.chatFragment);
        chatFragment.getView().setVisibility(View.GONE);

        camera.startPreview();
    }

    public void goToChat(View v) {
        chatFragment.getView().setVisibility(View.VISIBLE);
    }

    public void startStopCamera(View v) {
        if (!camera.isStreaming()) {
            if (camera.isRecording()
                    || camera.prepareAudio() && camera.prepareVideo()) {
                liveText.setText("Starting stream...");
                camera.startStream("rtmp://live-ams.twitch.tv/app/live_229618731_AUvc24gV8uYsDyrNZz1a6QSDWkIIEX");
            } else {
                Toast.makeText(this, "Error preparing stream, This device cant do it",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            liveText.setVisibility(View.INVISIBLE);
            camera.stopStream();
        }
    }

    public void switchCamera(View v) {
        try {
            camera.switchCamera();
        } catch (CameraOpenException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                liveText.setText("Live");
                liveText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                        .show();
                camera.stopStream();
            }
        });
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                liveText.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera.isStreaming()) {
            camera.stopStream();
            liveText.setVisibility(View.INVISIBLE);
        }
        camera.stopPreview();
    }

}
