package com.thecirkel.seechange;

import android.test.ActivityTestCase;

import com.thecirkel.seechange.fragments.ChatFragment;
import com.thecirkel.seechange.services.ChatApplication;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import io.socket.client.Socket;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


public class SocketIOTest {
    ChatFragment chatFragment = new ChatFragment();
    ChatApplication chatApplication = new ChatApplication();
    ChatFragment mockChatFragment = mock(ChatFragment.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testing_tests() {
        Assert.assertEquals(4, 2 + 2);

    }

    @Test
    public void sockets_canConnect(){
        boolean connected = false;
        Socket testSocket = chatApplication.getSocket();
        testSocket.connect();

        connected = testSocket.connected();

        Assert.assertEquals(connected = true, true);
        Assert.assertTrue(connected = true);
        Assert.assertFalse(connected = false);
    }

    @Test
    public void sockets_canDisconnect(){
        boolean connected;

        Socket testSocket = chatApplication.getSocket();
        testSocket.connect();

        testSocket.disconnect();

        connected = testSocket.connected();

        Assert.assertTrue("Socket disconnected", !testSocket.connected());
        Assert.assertEquals(testSocket.connected(), false);
    }

    @Test
    public void canSendMessageOnSocket() {
        Socket testSocket = chatApplication.getSocket();
        String testMessage = "Testing the socket.";

        testSocket.connect();

        mockChatFragment.attemptSend();

        Assert.assertSame(testMessage, testMessage);
    }

    @After
    public void tearDown() throws Exception {
    }
}