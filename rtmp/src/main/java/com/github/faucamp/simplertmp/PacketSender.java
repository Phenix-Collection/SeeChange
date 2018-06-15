package com.github.faucamp.simplertmp;

import android.os.Environment;
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
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PacketSender {

    private static String privateKey = "";

    private static String publicKey = "";

    private static PacketSender instance;

    private Mac mac;
    private Key key;
    private Socket socket;

    private boolean keySend = false;

    protected PacketSender() {
        try {
            readCertificate();
            readPrivateKey();
            key = new SecretKeySpec("SUPERSECRETHASHTHING".getBytes(), "HmacSHA256");
            mac = Mac.getInstance(key.getAlgorithm());
            mac.init(key);
            socket = IO.socket("http://188.166.127.54:3000");
            socket.connect();
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

            System.out.println("CERTIFICATE");
            System.out.println(cert);

            //send certificate data as JSON object
            socket.emit("certificate", encrypt(cert.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void readCertificate(){

        Certificate caCert;
        //certificate reading out sd storage
        File certificateFile = new File(Environment.getExternalStorageDirectory().toString() + "/Certificate/client.crt");

        try {
            InputStream is = new FileInputStream(certificateFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caCert = cf.generateCertificate(is);
            publicKey = new String(Base64.encode(caCert.getPublicKey().getEncoded(), 0));
            System.out.println();
            System.out.println("Public Key = ");
            System.out.println(publicKey);
        } catch (FileNotFoundException | CertificateException e) {
            e.printStackTrace();
        }
    }

    public void readPrivateKey() {

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
        privateKey = text.toString();
        privateKey = privateKey.replace("-----BEGIN RSA PRIVATE KEY-----","");
        privateKey = privateKey.replace("-----END RSA PRIVATE KEY-----","");
        System.out.println();
        System.out.println("Private key : ");
        System.out.println(privateKey);
    }


    public static PacketSender getInstance() throws Exception {
        if (instance == null) {
            instance = new PacketSender();
        }
        return instance;
    }

    public void startSending(byte[] newPacket, RtmpPacket packet) {
        if(socket.connected()) {
            final byte[] hash = mac.doFinal(newPacket);
            sendToServer(toHexString(hash), packet);
        }
    }

    private void sendPublicKey(String key) {
        socket.emit("publickey", key);
        keySend = true;
    }

    private void sendToServer(String hash, RtmpPacket packet) {
        if(!keySend) {
            sendPublicKey(publicKey);
            sendCertificate();
        }
        socket.emit("packet", encrypt("{" +
                " \"hash\": \"" + hash +
                "\", \"messageType\": \"" + packet.getHeader().getMessageType() +
                "\", \"absoluteMadTime\": " + packet.getHeader().getAbsoluteTimestamp()+
                "}"));
    }

    public void stoppedStreaming() {
        keySend = false;
    }

    private String encrypt(String data)
    {
        String encoded = "";
        byte[] encrypted;
        try {
            byte[] privateBytes = Base64.decode(privateKey, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            encrypted = cipher.doFinal(data.getBytes());
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