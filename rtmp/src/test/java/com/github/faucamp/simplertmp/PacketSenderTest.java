package com.github.faucamp.simplertmp;

import android.os.Environment;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.*;

public class PacketSenderTest {

    private PacketSender packetSender;

    @Before
    public void setUp() throws Exception {
        packetSender = PacketSender.getInstance();
    }

    @Test
    public void there_is_on_instance() throws Exception {

        PacketSender packetSender1 = PacketSender.getInstance();
        PacketSender packetSender2 = PacketSender.getInstance();

        assertEquals(packetSender1, packetSender2);
    }

    @Test
    public void toEncryptedHexString_returns_string() {
        String original = "test";
        String hexString = packetSender.toHexString(original.getBytes());

        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }

        String compareString = new String(data);

        assertTrue(original.equals(compareString));
    }

    @Test
    public void privateKey_is_filtered(){
        String privkey = packetSender.ReadPrivateKey("../java/com/github/faucamp/simplertmp/testfiles/testPrivate.key");

        assertFalse(privkey.contains("-----BEGIN RSA PRIVATE KEY-----"));
        assertFalse(privkey.contains("-----END RSA PRIVATE KEY-----"));

    }

    @Test
    public void publicKey_is_filtered(){
        String publickey = packetSender.ReadCertificatePublicKey("../java/com/github/faucamp/simplertmp/testfiles/testCertificate.crt");

        assertFalse(publickey.contains("OpenSSLRSAPublicKey{modulus="));
        assertFalse(publickey.contains("publicExponent"));
    }

}