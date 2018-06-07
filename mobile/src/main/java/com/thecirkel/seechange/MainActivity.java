package com.thecirkel.seechange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity {

    private static final String NUMBER_KEY = "com.seechange.key.number";
    private DataClient dataClient;
    private int number = 0;

    private TextView numberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberView = findViewById(R.id.number);
        numberView.setText(number + "");

        dataClient = Wearable.getDataClient(this);
    }

    public void plusOne(View v) {
        this.number++;
        this.numberView.setText(number + "");

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/number");
        putDataMapRequest.getDataMap().putInt(NUMBER_KEY, number);

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        putDataRequest.setUrgent();
        dataClient.putDataItem(putDataRequest);
    }
}
