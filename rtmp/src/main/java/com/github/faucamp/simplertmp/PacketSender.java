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

    private static String PRIVATEKEY =
            //"-----BEGIN RSA PRIVATE KEY-----" +
            "MIIEowIBAAKCAQEAu/TGs6MXXHCfCLfrP6KuQSyjPO6RHajxEEKeLGSsNdJKQ8k1" +
                    "NW9qRa5QNMvNu/m3ezjtqIN9Evy+puHuzPGCX99R+OSbEFV2tLeaTwjpQCDTBGBW" +
                    "qby5HlxXuxU3lPo6VY07Z1uJiv50jClg1kCscSLhh/fI8IYsdfCGlFKmNEiarMdH" +
                    "NdYscTgZ86gPnPEWAHQFBXT/XYNR8Wtzwk0W9Le+m2eFHuXFOxNG/Y6gwBze4gJx" +
                    "vFaq9BP4o8I1e4MD9Vcn+qA3T5iBRPtCEB5JGjfpQKHT/2sVl0z1YfBGi4okyfBf" +
                    "/tdZvqjR44yLv0hEtq2DDu75xbrjkmUR0fN04QIDAQABAoIBAA6T5pFqNdaimYMY" +
                    "mLNfz7oYVzTToAe34bKMKC5zRHQMMlxdj7XsRUbQRUJDCFrq/FMfUDeAs0O0vKi+" +
                    "30Gf/aen73ipamly53kOwHYez9B8e0Fco3wYuhbjvJ4zABOa8MlP4eqbS+iu4aU5" +
                    "VFxpubYwqWNdUYVuMqaXGCkK2/8z/Ceh6Iv/F7tqEOEwwaTuoBWjb9y7Ew7mFIm8" +
                    "YXG8VYcS6GOrJv3EUrnefxJ2a26g29uejn3KgWxZNJ5jFlvAdB9xcPGYe1cYhmTD" +
                    "7wGjtmiYOT3jjfM6OJbxmZrLm+w4iZqCeScU1mD095en5cnaryLXOtG6FB7azS+k" +
                    "UutqV60CgYEA9NpEFajDFWPNo9hhjv8hRkAz01yLVPepZ1d7kC/2WwEA3TpGz1F0" +
                    "JZ5eFDxkspY8SOSCzk02eUcxbDP13spNBouWGIZOVbWNUjXVNdH9BHR/YnwGz3l2" +
                    "qlAWg915qH9E3VkKBtICvGRT6s+igKsrpbBnm+H1d6Y8xlLSzUWxBS8CgYEAxINj" +
                    "NBL1yrm4nIV2AQKGrqYJBS4UCEcSZxw7k7BEIvnjIIG8gMX5YUB67hxMO8Wja2ia" +
                    "EuwhFv1ioDMsv4H6nfqFqWpGp0BNSXxSOn+RH8P1LEIfbsB4FGRdJUv+nUAsNOea" +
                    "kU2RTiQFmiQBinYliT2nrTV9xZBP0+Vk6XPewu8CgYBmLL7EmwvtXRxvBtiPJ3/n" +
                    "JNt7k97AsiiK60KwxJL1HtIRf0QVN1RUbmWr5BfRPkgh0tmS5T2aFk9Va8lqEtlZ" +
                    "pd4YldMSiRRT/grezqXauhJ+MmtVIMaYA5uho0YlPhaql4FLn09s9iDel46kSsmH" +
                    "9rkFn6EjMMejquQ0rKRWGQKBgCZzKrrqpssGsbd2aZNjAiUz+XnY/TRta41fOcz4" +
                    "5SwOxsD6gX6UvemoayUhxky/q+z9J4BiUUslQuHjgXLrJLU6amKul6pr63Ngbtph" +
                    "UWzss5D4Uxwhbp1W0d7VUrlD8CJd2qFpku++HpZ9SwvjeA61UJSUbcp1JRlpvefr" +
                    "sv9jAoGBANFg8YdnaowlmXqCeT0sKspLsG6qg1wmB2SyUPGjFNUgn3VxY6ANk+ji" +
                    "r5eFZev3E+A7X4ce+dYvEUhjJdpt3URXHLg9M2sdRK45BudiPo+8jK7B55xzk4yZ" +
                    "idFTJbPFh61asYuL5NFoN9k5AV6/j9FS03RKFYflFryeQZVYjw1X";
    //"-----END RSA PRIVATE KEY-----";

    private static String PUBLICKEY =
            //"-----BEGIN PUBLIC KEY-----" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu/TGs6MXXHCfCLfrP6Ku" +
                    "QSyjPO6RHajxEEKeLGSsNdJKQ8k1NW9qRa5QNMvNu/m3ezjtqIN9Evy+puHuzPGC" +
                    "X99R+OSbEFV2tLeaTwjpQCDTBGBWqby5HlxXuxU3lPo6VY07Z1uJiv50jClg1kCs" +
                    "cSLhh/fI8IYsdfCGlFKmNEiarMdHNdYscTgZ86gPnPEWAHQFBXT/XYNR8Wtzwk0W" +
                    "9Le+m2eFHuXFOxNG/Y6gwBze4gJxvFaq9BP4o8I1e4MD9Vcn+qA3T5iBRPtCEB5J" +
                    "GjfpQKHT/2sVl0z1YfBGi4okyfBf/tdZvqjR44yLv0hEtq2DDu75xbrjkmUR0fN0" +
                    "4QIDAQAB";
    //"-----END PUBLIC KEY-----";

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