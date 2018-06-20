package com.thecirkel.seechange.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thecirkel.seechange.R;
import com.thecirkel.seechange.services.CertificateService;

public class InfoActivity extends AppCompatActivity {

    private CertificateService certificateService;
    private ImageView closeInfo,avatar;
    private TextView name,bio,satoshiAmount;
    private Integer testSatoshi = 12345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        certificateService = new CertificateService();

        avatar = findViewById(R.id.avatar);
        Picasso.get().load(certificateService.getAvatarsource()).into(avatar);

        name = findViewById(R.id.name);
        name.setText(certificateService.getStreamerName());

        bio = findViewById(R.id.shortBio);
        bio.setText(certificateService.getShortbio());

        satoshiAmount = findViewById(R.id.satoshiAmount);
        satoshiAmount.setText( "" + testSatoshi + " ㋛");

        closeInfo = findViewById(R.id.closeInfoButton);
        closeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
