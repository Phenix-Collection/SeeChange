package com.github.faucamp.simplertmp;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PacketSenderTest {

    private PacketSender packetSender;

    @Before
    public void setUp() {
        packetSender = PacketSender.getInstance();
    }

    @Test
    public void there_is_on_instance() {
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
}