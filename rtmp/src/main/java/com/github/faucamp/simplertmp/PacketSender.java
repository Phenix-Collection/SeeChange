package com.github.faucamp.simplertmp;

import android.util.Base64;

import com.github.faucamp.simplertmp.packets.RtmpPacket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.crypto.Mac;
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

    private boolean keySend = false;

    protected PacketSender() {
        try {
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
        }
    }

    public static PacketSender getInstance() {
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
            sendPublicKey(PUBLICKEY);
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
            byte[] privateBytes = Base64.decode(PRIVATEKEY, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA");
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
