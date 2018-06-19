package com.thecirkel.seechange.services;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URI;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateService {

    X509Certificate caCert;
    //get file
    File certificateFile = new File(Environment.getExternalStorageDirectory().toString() + "/Certificate/client.crt");

    public String getStreamerName() {
        return streamerName;
    }

    public String getShortbio() {
        return shortbio;
    }

    public String getStreamkey() {
        return streamkey;
    }

    public String getAvatarsource() {
        return avatarsource;
    }

    private String streamerName = "";
    private String shortbio = "";
    private String streamkey = "";
    private String avatarsource ;


    public CertificateService(){
        try {
            // Load cert
            InputStream is = new FileInputStream(certificateFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            caCert = (X509Certificate)cf.generateCertificate(is);
            caCert.getSubjectX500Principal();
            String alias = caCert.getSubjectX500Principal().toString();
            String[] split = alias.split(",");
            for (String x : split) {
                if (x.contains("OID.1.2.3.7=")) {
                    streamkey = x.split("=")[1];
                }
                if(x.contains("OID.1.2.3.6=")){
                    avatarsource = x.split("=")[1];
                }
                if(x.contains("OID.1.2.3.5=")){
                    shortbio = x.split("=")[1];
                }
                if(x.contains("OID.1.2.3.4=")){
                    streamerName = x.split("=")[1];
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
