package com.thecirkel.seechange;

import android.support.v4.app.Fragment;
import android.content.ActivityNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import net.ossrs.rtmp.ConnectCheckerRtmp;

/**
 * More documentation see:
 * {@link com.pedro.rtplibrary.base.Camera1Base}
 * {@link com.pedro.rtplibrary.rtmp.RtmpCamera1}
 */
public class CameraActivity extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {

    private RtmpCamera1 rtmpCamera1;
    private Button button;

    private String currentDateAndTime = "";
    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/rtmp-rtsp-stream-client-java");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        //surface view for preview
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        //start/stop button
        button = findViewById(R.id.b_start_stop);
        button.setOnClickListener(this);
        //switch camera button
        Button switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        //chat button starts new activity
        Button ChatActivity = findViewById(R.id.chat_button);
        ChatActivity.setOnClickListener(this);
        //rtmp library needs surfaceview
        rtmpCamera1 = new RtmpCamera1(surfaceView, this);
        surfaceView.getHolder().addCallback(this);

        Button backbutton = findViewById(R.id.back_button);
        backbutton.setOnClickListener(this);

        //certificate reading out sd storage
        File certificateFile = new File(Environment.getExternalStorageDirectory().toString() + "/Certificate/client.crt");

        try {
            InputStream is = new FileInputStream(certificateFile);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            X509Certificate caCert = (X509Certificate)cf.generateCertificate(is);

            PublicKey key = caCert.getPublicKey();

            System.out.println("public key : ");
            System.out.println(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

    }

    @Override
    //connection succes toast
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    //connection error toast
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                        .show();
                rtmpCamera1.stopStream();
                button.setText(R.string.start_button);
            }
        });
    }

    @Override
    //disconnection toast
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    //authentication error toast
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    //authenticatie succes toast
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    //onclick
    public void onClick(View view) {
        switch (view.getId()) {
            //start or stop button
            case R.id.b_start_stop:
                if (!rtmpCamera1.isStreaming()) {
                    if (rtmpCamera1.isRecording()
                            || rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                        //if streaming button is called stop
                        button.setText(R.string.stop_button);
                        //authorization
                        rtmpCamera1.setAuthorization("","");
                        //endpoint hardcoded
                        rtmpCamera1.startStream("rtmp://188.166.127.54/live/test");
                                //"rtmp://live-ams.twitch.tv/app/live_229618731_AUvc24gV8uYsDyrNZz1a6QSDWkIIEX");
                    } else {
                        Toast.makeText(this, "Error preparing stream, This device cant do it",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //if not streaming button is called start
                    button.setText(R.string.start_button);
                    rtmpCamera1.stopStream();
                }
                break;
                //switch camera button
            case R.id.switch_camera:
                try {
                    //simple method for switching camera(may break stream because mobile does not work propperly)
                    rtmpCamera1.switchCamera();
                } catch (CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.chat_button:
                try{
                    // Create new fragment and transaction
                    ChatFragment newFragment = new ChatFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                }catch (ActivityNotFoundException e){

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rtmpCamera1.startPreview();
    }

    @Override
    //stop using app stop streaming/recording
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && rtmpCamera1.isRecording()) {
            rtmpCamera1.stopRecord();
            Toast.makeText(this,
                    "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            currentDateAndTime = "";
        }
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
            button.setText(getResources().getString(R.string.start_button));
        }
        rtmpCamera1.stopPreview();
    }
}