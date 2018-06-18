package com.github.faucamp.simplertmp;

import android.os.Build;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.github.faucamp.simplertmp.packets.RtmpPacket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Formatter;


import javax.crypto.Cipher;

import javax.crypto.Mac;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;

import javax.net.ssl.SSLSession;


public class PacketSender {

    private static String PRIVATEKEY = "MIICXAIBAAKBgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUp" +
            "wmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ5" +
            "1s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQABAoGAFijko56+qGyN8M0RVyaRAXz++xTqHBLh" +
            "3tx4VgMtrQ+WEgCjhoTwo23KMBAuJGSYnRmoBZM3lMfTKevIkAidPExvYCdm5dYq3XToLkkLv5L2" +
            "pIIVOFMDG+KESnAFV7l2c+cnzRMW0+b6f8mR1CJzZuxVLL6Q02fvLi55/mbSYxECQQDeAw6fiIQX" +
            "GukBI4eMZZt4nscy2o12KyYner3VpoeE+Np2q+Z3pvAMd/aNzQ/W9WaI+NRfcxUJrmfPwIGm63il" +
            "AkEAxCL5HQb2bQr4ByorcMWm/hEP2MZzROV73yF41hPsRC9m66KrheO9HPTJuo3/9s5p+sqGxOlF" +
            "L0NDt4SkosjgGwJAFklyR1uZ/wPJjj611cdBcztlPdqoxssQGnh85BzCj/u3WqBpE2vjvyyvyI5k" +
            "X6zk7S0ljKtt2jny2+00VsBerQJBAJGC1Mg5Oydo5NwD6BiROrPxGo2bpTbu/fhrT8ebHkTz2epl" +
            "U9VQQSQzY1oZMVX8i1m5WUTLPz2yLJIBQVdXqhMCQBGoiuSoSjafUhV7i1cEGpb88h5NBYZzWXGZ" +
            "37sJ5QsW+sJyoNde3xH8vdXhzU7eT82D6X/scw9RZz+/6rCJ4p0=";

    private static String PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0" +
            "FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/" +
            "3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQAB";

    private static PacketSender instance;

    private Mac mac;
    private Key key;
    private Socket socket;

    protected PacketSender() {
        try {
            PUBLICKEY = ReadCertificatePublicKey();
            PRIVATEKEY = ReadPrivateKey();
            key = new SecretKeySpec("SUPERSECRETHASHTHING".getBytes(), "HmacSHA256");
            mac = Mac.getInstance(key.getAlgorithm());
            mac.init(key);
            socket = IO.socket("http://188.166.127.54:6969");
            socket.connect();
            sendCertificate();
            sendPublicKey(PUBLICKEY);


        } catch (NoSuchAlgorithmException e) {

        } catch (InvalidKeyException e) {

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendCertificate() {

        X509Certificate caCert;
        //get file
        File certificateFile = new File(Environment.getExternalStorageDirectory().toString() + "/Certificate/client.crt");

        try {
            // Load cert
            InputStream is = new FileInputStream(certificateFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            caCert = (X509Certificate)cf.generateCertificate(is);
            caCert.getSubjectX500Principal();
            String alias = caCert.getSubjectX500Principal().toString();
            String streamKey = null;
            String avatarSource = null;
            String bio = null;
            String name = null;
            String[] split = alias.split(",");
            for (String x : split) {
                if (x.contains("OID.1.2.3.7=")) {
                    streamKey = x.split("=")[1];
                }
                if(x.contains("OID.1.2.3.6=")){
                    avatarSource = x.split("=")[1];
                }
                if(x.contains("OID.1.2.3.5=")){
                    bio = x.split("=")[1];
                }
                if(x.contains("OID.1.2.3.4=")){
                    name = x.split("=")[1];
                }
            }

            JSONObject cert = new JSONObject();
            cert.put("name",name);
            cert.put("short_bio",bio);
            cert.put("stream_key",streamKey);
            cert.put("avatar_source",avatarSource);

            //send certificate data as JSON object
            socket.emit("certificate",cert);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class to verify hostname --> Allow all hostname's
     */
    public static class RelaxedHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public String ReadCertificatePublicKey(){

        Certificate caCert;
        String publicKey = "";
        //certificate reading out sd storage
        File certificateFile = new File(Environment.getExternalStorageDirectory().toString() + "/Certificate/client.crt");

        try {
            InputStream is = new FileInputStream(certificateFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caCert = cf.generateCertificate(is);
            publicKey = caCert.getPublicKey().toString();

            String[] separated = publicKey.split("="); // OpenSSLRSAPublicKey{modulus=PUBLICKEY,publicExponent=10001}
            String[] separated2 = separated[1].split(","); //PUBLICKEY,publicExponent
            publicKey = separated2[0]; //PUBLICKEY
        } catch (FileNotFoundException | CertificateException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public String ReadPrivateKey() {

        File Privatekeyfile = new File(Environment.getExternalStorageDirectory().toString() + "/Certificate/Private.key");
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(Privatekeyfile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {

        }
        String privatekey = text.toString();
        privatekey.replace("-----BEGIN RSA PRIVATE KEY-----","");
        privatekey.replace("-----END RSA PRIVATE KEY-----","");
        return privatekey;
    }


    public static PacketSender getInstance() {
        if (instance == null) {
            instance = new PacketSender();
        }
        return instance;
    }

    public void startSending(byte[] newPacket, RtmpPacket packet) {
        final byte[] hash = mac.doFinal(newPacket);
        sendToServer(encrypt(hash), packet);
    }

    private void sendPublicKey(String key) {
        socket.emit("publickey", key);
    }

    private void sendToServer(String digitalSignature, RtmpPacket packet) {
        socket.emit("packet", "{" +
                " \"digitalSignature\": \"" + digitalSignature +
                "\", \"messageType\": \"" + packet.getHeader().getMessageType() +
                "\", \"absoluteMadTime\": " + packet.getHeader().getAbsoluteTimestamp()+
                "}");
    }

    private String encrypt(byte[] data)
    {
        String encoded = "";
        byte[] encrypted;
        try {
            byte[] privateBytes = Base64.decode(PRIVATEKEY, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            encrypted = cipher.doFinal(data);
            encoded = toHexString(encrypted);
            return encoded;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }

    protected String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }
}
