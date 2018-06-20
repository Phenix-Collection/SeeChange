package com.thecirkel.seechange;

import com.thecirkel.seechange.services.ChatApplication;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.socket.client.Socket;

public class ChatApplicationTest {
    ChatApplication chatApplication = new ChatApplication();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getSocketIsNotNull() {
        Socket testSocket = chatApplication.getSocket();
        Assert.assertNotNull("Socket is not null.", chatApplication.getSocket());
//        Assert.assertTrue(testSocket.connected());
    }

    @After
    public void tearDown() throws Exception {
    }
}
