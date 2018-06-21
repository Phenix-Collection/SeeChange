package com.thecirkel.seechange.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SatoshiService {

Context context;
CertificateService certificateService;

    public SatoshiService(Context c){
        context = c;
        certificateService = new CertificateService();
    }


    public void postSatoshi(int amount){

        String body = "{" +
                "'streamkey' : " + "'" + certificateService.getStreamkey() + "'" +
                "'satoshi' : " + "'"+ amount +"'," +
                "}";

        try {
            JSONObject jsonBody = new JSONObject(body);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://167.99.42.8:5555/api/satoshi", jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Satoshi service : " , response.toString());
                            // satoshi is added to user wallet
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Error - send back to caller
                            Log.i("Satoshi service : " , error.toString());
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Access the RequestQueue through your singleton class.
            VolleyRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
        } catch(JSONException e) {
            Log.e("Satoshi service : ", e.getMessage());
        }
    }

}
