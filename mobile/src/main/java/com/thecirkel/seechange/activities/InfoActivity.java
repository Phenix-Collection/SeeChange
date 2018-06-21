package com.thecirkel.seechange.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thecirkel.seechange.R;
import com.thecirkel.seechange.services.CertificateService;
import com.thecirkel.seechange.services.UserDataGetTask;
import com.thecirkel.seechangemodels.models.UserData;

public class InfoActivity extends AppCompatActivity implements UserDataGetTask.OnUserDataAvailable{
    private CertificateService certificateService;
    private ImageView closeInfo,avatar;
    private String streamerkey;
    private TextView name,bio,satoshiAmount;
    private Integer testSatoshi = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        certificateService = new CertificateService();
        streamerkey = certificateService.getStreamkey();

        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        bio = findViewById(R.id.shortBio);
        satoshiAmount = findViewById(R.id.satoshiAmount);

        closeInfo = findViewById(R.id.closeInfoButton);
        closeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserDataOutCertificate();
        //getUserData("http://188.166.127.54:5555/api/getStreamer/" + streamerkey);

    }

    public void getUserDataOutCertificate(){
        name.setText(certificateService.getStreamerName());
        bio.setText(certificateService.getShortbio());
        Picasso.get().load(certificateService.getAvatarsource()).into(avatar);
    }


    public void getUserData(String apiUrl) {
        String[] urls = new String[]{apiUrl};

        UserDataGetTask getdata = new UserDataGetTask(this);
        getdata.execute(urls);
    }

    @Override
    public void onUserDataAvailable(UserData userData) {
        name.setText(userData.getUsername());
        bio.setText(userData.getBio());
        Picasso.get().load(userData.getAvatarurl()).into(avatar);
        satoshiAmount.setText(userData.getSatoshi());
    }
}
