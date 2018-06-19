package com.thecirkel.seechange.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thecirkel.seechange.R;
import com.thecirkel.seechange.services.CertificateService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoActivity extends AppCompatActivity {

    CertificateService certificateService;
    ImageView closeInfo,avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        certificateService = new CertificateService();

        avatar = findViewById(R.id.avatar);


        closeInfo = findViewById(R.id.closeInfoButton);
        closeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                loadImage();
            }
        });
    }

    private void loadImage() {
        Picasso.get().load(certificateService.getAvatarsource()).into(avatar);
    }


}
