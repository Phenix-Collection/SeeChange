package com.thecirkel.seechange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button connectBtn;
    private TextView statusHeaderText, statusText;
    private Connector connector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBtn = (Button) findViewById(R.id.connectBtn);
            connectBtn.setOnClickListener(connectBtnListener);
        statusHeaderText = findViewById(R.id.statusHeaderText);
        statusText = findViewById(R.id.statusText);
    }

    private View.OnClickListener connectBtnListener = new View.OnClickListener(){
        public void onClick(View v){
//            connectBtn.setVisibility(View.INVISIBLE);
            connector = new Connector(); //New instance of NetworkTask
            connector.execute();
            statusText.setText("Verbinden...");
        }
    };

}
