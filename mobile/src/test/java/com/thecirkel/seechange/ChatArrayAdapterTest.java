package com.thecirkel.seechange;


import android.content.Context;
import android.content.Intent;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thecirkel.seechange.adapters.ChatArrayAdapter;
import com.thecirkel.seechangemodels.models.ChatMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ChatArrayAdapterTest {
//    ChatArrayAdapter mockChatArrayAdapter = mock(ChatArrayAdapter.class);
    private Context context;
    private List<ChatMessage> messageList = new ArrayList<>();
    private ChatMessage testMessage1 = new ChatMessage("Testing 1-2-3", "Abel", "2018/06/18 09:33:00");
    private ChatMessage testMessage2 = new ChatMessage("Testing 4-5-6", "Bort", "2018/06/18 10:10:00");
    private ChatArrayAdapter mockChatArrayAdapter = new ChatArrayAdapter(context, messageList);

    public ChatArrayAdapterTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
    }

//    @Test
//    public void adapterCreatesAView() {
//        View testView = mockChatArrayAdapter.getView(0, null, null);
//
////        TextView userName = testView.findViewById(R.id.chatUser);
////        TextView timestamp = testView.findViewById(R.id.chatTimestamp);
////        TextView message = testView.findViewById(R.id.chatMessage);
//
//        Assert.assertNotNull("View is null.", testView);
//    }

    @Test
    public void countMessagesInList() {
        messageList.add(testMessage1);
        messageList.add(testMessage2);

        Assert.assertFalse(messageList.size() == 1);
        Assert.assertFalse(messageList.size() == 3);
        Assert.assertTrue(messageList.size() == 2);
        Assert.assertEquals( 2, messageList.size());
    }

    @Test
    public void GetItemId() {
        Assert.assertEquals("If ID = Position, ", 0, mockChatArrayAdapter.getItemId(0));
        Assert.assertFalse("Position and ID are not equal", mockChatArrayAdapter.getItemId(0) == 1);
        Assert.assertTrue("Position and ID are equal", mockChatArrayAdapter.getItemId(1) == 1);
    }

    @Test
    public void returnsItemByPosition() {
        int pos = 2;
        int newPos = (int) mockChatArrayAdapter.getItemId(1);
        pos = newPos;

        Assert.assertFalse(newPos == 2);
        Assert.assertTrue(newPos == 1);
        Assert.assertEquals(1, 1 );
    }

    @Test
    public void canVerifyMessageData() {
        Assert.assertEquals("Abel expected", "Abel", testMessage1.getUsername());
        Assert.assertFalse(testMessage1.getMessage().equals("Testing for false results"));
        Assert.assertTrue(testMessage2.getTimestamp().equals("2018/06/18 10:10:00"));
    }


    @After
    public void tearDown() throws Exception {
    }
}
