package com.thecirkel.seechange.services;

import android.os.AsyncTask;
import android.util.Log;

import com.thecirkel.seechangemodels.models.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UserDataGetTask extends AsyncTask<String, Void, String> {

    private OnUserDataAvailable ouda;

    private final String tag = getClass().getSimpleName();

    public UserDataGetTask(OnUserDataAvailable ouda) {
        this.ouda = ouda;
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream;
        int responseCode;
        String userdataUrl = params[0];
        String response = "";

        try {
            URL url = new URL(userdataUrl);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("GET");

            httpConnection.connect();

            responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            } else {
                Log.e("", "Error, invalid response");
            }
        } catch (MalformedURLException e) {
            Log.e("", "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e("", "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        JSONObject jsonObject;

        Log.i(tag, "onPostExecute " + response);

        if (response == null || response == "") {
            Log.e(tag, "onPostExecute: response is empty!");
            return;
        }

        try {
            jsonObject = new JSONObject(response);
            UserData ud = new UserData();

            String name = jsonObject.getString("name");
            String avatarurl = jsonObject.getString("avatar_source");
            String bio = jsonObject.getString("short_bio");
            Integer satoshi = jsonObject.getInt("satoshi");

            ud.setUsername(name);
            ud.setAvatarurl(avatarurl);
            ud.setBio(bio);
            ud.setSatoshi(satoshi);

            ouda.onUserDataAvailable(ud);
        } catch (JSONException ex) {
            Log.e(tag, "onPostExecute JSONException " + ex.getLocalizedMessage());
        }
    }

    public interface OnUserDataAvailable {
        void onUserDataAvailable(UserData userData);
    }

    public static String getStringFromInputStream(InputStream is) {
        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
        } catch (IOException e) {
            Log.e("", "getStringFromInputStream " + e.getLocalizedMessage());
        }

        return sb.toString();
    }
}