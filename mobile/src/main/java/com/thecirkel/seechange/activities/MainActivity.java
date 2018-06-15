package com.thecirkel.seechange.activities;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.thecirkel.seechange.R;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.socket.client.IO;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, ConnectCheckerRtmp {


    private RtmpCamera1 camera;
    private SurfaceView cameraPreview;
    private Fragment chatFragment;

    private TextView liveText;
    private ImageView recordButton;
    private String PRIVATEKEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recordButton = findViewById(R.id.recordButton);

        liveText = findViewById(R.id.liveText);
        liveText.setVisibility(View.INVISIBLE);

        chatFragment = getFragmentManager().findFragmentById(R.id.chatFragment);
        chatFragment.getView().setVisibility(View.GONE);

        cameraPreview = findViewById(R.id.cameraView);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            camera = new RtmpCamera1(cameraPreview, this);
            cameraPreview.getHolder().addCallback(this);
        }else{
            CheckPermissions();
        }

    }

    public void CheckPermissions(){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.
                    System.out.println("PERMISSION GRANTED");
                    camera = new RtmpCamera1(cameraPreview, this);
                    cameraPreview.getHolder().addCallback(this);
                    camera.startPreview();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    CheckPermissions();
                }
                return;
            }
        }
            // other 'case' lines to check for other
            // permissions this app might request.
    }



    public void goToChat(View v) {
        chatFragment.getView().setVisibility(View.VISIBLE);
    }

    public void startStopCamera(View v) {
        if (!camera.isStreaming()) {
            if (camera.isRecording()
                    || camera.prepareAudio() && camera.prepareVideo()) {
                liveText.setText("Starting stream...");
                liveText.setVisibility(View.VISIBLE);
                camera.startStream("rtmp://188.166.127.54/play/test");
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

    public void hideChat(View v) {
        chatFragment.getView().setVisibility(View.GONE);
    }

    private void startedUI() {
        recordButton.setImageResource(R.drawable.ic_action_playback_stop);
        liveText.setText("Live");
        liveText.setVisibility(View.VISIBLE);
    }

    private void stoppedUI() {
        recordButton.setImageResource(R.drawable.ic_action_record);
        liveText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startedUI();
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
                stoppedUI();
            }
        });
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stoppedUI();
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
            stoppedUI();
        }
        camera.stopPreview();
    }


}
