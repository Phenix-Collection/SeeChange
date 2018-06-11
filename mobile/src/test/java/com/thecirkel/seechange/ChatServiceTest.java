package com.thecirkel.seechange;

import com.thecirkel.seechange.services.ChatService;

import org.junit.Test;
import static org.junit.Assert.*;
import android.test.mock.MockContext;

public class ChatServiceTest {

    @Test
    public void there_is_on_instance() {
        ChatService chatService1 = ChatService.getInstance(new MockContext());
        ChatService chatService2 = ChatService.getInstance(new MockContext());

        assertEquals(chatService1, chatService2);
    }

    @Test
    public void can_start() {
        ChatService chatService = ChatService.getInstance(new MockContext());
        chatService.start();

        assertTrue(chatService.isRunning());
    }

    @Test
    public void can_stop() {
        ChatService chatService = ChatService.getInstance(new MockContext());
        chatService.start();
        chatService.stop();

        assertFalse(chatService.isRunning());
    }
}
