package com.thecirkel.seechange;

import com.thecirkel.seechange.services.ChatService;
import com.thecirkel.seechangemodels.models.ChatMessage;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ChatServiceTest {

    @Test
    public void there_is_on_instance() {
        ChatService chatService1 = ChatService.getInstance(null);
        ChatService chatService2 = ChatService.getInstance(null);

        assertEquals(chatService1, chatService2);
    }

    @Test
    public void returns_list_of_ChatMessages() {
        ChatService chatService = ChatService.getInstance(null);
        List<ChatMessage> chatMessages = chatService.getMessages();
        chatMessages.clear();

        chatMessages.add(new ChatMessage("Test", "Test"));
        assertEquals(1,chatMessages.size() );
        assertEquals(chatMessages.get(0).getClass(), new ChatMessage("Nieuw", "Nieuw").getClass());
    }
}
