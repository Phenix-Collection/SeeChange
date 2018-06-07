package com.thecirkel.seechange;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Connector extends AsyncTask<Void, byte[], Boolean> {
    Socket socket; //Network Socket
    InputStream inputStream; //Network Input Stream
    OutputStream outputStream; //Network Output Stream

    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = false;

        try{
            Log.i("AsyncTask", "DoInBackground: Creating Socket");
            SocketAddress socketAddress = new InetSocketAddress("192.168..1.1", 80);

            socket = new Socket();
            socket.connect(socketAddress, 5000);
            if(socket.isConnected()) {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                byte[] buffer = new byte[4096];
                int read = inputStream.read(buffer, 0, 4096);
                while(read != -1) {
                    byte[] tempdata = new byte[read];
                    System.arraycopy(buffer, 0, tempdata, 0, read);
                    publishProgress(tempdata);
                    Log.i("Async", "DoInBackground: Received data");
                    read = inputStream.read(buffer, 0, 4096);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: IOException");
            result = true;
        } catch( Exception e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: Exception");
            result = true;
        }
        Log.i("AsyncTask", "doInBackground: Finished");
        return result;
    }
}
