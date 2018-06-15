package com.github.faucamp.simplertmp;

import android.os.Build;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.github.faucamp.simplertmp.packets.RtmpPacket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Formatter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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
            ReadCertificate();
            ReadPrivateKey();
            key = new SecretKeySpec("SUPERSECRETHASHTHING".getBytes(), "HmacSHA256");
            mac = Mac.getInstance(key.getAlgorithm());
            mac.init(key);
            socket = IO.socket("http://188.166.127.54:3000");
            socket.connect();
            sendPublicKey(PUBLICKEY);

        } catch (NoSuchAlgorithmException e) {

        } catch (InvalidKeyException e) {

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReadCertificate(){

        Certificate caCert;
        //certificate reading out sd storage
        File certificateFile = new File(Environment.getExternalStorageDirectory().toString() + "/Certificate/client.crt");

        try {
            InputStream is = new FileInputStream(certificateFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caCert = (X509Certificate)cf.generateCertificate(is);
            PUBLICKEY = caCert.getPublicKey().toString();

            String[] separated = PUBLICKEY.split("="); // OpenSSLRSAPublicKey{modulus=PUBLICKEY,publicExponent=10001}
            String[] separated2 = separated[1].split(","); //PUBLICKEY,publicExponent
            PUBLICKEY = separated2[0]; //PUBLICKEY
            System.out.println();
            System.out.println("Public Key = ");
            System.out.println(PUBLICKEY);
        } catch (FileNotFoundException | CertificateException e) {
            e.printStackTrace();
        }
    }

    public void ReadPrivateKey() {

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
        PRIVATEKEY = text.toString();
        PRIVATEKEY.replace("-----BEGIN RSA PRIVATE KEY-----","");
        PRIVATEKEY.replace("-----END RSA PRIVATE KEY-----","");
        System.out.println();
        System.out.println("Private key : ");
        System.out.println(PRIVATEKEY);
    }


    public static PacketSender getInstance() throws Exception {
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

    static String encrypt(byte[] data)
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
            encoded = toEncryptedHexString(encrypted);
            return encoded;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }


    private static String toEncryptedHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }
}
